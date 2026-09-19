package com.mrashish18.lifeos.core.resilience.transport

import com.mrashish18.lifeos.core.model.EmergencyMessage
import com.mrashish18.lifeos.core.model.MessageStatus
import com.mrashish18.lifeos.core.model.NetworkState
import com.mrashish18.lifeos.core.model.TransportType
import com.mrashish18.lifeos.core.resilience.RescueMeshEngine
import com.mrashish18.lifeos.domain.repository.EmergencyMessageRepository

class LocalStoreAndForwardTransport(
    private val repository: EmergencyMessageRepository,
    private val engine: RescueMeshEngine
) : MeshTransport {

    override val transportName: String = "LOCAL_STORE_AND_FORWARD"
    override fun isAvailable(networkState: NetworkState): Boolean = true

    override suspend fun transmit(
        message: EmergencyMessage,
        networkState: NetworkState
    ): TransportResult {
        return try {
            val queuedMessage = message.copy(
                status = MessageStatus.QUEUED,
                transportType = TransportType.LOCAL_ONLY
            )
            repository.insertMessage(queuedMessage)
            TransportResult.QueuedLocally(queuedMessage)
        } catch (e: Exception) {
            TransportResult.Failed(message, e.message ?: "Failed to queue locally")
        }
    }
}
