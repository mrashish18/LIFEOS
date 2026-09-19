package com.mrashish18.lifeos.core.resilience.transport

import com.mrashish18.lifeos.core.model.EmergencyMessage
import com.mrashish18.lifeos.core.model.NetworkState

sealed class TransportResult {
    data class Success(val updatedMessage: EmergencyMessage) : TransportResult()
    data class QueuedLocally(val message: EmergencyMessage) : TransportResult()
    data class Failed(val message: EmergencyMessage, val error: String) : TransportResult()
}

interface MeshTransport {
    val transportName: String
    fun isAvailable(networkState: NetworkState): Boolean
    suspend fun transmit(
        message: EmergencyMessage,
        networkState: NetworkState
    ): TransportResult
}
