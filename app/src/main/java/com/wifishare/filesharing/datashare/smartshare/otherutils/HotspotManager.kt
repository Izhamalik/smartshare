package com.wifishare.filesharing.datashare.smartshare.otherutils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method

abstract class HotspotManager internal constructor(context: Context) {
    protected val context: Context = context.applicationContext

    abstract val configuration: WifiConfiguration?
    abstract val enabled: Boolean
    abstract val previousConfig: WifiConfiguration?

    var secondaryCallback: WifiManager.LocalOnlyHotspotCallback? = null

    abstract val started: Boolean

    val wifiManager: WifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

    abstract fun disable(): Boolean

    abstract fun enable(): Boolean

    abstract fun enableConfigured(apName: String, passKeyWPA2: String?): Boolean

    abstract fun unloadPreviousConfig(): Boolean

    @RequiresApi(26)
    class OreoHotspotManager(context: Context) : HotspotManager(context) {
        override val configuration: WifiConfiguration?
            get() = hotspotReservation?.wifiConfiguration

        override val enabled: Boolean
            get() = OldHotspotManager.enabled(wifiManager)

        private var hotspotReservation: WifiManager.LocalOnlyHotspotReservation? = null

        override val previousConfig: WifiConfiguration?
            get() = configuration

        override val started: Boolean
            get() = hotspotReservation != null

        override fun disable(): Boolean {
            Log.e("CheckDoneBtn","Disable ")

            if (hotspotReservation == null)
                return false

            Log.e("CheckDoneBtn","disable else")

            hotspotReservation?.close()
            hotspotReservation = null

            return true
        }

        override fun enable(): Boolean {
            if (ActivityCompat.checkSelfPermission(context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {

                return false
            }

            try {
                wifiManager.startLocalOnlyHotspot(callback, Handler(Looper.getMainLooper()))

                return true
            } catch (e: Exception) {
                e.printStackTrace()
//                Snackbar.make(context,"Kindly Turn off the wifi",Snackbar.LENGTH_LONG)
            }
            return false
        }

        override fun enableConfigured(apName: String, passKeyWPA2: String?): Boolean {

            return enable()
        }

        override fun unloadPreviousConfig(): Boolean {
            return hotspotReservation != null
        }

        val callback = object : WifiManager.LocalOnlyHotspotCallback() {
            override fun onStarted(reservation: WifiManager.LocalOnlyHotspotReservation) {
                super.onStarted(reservation)
                hotspotReservation = reservation
                secondaryCallback?.onStarted(reservation)
            }

            override fun onStopped() {
                super.onStopped()
                hotspotReservation = null
                secondaryCallback?.onStopped()
            }

            override fun onFailed(reason: Int) {
                super.onFailed(reason)
                hotspotReservation = null
                secondaryCallback?.onFailed(reason)
            }
        }
    }

    @Suppress("DEPRECATION")
    private class OldHotspotManager(context: Context) : HotspotManager(context) {
        override val configuration: WifiConfiguration?
            get() = invokeSilently(getWifiApConfiguration, wifiManager) as WifiConfiguration?

        override val enabled: Boolean
            get() = enabled(wifiManager)

        override val previousConfig: WifiConfiguration?
            get() = previousConfigPrivate

        private var previousConfigPrivate: WifiConfiguration? = null

        override val started: Boolean
            get() = previousConfig != null

        override fun disable(): Boolean {
            unloadPreviousConfig()
            return setHotspotEnabled(previousConfigPrivate, false)
        }

        override fun enable(): Boolean {
            wifiManager.isWifiEnabled = false
            return setHotspotEnabled(configuration, true)
        }

        override fun enableConfigured(apName: String, passKeyWPA2: String?): Boolean {
            wifiManager.isWifiEnabled = false

            if (previousConfigPrivate == null)
                previousConfigPrivate = configuration

            val wifiConfiguration = WifiConfiguration().apply {
                SSID = apName

                if (passKeyWPA2 != null && passKeyWPA2.length >= 8) {
                    allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK)
                    allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN)
                    preSharedKey = passKeyWPA2
                } else
                    allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE)
            }

            return setHotspotEnabled(wifiConfiguration, true)
        }

        private fun setHotspotConfig(config: WifiConfiguration): Boolean {
            val result = invokeSilently(setWifiApConfiguration, wifiManager, config) ?: return false
            return result as Boolean
        }

        private fun setHotspotEnabled(config: WifiConfiguration?, enabled: Boolean): Boolean {
            val result = invokeSilently(setWifiApEnabled, wifiManager, config, enabled) ?: return false
            return result as Boolean
        }

        override fun unloadPreviousConfig(): Boolean = previousConfig?.let {
            previousConfigPrivate = null
            return@let setHotspotConfig(it)
        } ?: false

        companion object {
            private var getWifiApConfiguration: Method? = null

            private var getWifiApState: Method? = null

            private var isWifiApEnabled: Method? = null

            private var setWifiApEnabled: Method? = null

            private var setWifiApConfiguration: Method? = null

            fun enabled(wifiManager: WifiManager): Boolean {
                val result = invokeSilently(isWifiApEnabled, wifiManager) ?: return false
                return result as Boolean
            }

            private fun invokeSilently(method: Method?, receiver: Any, vararg args: Any?): Any? {
                try {
                    return method?.invoke(receiver, *args)
                } catch (e: IllegalAccessException) {
                    Log.e(TAG, "exception in invoking methods: " + method?.name + "(): " + e.message)
                } catch (e: IllegalArgumentException) {
                    Log.e(TAG, "exception in invoking methods: " + method?.name + "(): " + e.message)
                } catch (e: InvocationTargetException) {
                    Log.e(TAG, "exception in invoking methods: " + method?.name + "(): " + e.message)
                }
                return null
            }

            fun supported(): Boolean {
                return getWifiApState != null && isWifiApEnabled != null && setWifiApEnabled != null
                        && getWifiApConfiguration != null
            }

            init {
                for (method in WifiManager::class.java.declaredMethods) {
                    when (method.name) {
                        "getWifiApConfiguration" -> getWifiApConfiguration = method
                        "getWifiApState" -> getWifiApState = method
                        "isWifiApEnabled" -> isWifiApEnabled = method
                        "setWifiApEnabled" -> setWifiApEnabled = method
                        "setWifiApConfiguration" -> setWifiApConfiguration = method
                    }
                }
            }
        }
    }

    companion object {
        private const val TAG = "HotspotManager"

        val supported: Boolean
            get() = Build.VERSION.SDK_INT >= 26 || OldHotspotManager.supported()

        fun newInstance(context: Context): HotspotManager {

            return if (Build.VERSION.SDK_INT >= 26) OreoHotspotManager(context) else OldHotspotManager(context)
        }
    }
}