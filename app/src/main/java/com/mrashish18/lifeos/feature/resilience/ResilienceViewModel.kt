package com.mrashish18.lifeos.feature.resilience

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.mrashish18.lifeos.core.context.ContextEngine
import com.mrashish18.lifeos.core.model.EmergencyMessage
import com.mrashish18.lifeos.core.model.MessagePriority
import com.mrashish18.lifeos.core.model.MessageStatus
import com.mrashish18.lifeos.core.model.MessageType
import com.mrashish18.lifeos.core.model.NetworkState
import com.mrashish18.lifeos.core.model.TransportType
import com.mrashish18.lifeos.domain.usecase.CreateEmergencyMessageUseCase
import com.mrashish18.lifeos.domain.usecase.GetEmergencyQueueUseCase
import com.mrashish18.lifeos.domain.usecase.RelayEmergencyMessageUseCase
import com.mrashish18.lifeos.domain.usecase.SyncEmergencyQueueUseCase
import com.mrashish18.lifeos.domain.usecase.SyncResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ResilienceUiState(
    val networkState: NetworkState = NetworkState.UNKNOWN,
    val localNodeId: String = "NODE-7F4A",
    val messages: List<EmergencyMessage> = emptyList(),
    val queuedCount: Int = 0,
    val relayingCount: Int = 0,
    val sentCount: Int = 0,
    val deliveredCount: Int = 0,
    val activeFilter: MessageStatus? = null,
    val selectedMessage: EmergencyMessage? = null,
    val isEmergencyModalOpen: Boolean = false,
    val isSyncing: Boolean = false,
    val lastSyncResult: SyncResult? = null,
    val feedbackMessage: String? = null
) {
    val filteredMessages: List<EmergencyMessage>
        get() = if (activeFilter == null) messages else messages.filter { it.status == activeFilter }

    val hasCriticalEmergency: Boolean
        get() = messages.any { it.priority == MessagePriority.CRITICAL && (it.status == MessageStatus.QUEUED || it.status == MessageStatus.RELAYING) }
}

class ResilienceViewModel(
    private val contextEngine: ContextEngine,
    private val createEmergencyMessageUseCase: CreateEmergencyMessageUseCase,
    private val getEmergencyQueueUseCase: GetEmergencyQueueUseCase,
    private val relayEmergencyMessageUseCase: RelayEmergencyMessageUseCase,
    private val syncEmergencyQueueUseCase: SyncEmergencyQueueUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ResilienceUiState())
    val uiState: StateFlow<ResilienceUiState> = _uiState.asStateFlow()

    init {
        contextEngine.observeSnapshot()
            .onEach { snapshot ->
                _uiState.update { it.copy(networkState = snapshot.networkState) }
            }
            .launchIn(viewModelScope)

        getEmergencyQueueUseCase.observeAll()
            .onEach { allList ->
                _uiState.update { state ->
                    state.copy(
                        messages = allList,
                        queuedCount = allList.count { it.status == MessageStatus.QUEUED },
                        relayingCount = allList.count { it.status == MessageStatus.RELAYING },
                        sentCount = allList.count { it.status == MessageStatus.SENT },
                        deliveredCount = allList.count { it.status == MessageStatus.DELIVERED }
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    fun openEmergencyModal() {
        _uiState.update { it.copy(isEmergencyModalOpen = true) }
    }

    fun closeEmergencyModal() {
        _uiState.update { it.copy(isEmergencyModalOpen = false) }
    }

    fun setFilter(status: MessageStatus?) {
        _uiState.update { it.copy(activeFilter = status) }
    }

    fun selectMessage(message: EmergencyMessage?) {
        _uiState.update { it.copy(selectedMessage = message) }
    }

    fun clearFeedback() {
        _uiState.update { it.copy(feedbackMessage = null) }
    }

    fun sendEmergencyMessage(
        payload: String,
        type: MessageType = MessageType.EMERGENCY,
        priority: MessagePriority = MessagePriority.NORMAL,
        recipientId: String? = null
    ) {
        viewModelScope.launch {
            val currentNetwork = contextEngine.captureSnapshot().networkState
            _uiState.update { it.copy(networkState = currentNetwork) }
            val currentState = _uiState.value
            val result = createEmergencyMessageUseCase(
                senderId = currentState.localNodeId,
                payload = payload,
                type = type,
                priority = priority,
                recipientId = recipientId,
                networkState = currentNetwork
            )

            result.onSuccess { createdMsg ->
                val feedback = when (createdMsg.status) {
                    MessageStatus.SENT -> "Transmitted via network gateway ($currentNetwork)"
                    MessageStatus.QUEUED -> "Stored locally in offline queue. Will relay opportunistically."
                    else -> "Message recorded: ${createdMsg.status}"
                }
                _uiState.update {
                    it.copy(
                        isEmergencyModalOpen = false,
                        feedbackMessage = feedback
                    )
                }
            }.onFailure { err ->
                val safeErr = if (err is IllegalArgumentException || err is IllegalStateException) {
                    err.message ?: "Unable to queue message"
                } else {
                    "An unexpected error occurred while queueing the message."
                }
                _uiState.update {
                    it.copy(feedbackMessage = "Failed to queue message: $safeErr")
                }
            }
        }
    }

    fun relayMessage(messageId: String) {
        viewModelScope.launch {
            val nextHopNode = "PEER-HOP-${(1000..9999).random()}"
            val result = relayEmergencyMessageUseCase(
                messageId = messageId,
                relayerNodeId = nextHopNode,
                transport = TransportType.LOCAL_ONLY
            )

            result.onSuccess { updated ->
                _uiState.update {
                    it.copy(
                        selectedMessage = updated,
                        feedbackMessage = "Relayed to $nextHopNode (Hop ${updated.hopCount}/${updated.maxHops})"
                    )
                }
            }.onFailure { err ->
                val safeErr = if (err is IllegalArgumentException || err is IllegalStateException) {
                    err.message ?: "Unable to relay message"
                } else {
                    "An unexpected error occurred while relaying the message."
                }
                _uiState.update {
                    it.copy(feedbackMessage = "Relay rejected: $safeErr")
                }
            }
        }
    }

    fun syncQueue() {
        viewModelScope.launch {
            val currentNetwork = contextEngine.captureSnapshot().networkState
            _uiState.update { it.copy(isSyncing = true, networkState = currentNetwork) }
            val syncResult = syncEmergencyQueueUseCase(currentNetwork)
            _uiState.update {
                it.copy(
                    isSyncing = false,
                    lastSyncResult = syncResult,
                    feedbackMessage = syncResult.explanation
                )
            }
        }
    }

    class Factory(
        private val contextEngine: ContextEngine,
        private val createEmergencyMessageUseCase: CreateEmergencyMessageUseCase,
        private val getEmergencyQueueUseCase: GetEmergencyQueueUseCase,
        private val relayEmergencyMessageUseCase: RelayEmergencyMessageUseCase,
        private val syncEmergencyQueueUseCase: SyncEmergencyQueueUseCase
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ResilienceViewModel(
                contextEngine,
                createEmergencyMessageUseCase,
                getEmergencyQueueUseCase,
                relayEmergencyMessageUseCase,
                syncEmergencyQueueUseCase
            ) as T
        }
    }
}
