package com.wifishare.filesharing.datashare.smartshare.util

import android.net.wifi.p2p.WifiP2pDevice

object WifiP2pUtils {
    @JvmStatic
    fun getDeviceStatus(deviceStatus: Int): String {
        return when (deviceStatus) {
            WifiP2pDevice.AVAILABLE -> "usable"
            WifiP2pDevice.INVITED -> "Inviting"
            WifiP2pDevice.CONNECTED -> "connected"
            WifiP2pDevice.FAILED -> "Failure"
            WifiP2pDevice.UNAVAILABLE -> "unavailable"
            else -> "unknown"
        }
    }
}