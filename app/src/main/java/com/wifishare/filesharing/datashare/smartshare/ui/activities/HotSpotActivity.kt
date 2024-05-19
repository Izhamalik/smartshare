package com.wifishare.filesharing.datashare.smartshare.ui.activities

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.BroadcastReceiver
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiManager
import android.net.wifi.p2p.WifiP2pManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.location.LocationManagerCompat
import com.google.android.material.card.MaterialCardView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.king.zxing.util.CodeUtils
import com.wifishare.filesharing.datashare.smartshare.R
import com.wifishare.filesharing.datashare.smartshare.common.Constants
import com.wifishare.filesharing.datashare.smartshare.databinding.ActivityHotSpotBinding
import com.wifishare.filesharing.datashare.smartshare.otherutils.HotspotManager
import com.wifishare.filesharing.datashare.smartshare.otherutils.Keyword
import com.wifishare.filesharing.datashare.smartshare.task.ReceivingTask
import com.wifishare.filesharing.datashare.smartshare.util.FirebaseUtils
import java.net.InetAddress
import java.net.NetworkInterface
import java.util.Enumeration
import java.util.Random

class HotSpotActivity : AppCompatActivity() {


    private var binding : ActivityHotSpotBinding? = null

    private var locationManager: LocationManager? = null
    private val statusReceiver: StatusReceiver = StatusReceiver()
    private val intentFilter = IntentFilter()
    private val codeScannerFilter = IntentFilter()
    private var activeType: Type? = null
    private var isConnected = false
    private var isdelayed = true
    private var password = ""
    private var codeScannedReceiver: BroadcastReceiver? = null
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>

    companion object {
        const val WIFI_AP_STATE_CHANGED = "android.net.wifi.WIFI_AP_STATE_CHANGED"
        const val ACTION_OREO_HOTSPOT_STARTED = "com.share.intent.action.HOTSPOT_STARTED"
        const val EXTRA_HOTSPOT_CONFIG = "hotspotConfig"
        const val HOTSPOT_CONNECTED_ACTION = "com.share.intent.action.hotspotConnectedAction"
        const val HOTSPOT_CONNECTED_EXTRA = "hotspotConnectedExtra"
        val TAG = "HotSpot"
        lateinit var manager: HotspotManager

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHotSpotBinding.inflate(layoutInflater)
        setContentView(binding?.root)


        codeScannedReceiver = CodeScannedReceiver()
        codeScannerFilter.addAction(HOTSPOT_CONNECTED_ACTION)


        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        manager = HotspotManager.newInstance(this@HotSpotActivity)
        if (Build.VERSION.SDK_INT >= 26) {
            manager.secondaryCallback = SecondaryHotspotCallback()
        }

        requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (!isGranted) {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri: Uri = Uri.fromParts("package", packageName, null)
                intent.data = uri
                startActivity(intent)
            }
        }

