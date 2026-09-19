package com.mrashish18.lifeos.domain.usecase

import com.mrashish18.lifeos.core.model.BehaviorEvent
import com.mrashish18.lifeos.core.model.BehaviorEventType
import com.mrashish18.lifeos.core.model.EmergencyMessage
import com.mrashish18.lifeos.core.model.MessagePriority
import com.mrashish18.lifeos.core.model.MessageStatus
import com.mrashish18.lifeos.core.model.MessageType
import com.mrashish18.lifeos.core.model.NetworkState
import com.mrashish18.lifeos.core.model.TransportType
import com.mrashish18.lifeos.core.resilience.RescueMeshEngine
import com.mrashish18.lifeos.core.resilience.transport.LocalStoreAndForwardTransport
import com.mrashish18.lifeos.core.resilience.transport.NetworkGatewayTransport
import com.mrashish18.lifeos.core.resilience.transport.TransportResult
import com.mrashish18.lifeos.domain.repository.BehaviorEventRepository
import com.mrashish18.lifeos.domain.repository.EmergencyMessageRepository
import kotlinx.coroutines.flow.Flow
import java.time.Instant
import java.util.UUID

data class SyncResult(
    val processedCount: Int,
    val syncedCount: Int,
    val expiredCount: Int,
    val failedCount: Int,
    val explanation: String
)

