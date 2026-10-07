package com.example.data.repository

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.wifi.WifiManager
import android.os.Build
import com.example.domain.model.NetworkSecurityInfo
import com.example.domain.model.NetworkType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.Inet4Address
import java.net.NetworkInterface
import java.util.Collections

class NetworkRepository(private val context: Context) {

    suspend fun getNetworkInfo(): NetworkSecurityInfo = withContext(Dispatchers.IO) {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
        val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as? WifiManager

        if (connectivityManager == null) {
            return@withContext NetworkSecurityInfo(
                isConnected = false,
                networkType = NetworkType.DISCONNECTED,
                isMetered = false,
                isVpnActive = false,
                localIpAddress = "Unavailable",
                linkSpeedMbps = null,
                wifiSsid = null,
                dnsServers = emptyList(),
                gateway = null,
                interfaceName = null,
                limitations = listOf("ConnectivityManager unavailable.")
            )
        }

        val activeNetwork = connectivityManager.activeNetwork
        val caps = activeNetwork?.let { connectivityManager.getNetworkCapabilities(it) }
        val linkProps = activeNetwork?.let { connectivityManager.getLinkProperties(it) }

        val isConnected = caps != null && caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        val isMetered = caps != null && !caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_NOT_METERED)
        val isVpnActive = caps?.hasTransport(NetworkCapabilities.TRANSPORT_VPN) == true

        val networkType = when {
            caps == null || !isConnected -> NetworkType.DISCONNECTED
            caps.hasTransport(NetworkCapabilities.TRANSPORT_VPN) -> NetworkType.VPN
            caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> NetworkType.WIFI
            caps.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> NetworkType.CELLULAR
            caps.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> NetworkType.ETHERNET
            else -> NetworkType.UNKNOWN
        }

        val ipAddress = getLocalIpAddress()
        val dnsServers = linkProps?.dnsServers?.map { it.hostAddress ?: "" }?.filter { it.isNotEmpty() } ?: emptyList()
        val interfaceName = linkProps?.interfaceName

        // Wi-Fi link speed where available
        var linkSpeed: Int? = null
        var ssid: String? = null
        val limitations = mutableListOf<String>()
        val warnings = mutableListOf<String>()

        if (networkType == NetworkType.WIFI && wifiManager != null) {
            try {
                @Suppress("DEPRECATION")
                val wifiInfo = wifiManager.connectionInfo
                if (wifiInfo != null) {
                    val speed = wifiInfo.linkSpeed
                    if (speed > 0) linkSpeed = speed

                    val rawSsid = wifiInfo.ssid
                    if (rawSsid != null && rawSsid != "<unknown ssid>") {
                        ssid = rawSsid.replace("\"", "")
                    } else {
                        limitations.add("Wi-Fi SSID is protected by Android location privacy permissions on Android 10+.")
                    }
                }
            } catch (e: Exception) {
                limitations.add("Could not query Wi-Fi link parameters.")
            }
        }

        if (isVpnActive) {
            warnings.add("VPN tunnel is actively routing network traffic.")
        }

        if (isMetered) {
            warnings.add("Active network is metered (Cellular data or mobile hotspot).")
        }

        limitations.add("Deep packet inspection and hardware MAC addresses are restricted by modern Android security isolation.")

        NetworkSecurityInfo(
            isConnected = isConnected,
            networkType = networkType,
            isMetered = isMetered,
            isVpnActive = isVpnActive,
            localIpAddress = ipAddress,
            linkSpeedMbps = linkSpeed,
            wifiSsid = ssid,
            dnsServers = dnsServers,
            gateway = linkProps?.routes?.firstOrNull { it.isDefaultRoute }?.gateway?.hostAddress,
            interfaceName = interfaceName,
            limitations = limitations,
            warnings = warnings
        )
    }

    private fun getLocalIpAddress(): String {
        return try {
            val interfaces = Collections.list(NetworkInterface.getNetworkInterfaces())
            for (intf in interfaces) {
                if (intf.isLoopback || !intf.isUp) continue
                val addrs = Collections.list(intf.inetAddresses)
                for (addr in addrs) {
                    if (!addr.isLoopbackAddress && addr is Inet4Address) {
                        return addr.hostAddress ?: "Unknown"
                    }
                }
            }
            "127.0.0.1"
        } catch (e: Exception) {
            "Unavailable"
        }
    }
}
