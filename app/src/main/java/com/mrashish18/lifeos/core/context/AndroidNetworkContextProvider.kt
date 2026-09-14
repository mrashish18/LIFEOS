package com.mrashish18.lifeos.core.context

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.mrashish18.lifeos.core.model.NetworkState

/**
 * Safe, read-only network context provider utilizing Android's ConnectivityManager.
 * Requires only the normal permission android.permission.ACCESS_NETWORK_STATE.
 */
class AndroidNetworkContextProvider(
    private val context: Context
) : ContextProvider<NetworkState> {

    override fun provide(): NetworkState {
        return try {
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
                ?: return NetworkState.UNKNOWN

            val activeNetwork = connectivityManager.activeNetwork ?: return NetworkState.DISCONNECTED
            val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return NetworkState.DISCONNECTED

            when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> NetworkState.CONNECTED_WIFI
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> NetworkState.CONNECTED_CELLULAR
                capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) -> NetworkState.CONNECTED
                else -> NetworkState.DISCONNECTED
            }
        } catch (e: SecurityException) {
            NetworkState.UNKNOWN
        } catch (e: Exception) {
            NetworkState.UNKNOWN
        }
    }
}
