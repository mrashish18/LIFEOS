package com.mrashish18.lifeos.core.resilience.transport

import com.mrashish18.lifeos.core.model.EmergencyMessage
import com.mrashish18.lifeos.core.model.MessageStatus
import com.mrashish18.lifeos.core.model.NetworkState
import com.mrashish18.lifeos.core.model.TransportType
import com.mrashish18.lifeos.core.resilience.RescueMeshEngine
import com.mrashish18.lifeos.domain.repository.EmergencyMessageRepository

class NetworkGatewayTransport(
    private val repository: EmergencyMessageRepository,
    private val engine: RescueMeshEngine
) : MeshTransport {

    override val transportName: String = "NETWORK_GATEWAY"

    override fun isAvailable(networkState: NetworkState): Boolean {
        return networkState == NetworkState.CONNECTED_WIFI || networkState == NetworkState.CONNECTED_CELLULAR
    }

    override suspend fun transmit(
        message: EmergencyMessage,
        networkState: NetworkState
    ): TransportResult {
        if (!isAvailable(networkState)) {
            return TransportResult.Failed(message, "Network gateway unavailable in current state: $networkState")
        }

        return try {
            val dispatchedMessage = message.copy(
                status = MessageStatus.SENT,
                transportType = TransportType.NETWORK
            )
            repository.updateMessage(dispatchedMessage)
            TransportResult.Success(dispatchedMessage)
        } catch (e: Exception) {
            TransportResult.Failed(message, e.message ?: "Failed to transmit through network gateway")
        }
    }
}
