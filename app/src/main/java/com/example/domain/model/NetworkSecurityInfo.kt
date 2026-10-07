package com.example.domain.model

enum class NetworkType(val displayName: String) {
    WIFI("Wi-Fi"),
    CELLULAR("Cellular Mobile"),
    ETHERNET("Ethernet"),
    VPN("VPN"),
    UNKNOWN("Unknown / Other"),
    DISCONNECTED("Disconnected")
}

data class NetworkSecurityInfo(
    val isConnected: Boolean,
    val networkType: NetworkType,
    val isMetered: Boolean,
    val isVpnActive: Boolean,
    val localIpAddress: String,
    val linkSpeedMbps: Int?,
    val wifiSsid: String?,
    val dnsServers: List<String>,
    val gateway: String?,
    val interfaceName: String?,
    val limitations: List<String> = emptyList(),
    val warnings: List<String> = emptyList()
)