        intentFilter.addAction(ACTION_OREO_HOTSPOT_STARTED)
        // Indicates the state of Wi-Fi P2P connectivity has changed.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION)
        intentFilter.addAction(WIFI_AP_STATE_CHANGED)
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION)
        intentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION)
        binding?.backpressBtn?.setOnClickListener { onBackPressed() }

        binding?.phoneTitle?.text = getDeviceName()

        binding?.createQR?.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if(checkPermissionOrStartLocalOnlyHotspot()){
                    val retVal = Settings.System.canWrite(this)
                    if (retVal) {

                        Log.e(TAG, "80 ==== toggleHotspot:  IF")
                        toggleHotspotMy()
                    } else {

                        Log.e(TAG, "83 ==== toggleHotspot:  ELSE")
                        allowSettingPermission()
                    }
                }

            }
            else{
                val retVal = Settings.System.canWrite(this)
                if (retVal) {

                    Log.e(TAG, "80 ==== toggleHotspot:  IF")
                    toggleHotspotMy()
                } else {

                    Log.e(TAG, "83 ==== toggleHotspot:  ELSE")
                    allowSettingPermission()
                }
            }

        }


        binding?.imgCopy?.setOnClickListener {
            if (password != "") {
                val clipboard: ClipboardManager =
                    getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("password", password)
                clipboard.setPrimaryClip(clip)
                Toast.makeText(this@HotSpotActivity, "Password Copied!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this@HotSpotActivity, "Create HostSpot First!", Toast.LENGTH_SHORT)
                    .show()
            }
        }


    }

    @SuppressLint("InlinedApi")
    private fun checkPermissionOrStartLocalOnlyHotspot():Boolean {

        val permission: String = Manifest.permission.NEARBY_WIFI_DEVICES
        when {
            ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED -> {
                return true
            }
            shouldShowRequestPermissionRationale(permission) -> {
                MaterialAlertDialogBuilder(this)
                    .setMessage("This app would not work without Nearby Wi-Fi Devices permission. Do you want to give this app the permission?")
                    .setPositiveButton("Yes") { _, _ ->
                        requestPermissionLauncher.launch(permission)
                    }.setNegativeButton("No Thanks") { _, _ ->

                    }.show()
                return false
            }
            else -> {

                requestPermissionLauncher.launch(permission)
                return false
            }
        }

    }

    private fun allowSettingPermission() {
        val retVal = Settings.System.canWrite(this)
        if (!retVal) {
            Log.e(TAG, "106 ==== toggleHotspot:  333 IF")

            if (!Settings.System.canWrite(this@HotSpotActivity)) {
                Log.e(TAG, "108 ==== toggleHotspot:  Permission IF")

                val intent = Intent(
                    Settings.ACTION_MANAGE_WRITE_SETTINGS, Uri.parse(
                        "package:$packageName"
                    )
                )
                Toast.makeText(
                    applicationContext,
                    "Please, allow system settings for Hotspot ",
                    Toast.LENGTH_LONG
                ).show()
                resultLauncherHotSpot.launch(intent)
            }
        } else {
            Toast.makeText(
                applicationContext,
                "You are not allowed to wright ",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private val resultLauncherHotSpot =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            Log.e(TAG, "666 ${binding?.createQR1?.text}")
        }

    private fun toggleHotspotMy() {
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O && !Settings.System.canWrite(this)
//        ) return

        if(isdelayed){
            isdelayed=false
            try{
                Log.e(TAG, "148 ${binding?.createQR1?.text}")

                if(binding?.createQR1?.text?.contains(resources.getText(R.string.stop_hotspot)) == true){

                    manager.disable()
                    Log.e(TAG, "152 ========    toggleHotspot: hotspot Stop!")
                    binding?.qrCode?.setImageResource(R.drawable.ic_qr_holder)
                    binding?.llName?.visibility = View.INVISIBLE
                    binding?.llKey?.visibility = View.INVISIBLE
                    binding?.createQR1?.setText(R.string.create_qR)
                    isConnected = false
                    val wifi = applicationContext.getSystemService(WIFI_SERVICE) as WifiManager
                    wifi.isWifiEnabled = true
                }
                else {
                    Log.e(TAG, "160 ==== toggleHotspot:  $isConnected")
//                    manager.disable()
                    try{
                        manager.disable()
                        manager = HotspotManager.newInstance(this@HotSpotActivity)
                        if (Build.VERSION.SDK_INT >= 26) {
                            manager.secondaryCallback = SecondaryHotspotCallback()
                        }
                        val result = manager.enableConfigured(getString(R.string.app_name), null)
                        isConnected = result
                    }catch (e:Exception){


                    }


                }
            }
            catch (e: Exception){
                Log.e(TAG, "170 Crash==== ${e.message}")
            }
        }
        Handler(Looper.getMainLooper()).postDelayed({
            isdelayed=true
        },1000)

    }

    private inner class StatusReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            Log.e(TAG, "162 ==== onReceive :   " + intent.action)
            if (WIFI_AP_STATE_CHANGED == intent.action
                || WifiManager.WIFI_STATE_CHANGED_ACTION == intent.action
                || ConnectivityManager.CONNECTIVITY_ACTION == intent.action
                || ACTION_OREO_HOTSPOT_STARTED == intent.action
                || WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION == intent.action
            ) updateViews()
        }
    }

    override fun onResume() {
        super.onResume()
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(statusReceiver, intentFilter,RECEIVER_NOT_EXPORTED)
            registerReceiver(codeScannedReceiver, codeScannerFilter,RECEIVER_NOT_EXPORTED)
        }
        else{
            registerReceiver(statusReceiver, intentFilter)
            registerReceiver(codeScannedReceiver, codeScannerFilter)
        }

        updateViews()
    }

    override fun onPause() {
        super.onPause()

        unregisterReceiver(statusReceiver)
        unregisterReceiver(codeScannedReceiver)
    }

    private fun updateViews() {

        val wifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val min = 2000
        val max = 9000
        val pin = Random().nextInt(max - min + 1) + min

        Constants.PORT=pin

        val delimiter = ";"
        val code = StringBuilder()


        Handler(Looper.getMainLooper()).postDelayed({
            val config: WifiConfiguration? = getWifiConfiguration()
            if (config != null) {
                Log.e(TAG, "193 === updateViews:   ${config.preSharedKey}")
            }
            if (isConnected) {
                if (config != null) {
                    activeType = Type.Hotspot
                    val ssid: String = config.SSID
                    val bssid: String? = config.BSSID
                    val key: String? = config.preSharedKey
                    code.append(Keyword.QR_CODE_TYPE_HOTSPOT)
                        .append(delimiter)
                        .append(pin)
                        .append(delimiter)
                        .append(ssid)
                        .append(delimiter)
                        .append(getIpAddress())
                        .append(delimiter)
                        .append(key ?: "")
                        .append(delimiter)
                        .append(getDeviceName())

                    Log.e(TAG, "194 === updateViews:   $code")

                    binding?.text1?.text = ssid
                    binding?.text2?.text = key
                    password = key.toString()
                } else {
                    activeType = Type.HotspotExternal
                    binding?.text1?.setText(R.string.externally_started_hotspot_notice)
                }
                binding?.createQR1?.setText(R.string.stop_hotspot)
                binding?.connectTxt?.text = "Connect"
            }
            else if (!canReadWifiInfo() && wifiManager.isWifiEnabled) {
                activeType = Type.LocationAccess
                binding?.text1?.setText(
                    if (isLocationServiceEnabled()) {
                        R.string.location_permission_required_notice
                    } else {
                        R.string.location_service_disabled_notice
                    }
                )
                binding?.connectTxt?.text = ""
                binding?.createQR1?.setText(R.string.create_qR)
            }


            val showQRCode = code.isNotEmpty()
            if (showQRCode) {
                code.append(delimiter)
                    .append("end")
                createQRCode(code.toString())
                binding?.llName?.visibility = View.VISIBLE
                binding?.llKey?.visibility = View.VISIBLE
                val receivingTask = ReceivingTask(this)
                receivingTask.asyncCall()
                Log.e(TAG, "440 ===    showQRCode")

            }
        },500)

    }

    private fun getIpAddress(): String {
        var ip = ""
        try {
            val enumNetworkInterfaces: Enumeration<NetworkInterface> = NetworkInterface
                .getNetworkInterfaces()
            while (enumNetworkInterfaces.hasMoreElements()) {
                val networkInterface: NetworkInterface = enumNetworkInterfaces
                    .nextElement()
                val enumInetAddress: Enumeration<InetAddress> = networkInterface
                    .inetAddresses
                while (enumInetAddress.hasMoreElements()) {
                    val inetAddress: InetAddress = enumInetAddress.nextElement()
                    if (inetAddress.isSiteLocalAddress) {
                        ip += (inetAddress.hostAddress)!! + ":"
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            ip += """
            Something Wrong! $e
            """.trimIndent()
        }
        return ip
    }

    private fun createQRCode(qrValue: String) {
        Log.e(TAG, "275 check bitmapvalue====$qrValue")
        Thread {
            val logo = BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher)
            val bitmap = CodeUtils.createQRCode(qrValue, 600, logo)
            runOnUiThread {
                binding?.qrCode?.setImageBitmap(bitmap)
            }
        }.start()
    }

    private enum class Type {
        None, WiFi, Hotspot, HotspotExternal, LocationAccess
    }

    fun isLocationServiceEnabled(): Boolean =
        LocationManagerCompat.isLocationEnabled(locationManager!!)

    fun canReadWifiInfo(): Boolean {
        return Build.VERSION.SDK_INT < 26 || hasLocationPermission() && isLocationServiceEnabled()
    }

    fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun getWifiConfiguration(): WifiConfiguration? {
        if (Build.VERSION.SDK_INT < 26) {
            return manager.configuration
        }
        try {
            return manager.configuration
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        }
        return null
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private inner class SecondaryHotspotCallback : WifiManager.LocalOnlyHotspotCallback() {
        override fun onStarted(reservation: WifiManager.LocalOnlyHotspotReservation) {
            super.onStarted(reservation)
            sendBroadcast(
                Intent(ACTION_OREO_HOTSPOT_STARTED).putExtra(
                    EXTRA_HOTSPOT_CONFIG,
                    reservation.wifiConfiguration
                )
            )
        }
    }


    fun getDeviceName(): String {
        val manufacturer = Build.MANUFACTURER
        val model = Build.MODEL
        return if (model.startsWith(manufacturer)) {
            capitalize(model)
        } else {
            capitalize(manufacturer) + " " + model
        }
    }


    private fun capitalize(s: String?): String {
        if (s == null || s.isEmpty()) {
            return ""
        }
        val first = s[0]
        return if (Character.isUpperCase(first)) {
            s
        } else {
            first.uppercaseChar().toString() + s.substring(1)
        }
    }


    class CodeScannedReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val action = intent!!.action
            if (action == HOTSPOT_CONNECTED_ACTION) {
                Log.e(TAG, "366 === QR Code Scanned!")
            }
        }
    }

    private fun hasInternetConnection(): Boolean{
        val connectivityManager= this.getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager

        val activeNetwork= connectivityManager.activeNetwork?: return false
        val capabilities= connectivityManager.getNetworkCapabilities(activeNetwork)?: return false

        return when{
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)-> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)-> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)->true
            else -> false
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()

        manager.disable()
        binding?.qrCode?.setImageResource(R.drawable.ic_qr_holder)
        binding?.llName?.visibility = View.INVISIBLE
        binding?.llKey?.visibility = View.INVISIBLE
        isConnected = false
        val wifi = applicationContext.getSystemService(WIFI_SERVICE) as WifiManager
        wifi.isWifiEnabled = true
    }


    override fun onDestroy() {
        super.onDestroy()
        manager.disable()
        val wifi = applicationContext.getSystemService(WIFI_SERVICE) as WifiManager
        wifi.isWifiEnabled = true
    }



}