class CreateEmergencyMessageUseCase(
    private val emergencyRepository: EmergencyMessageRepository,
    private val engine: RescueMeshEngine,
    private val behaviorEventRepository: BehaviorEventRepository,
    private val networkTransport: NetworkGatewayTransport,
    private val localTransport: LocalStoreAndForwardTransport,
    private val clock: java.time.Clock = java.time.Clock.systemUTC()
) {
    suspend operator fun invoke(
        senderId: String,
        payload: String,
        type: MessageType = MessageType.EMERGENCY,
        priority: MessagePriority = MessagePriority.NORMAL,
        recipientId: String? = null,
        networkState: NetworkState = NetworkState.UNKNOWN
    ): Result<EmergencyMessage> {
        return try {
            val message = engine.createMessage(
                senderId = senderId,
                payload = payload,
                type = type,
                priority = priority,
                recipientId = recipientId,
                createdAt = clock.instant()
            )

            // Deduplication check: reject if identical message fingerprint already stored
            if (emergencyRepository.getMessageByFingerprint(message.fingerprintSha256) != null) {
                return Result.failure(IllegalStateException("Duplicate message: exact message fingerprint already exists in repository"))
            }

            emergencyRepository.insertMessage(message)

            behaviorEventRepository.recordEvent(
                BehaviorEvent(
                    id = UUID.randomUUID().toString(),
                    type = BehaviorEventType.EMERGENCY_MESSAGE_CREATED,
                    metadata = mapOf(
                        "messageId" to message.messageId,
                        "type" to message.type.name,
                        "priority" to message.priority.name,
                        "fingerprint" to message.fingerprintSha256.take(8)
                    )
                )
            )

            val finalMessage = if (networkTransport.isAvailable(networkState)) {
                when (val transportResult = networkTransport.transmit(message, networkState)) {
                    is TransportResult.Success -> {
                        behaviorEventRepository.recordEvent(
                            BehaviorEvent(
                                id = UUID.randomUUID().toString(),
                                type = BehaviorEventType.EMERGENCY_MESSAGE_SENT,
                                metadata = mapOf(
                                    "messageId" to message.messageId,
                                    "transport" to TransportType.NETWORK.name
                                )
                            )
                        )
                        transportResult.updatedMessage
                    }
                    else -> {
                        val queued = message.copy(status = MessageStatus.QUEUED)
                        emergencyRepository.updateMessage(queued)
                        behaviorEventRepository.recordEvent(
                            BehaviorEvent(
                                id = UUID.randomUUID().toString(),
                                type = BehaviorEventType.EMERGENCY_MESSAGE_QUEUED,
                                metadata = mapOf("messageId" to message.messageId)
                            )
                        )
                        queued
                    }
                }
            } else {
                localTransport.transmit(message, networkState)
                behaviorEventRepository.recordEvent(
                    BehaviorEvent(
                        id = UUID.randomUUID().toString(),
                        type = BehaviorEventType.EMERGENCY_MESSAGE_QUEUED,
                        metadata = mapOf("messageId" to message.messageId)
                    )
                )
                message.copy(status = MessageStatus.QUEUED, transportType = TransportType.LOCAL_ONLY)
            }

            Result.success(finalMessage)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

class GetEmergencyQueueUseCase(
    private val emergencyRepository: EmergencyMessageRepository
) {
    operator fun invoke(): Flow<List<EmergencyMessage>> {
        return emergencyRepository.observeQueue()
    }

    fun observeAll(): Flow<List<EmergencyMessage>> {
        return emergencyRepository.observeAllMessages()
    }

    fun observeByStatus(status: MessageStatus): Flow<List<EmergencyMessage>> {
        return emergencyRepository.observeMessagesByStatus(status)
    }
}

class RelayEmergencyMessageUseCase(
    private val emergencyRepository: EmergencyMessageRepository,
    private val engine: RescueMeshEngine,
    private val behaviorEventRepository: BehaviorEventRepository,
    private val clock: java.time.Clock = java.time.Clock.systemUTC()
) {
    suspend operator fun invoke(
        messageId: String,
        relayerNodeId: String,
        transport: TransportType = TransportType.LOCAL_ONLY
    ): Result<EmergencyMessage> {
        val message = emergencyRepository.getMessageById(messageId)
            ?: return Result.failure(IllegalArgumentException("Message $messageId not found"))

        val now = clock.instant()
        if (engine.isExpired(message, now)) {
            val expired = message.copy(status = MessageStatus.EXPIRED)
            emergencyRepository.updateMessage(expired)
            behaviorEventRepository.recordEvent(
                BehaviorEvent(
                    id = UUID.randomUUID().toString(),
                    type = BehaviorEventType.EMERGENCY_MESSAGE_EXPIRED,
                    metadata = mapOf("messageId" to messageId)
                )
            )
            return Result.failure(IllegalStateException("Message $messageId has expired and cannot be relayed"))
        }

        if (message.hopCount >= message.maxHops) {
            val failed = message.copy(status = MessageStatus.FAILED)
            emergencyRepository.updateMessage(failed)
            behaviorEventRepository.recordEvent(
                BehaviorEvent(
                    id = UUID.randomUUID().toString(),
                    type = BehaviorEventType.EMERGENCY_MESSAGE_FAILED,
                    metadata = mapOf("messageId" to messageId, "reason" to "hop_limit_exceeded")
                )
            )
            return Result.failure(IllegalStateException("Message $messageId exceeded maximum hop count (${message.maxHops})"))
        }

        if (!engine.validateTransition(message.status, MessageStatus.RELAYING)) {
            return Result.failure(IllegalStateException("Invalid state transition from ${message.status} to RELAYING"))
        }

        val relayResult = engine.processRelay(message, relayerNodeId, transport, now)
        return if (relayResult.isSuccess) {
            val updated = relayResult.getOrThrow()
            emergencyRepository.updateMessage(updated)
            behaviorEventRepository.recordEvent(
                BehaviorEvent(
                    id = UUID.randomUUID().toString(),
                    type = BehaviorEventType.EMERGENCY_MESSAGE_RELAYED,
                    metadata = mapOf(
                        "messageId" to messageId,
                        "hop" to updated.hopCount.toString(),
                        "relayer" to relayerNodeId,
                        "transport" to transport.name
                    )
                )
            )
            Result.success(updated)
        } else {
            Result.failure(relayResult.exceptionOrNull() ?: IllegalStateException("Relay failed"))
        }
    }
}

class ProcessIncomingMessageUseCase(
    private val emergencyRepository: EmergencyMessageRepository,
    private val engine: RescueMeshEngine,
    private val behaviorEventRepository: BehaviorEventRepository,
    private val clock: java.time.Clock = java.time.Clock.systemUTC()
) {
    suspend operator fun invoke(incomingMessage: EmergencyMessage): Result<EmergencyMessage> {
        if (emergencyRepository.hasMessage(incomingMessage.messageId) ||
            emergencyRepository.getMessageByFingerprint(incomingMessage.fingerprintSha256) != null
        ) {
            return Result.failure(IllegalStateException("Duplicate message: ${incomingMessage.messageId} already exists"))
        }

        val now = clock.instant()
        if (engine.isExpired(incomingMessage, now)) {
            val expired = incomingMessage.copy(status = MessageStatus.EXPIRED)
            emergencyRepository.insertMessage(expired)
            return Result.failure(IllegalStateException("Incoming message ${incomingMessage.messageId} is expired"))
        }

        if (incomingMessage.hopCount >= incomingMessage.maxHops) {
            val failed = incomingMessage.copy(status = MessageStatus.FAILED)
            emergencyRepository.insertMessage(failed)
            return Result.failure(IllegalStateException("Incoming message ${incomingMessage.messageId} exceeded hop limit"))
        }

        emergencyRepository.insertMessage(incomingMessage)
        behaviorEventRepository.recordEvent(
            BehaviorEvent(
                id = UUID.randomUUID().toString(),
                type = BehaviorEventType.EMERGENCY_MESSAGE_RECEIVED,
                metadata = mapOf(
                    "messageId" to incomingMessage.messageId,
                    "sender" to incomingMessage.senderId,
                    "priority" to incomingMessage.priority.name,
                    "hops" to incomingMessage.hopCount.toString()
                )
            )
        )
        return Result.success(incomingMessage)
    }
}

class SyncEmergencyQueueUseCase(
    private val emergencyRepository: EmergencyMessageRepository,
    private val networkTransport: NetworkGatewayTransport,
    private val engine: RescueMeshEngine,
    private val behaviorEventRepository: BehaviorEventRepository,
    private val clock: java.time.Clock = java.time.Clock.systemUTC()
) {
    suspend operator fun invoke(networkState: NetworkState): SyncResult {
        if (!networkTransport.isAvailable(networkState)) {
            return SyncResult(
                processedCount = 0,
                syncedCount = 0,
                expiredCount = 0,
                failedCount = 0,
                explanation = "Network unavailable ($networkState). Messages remain securely queued locally."
            )
        }

        val rawQueued = emergencyRepository.getQueuedMessages()
        if (rawQueued.isEmpty()) {
            return SyncResult(
                processedCount = 0,
                syncedCount = 0,
                expiredCount = 0,
                failedCount = 0,
                explanation = "Queue is empty. All emergency messages up to date."
            )
        }

        // Strict priority ordering: CRITICAL first, then HIGH, then NORMAL; tie-breaker oldest createdAt
        val queuedMessages = rawQueued.sortedWith(
            compareBy<EmergencyMessage> {
                when (it.priority) {
                    MessagePriority.CRITICAL -> 0
                    MessagePriority.HIGH -> 1
                    MessagePriority.NORMAL -> 2
                }
            }.thenBy { it.createdAt }
        )

        var synced = 0
        var expired = 0
        var failed = 0
        val now = clock.instant()

        for (message in queuedMessages) {
            if (engine.isExpired(message, now)) {
                val expiredMsg = message.copy(status = MessageStatus.EXPIRED)
                emergencyRepository.updateMessage(expiredMsg)
                behaviorEventRepository.recordEvent(
                    BehaviorEvent(
                        id = UUID.randomUUID().toString(),
                        type = BehaviorEventType.EMERGENCY_MESSAGE_EXPIRED,
                        metadata = mapOf("messageId" to message.messageId)
                    )
                )
                expired++
                continue
            }

            when (val res = networkTransport.transmit(message, networkState)) {
                is TransportResult.Success -> {
                    emergencyRepository.updateMessage(res.updatedMessage)
                    behaviorEventRepository.recordEvent(
                        BehaviorEvent(
                            id = UUID.randomUUID().toString(),
                            type = BehaviorEventType.EMERGENCY_MESSAGE_SENT,
                            metadata = mapOf(
                                "messageId" to message.messageId,
                                "transport" to TransportType.NETWORK.name
                            )
                        )
                    )
                    synced++
                }
                is TransportResult.Failed -> {
                    emergencyRepository.updateMessage(res.message)
                    failed++
                }
                is TransportResult.QueuedLocally -> {
                }
            }
        }

        return SyncResult(
            processedCount = queuedMessages.size,
            syncedCount = synced,
            expiredCount = expired,
            failedCount = failed,
            explanation = "Processed ${queuedMessages.size} messages: $synced transmitted to gateway, $expired expired, $failed failed."
        )
    }
}
