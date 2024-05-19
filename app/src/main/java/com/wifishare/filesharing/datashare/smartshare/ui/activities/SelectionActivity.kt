package com.wifishare.filesharing.datashare.smartshare.ui.activities

import android.Manifest
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.net.wifi.SupplicantState
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.text.format.Formatter
import android.util.Log
import android.view.Window
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.king.zxing.util.CodeUtils
import com.wifishare.filesharing.datashare.smartshare.R
import com.wifishare.filesharing.datashare.smartshare.databinding.ActivitySelectionBinding
import com.wifishare.filesharing.datashare.smartshare.Controller
import com.wifishare.filesharing.datashare.smartshare.Controller.Companion.wifiORhotspot
import com.wifishare.filesharing.datashare.smartshare.callback.SnackbarPlacementProvider
import com.wifishare.filesharing.datashare.smartshare.common.Constants
import com.wifishare.filesharing.datashare.smartshare.otherutils.Keyword
import com.wifishare.filesharing.datashare.smartshare.task.ReceivingTask
import com.wifishare.filesharing.datashare.smartshare.task.SendingTask
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import java.io.File
import java.util.Objects
import java.util.Random

class SelectionActivity : AppCompatActivity() {

    private var binding : ActivitySelectionBinding? = null

    private var isTap = false
    private var isDeviceConnected = false
    lateinit var dialog12: Dialog
    lateinit var dialog123: Dialog
    private var wifiManager: WifiManager? = null
    var connectivityManager: ConnectivityManager? =null
    private val coroutineScope = CoroutineScope(Dispatchers.Default)
    private lateinit var dialog: Dialog
    private val TAG = "Selection"
    var flowCheck=""
    private  val CODE_REQ_PERMISSIONS = 665
    private  val CODE_REQ_PERMISSIONS5 = 565
    private  val CODE_REQ_PERMISSIONS1 = 666
    private  val CODE_REQ_PERMISSIONS2 = 667
    private  val CODE_REQ_PERMISSIONS3 = 668
    private  val CODE_REQ_PERMISSIONS4 = 670

    private var isAllDone = false
    private var isClicked = false
    var ipAddressForSender=""
    lateinit var loadingLayout: LinearLayout
    var value_not_show_again1=false
    var value_not_show_again2=false
    var value_not_show_again3=false
    var value_not_show_again4=false
    var value_not_show_again5=false
    var value_not_show_again6=false
    var value_not_show_again7=false
    var value_not_show_again8=false
    var value_flow=0
    companion object{
        var QRScanner=false
    }

    lateinit var sharedPreferences_not_show_agin: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelectionBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        SplashActivity.activity =this@SelectionActivity

        binding?.backpressBtn?.setOnClickListener {
           onBackPressed()
        }

        wifiManager = applicationContext.getSystemService(WIFI_SERVICE) as WifiManager
        connectivityManager = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities = connectivityManager!!.getNetworkCapabilities(connectivityManager!!.activeNetwork)

        flowCheck= intent.getStringExtra("checkFlow").toString()

        if(flowCheck == "startActivity"){
            value_flow=0
        }
        else{
            value_flow=1
        }

        if (Controller.isSender) {
            binding?.wifiTextView?.text=resources.getText(R.string.send_wifi)
            binding?.hotspotTextView?.text=resources.getText(R.string.send_hotspot)

        }else{
            binding?.wifiTextView?.text=resources.getText(R.string.receive_wifi)
            binding?.hotspotTextView?.text=resources.getText(R.string.receive_hotspot)
        }


        try {
            dialog = Dialog(this@SelectionActivity)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            Objects.requireNonNull(dialog.window)!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.setContentView(R.layout.connecting_dialog)
        } catch (e: RuntimeException) {
            e.printStackTrace()
        }



        binding?.directWifiBtn?.setOnClickListener {
            wifiORhotspot = "wf"
            if (hasInternetConnection()) {
                if (!isTap) {
                    isTap = true


                    if (!checkPermission()) {
                        if (ActivityCompat.shouldShowRequestPermissionRationale(
                                this@SelectionActivity,
                                Manifest.permission.ACCESS_FINE_LOCATION
                            )
                        ) {

                            try {
                                AlertDialog.Builder(this@SelectionActivity)
                                    .setMessage(resources.getText(R.string.location_permission_grant))
                                    .setCancelable(false)
                                    .setPositiveButton(
                                        resources.getText(R.string.permission_dialog_positive_btn)
                                    ) { _, _ ->
                                        try{
                                            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                                data = Uri.fromParts("package", packageName, null)
                                                addCategory(Intent.CATEGORY_DEFAULT)
                                                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                                addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                                                addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
                                            }

                                            startActivity(intent)
                                        }catch (e:Exception){

                                        }
                                        isClicked = false
                                        isTap=false
                                    }
                                    .setNegativeButton(R.string.cancel) { _, _ ->
                                        isClicked = false
                                        isTap=false
                                    }
                                    .show()
                            } catch (e: IllegalStateException) {
                                e.printStackTrace()
                                isClicked = false
                            }

                        } else {

                            ActivityCompat.requestPermissions(
                                this, arrayOf(
                                    Manifest.permission.ACCESS_FINE_LOCATION
                                ), CODE_REQ_PERMISSIONS5
                            )


                        }
                    }
                    else {
                        if (isGPSAllowed()) {
//                            if (Controller.isSender) {
//                                val wm: WifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
//
//                                val ip: String = Formatter.formatIpAddress(wm.connectionInfo.ipAddress)
//                                ipAddressForSender = ip.split("\\.(?=[^.]+$)".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0]
//                                if(ipAddressForSenderPrevious=="" || ipAddressForSenderPrevious!=ipAddressForSender){
//                                    ipAddressForSenderPrevious=ipAddressForSender
//                                    checkIP()
//                                }
//
//
//                            }
                            connectivityViaWifi()

                        }
                        else {
                            try {
                                AlertDialog.Builder(this@SelectionActivity)
                                    .setMessage(resources.getText(R.string.permission_dialog_msg))
                                    .setCancelable(false)
                                    .setPositiveButton(
                                        resources.getText(R.string.permission_dialog_positive_btn)
                                    ) { _, _ ->
                                        startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                                        isClicked = false
                                    }
                                    .setNegativeButton(R.string.cancel) { _, _ ->
                                        isClicked = false
                                    }
                                    .show()
                            } catch (e: IllegalStateException) {
                                e.printStackTrace()
                                isClicked = false
                            }
                        }

                    }
                }
            }
            else {
                snackbar.createSnackbar(R.string.connection_not_available)?.show()
            }
        }


        binding?.hotspotBtn?.setOnClickListener {
            wifiORhotspot="hs"
            Constants.PORT=58761
            if (hasInternetConnection()) {
                if (!isTap) {
                    isTap = true
                    if (Controller.isSender) {
                        if (!checkPermission()) {
                            if (ActivityCompat.shouldShowRequestPermissionRationale(
                                    this@SelectionActivity,
                                    Manifest.permission.ACCESS_FINE_LOCATION
                                )
                            ) {

                                try {
                                    AlertDialog.Builder(this@SelectionActivity)
                                        .setMessage(resources.getText(R.string.location_permission_grant))
                                        .setCancelable(false)
                                        .setPositiveButton(
                                            resources.getText(R.string.permission_dialog_positive_btn)
                                        ) { _, _ ->
                                            try{
                                                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                                    data = Uri.fromParts("package", packageName, null)
                                                    addCategory(Intent.CATEGORY_DEFAULT)
                                                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                                    addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                                                    addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
                                                }

                                                startActivity(intent)
                                            }catch (e:Exception){

                                            }

                                            isClicked = false
                                            isTap = false
                                        }
                                        .setNegativeButton(R.string.cancel) { _, _ ->
                                            isClicked = false
                                            isTap = false
                                        }
                                        .show()
                                } catch (e: IllegalStateException) {
                                    e.printStackTrace()
                                    isClicked = false
                                }

                            } else {

                                ActivityCompat.requestPermissions(
                                    this, arrayOf(
                                        Manifest.permission.ACCESS_FINE_LOCATION
                                    ), CODE_REQ_PERMISSIONS4
                                )


                            }
                        }
                        else {
                            if (!checkPermission1()) {
                                if (ActivityCompat.shouldShowRequestPermissionRationale(this@SelectionActivity, Manifest.permission.CAMERA)) {

                                    try {
                                        AlertDialog.Builder(this@SelectionActivity)
                                            .setMessage(resources.getText(R.string.camera_permission1))
                                            .setCancelable(false)
                                            .setPositiveButton(
                                                resources.getText(R.string.permission_dialog_positive_btn)
                                            ) { _, _ ->
                                                try {
                                                    val intent =
                                                        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                                            data = Uri.fromParts("package", packageName, null)
                                                            addCategory(Intent.CATEGORY_DEFAULT)
                                                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                                            addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                                                            addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
                                                        }

                                                    startActivity(intent)
                                                } catch (e: Exception) {

                                                }

                                                isClicked = false
                                                isTap = false
                                            }
                                            .setNegativeButton(R.string.cancel) { _, _ ->
                                                isClicked = false
                                                isTap = false
                                            }
                                            .show()
                                    } catch (e: IllegalStateException) {
                                        e.printStackTrace()
                                        isClicked = false
                                    }

                                } else {

                                    ActivityCompat.requestPermissions(
                                        this, arrayOf(
                                            Manifest.permission.CAMERA
                                        ), CODE_REQ_PERMISSIONS1
                                    )


                                }
                            } else {
                                if (flowCheck == "0") {
                                    Log.e("cccccccccccccccccccc","bbbbbbbbbbbbbb$value_flow")

                                    var intent: Intent = if (value_not_show_again2)
                                        Intent(this, ScannerActivity::class.java)
                                    else
                                        Intent(this, InstructionsScreenActivity::class.java)
                                    intent.putExtra("wifiOrHotspot", "send_hotspot$flowCheck")
                                    intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                                    //  startActivity(intent)
                                    startActivityForResult(intent, 1)
                                } else {
                                    Log.e("cccccccccccccccccccc","ddddddddddddddd$value_flow")

                                    var intent: Intent = if (value_not_show_again6)
                                        Intent(this, ScannerActivity::class.java)
                                    else
                                        Intent(this, InstructionsScreenActivity::class.java)
                                    intent.putExtra("wifiOrHotspot", "send_hotspot$flowCheck")
                                    intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                                    //  startActivity(intent)
                                    startActivityForResult(intent, 1)

                                }
                            }
                        }

                    }
                    else{
                        if (!checkPermission()) {
                            if (ActivityCompat.shouldShowRequestPermissionRationale(this@SelectionActivity, Manifest.permission.ACCESS_FINE_LOCATION)) {

                                try {
                                    AlertDialog.Builder(this@SelectionActivity)
                                        .setMessage(resources.getText(R.string.location_permission_grant))
                                        .setCancelable(false)
                                        .setPositiveButton(resources.getText(R.string.permission_dialog_positive_btn)) { _, _ ->
                                            try{
                                                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                                    data = Uri.fromParts("package", packageName, null)
                                                    addCategory(Intent.CATEGORY_DEFAULT)
                                                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                                    addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                                                    addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
                                                }

                                                startActivity(intent)
                                            }catch (e:Exception){

                                            }

                                            isClicked = false
                                            isTap = false
                                        }
                                        .setNegativeButton(R.string.cancel) { _, _ ->
                                            isClicked = false
                                            isTap = false
                                        }
                                        .show()
                                }
                                catch (e: IllegalStateException) {
                                    e.printStackTrace()
                                    isClicked = false
                                }

                            } else {
                                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), CODE_REQ_PERMISSIONS)
                            }
                        }
                        else {
                            if (flowCheck == "0") {
                                Log.e("cccccccccccccccccccc","00000000000000000$value_flow")

                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    var intent: Intent = if (value_not_show_again4)
                                        Intent(this, HotSpotActivity::class.java)
                                    else
                                        Intent(this, InstructionsScreenActivity::class.java)
                                    intent.putExtra("wifiOrHotspot", "receive_hotspot$value_flow")
                                    intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                                    startActivity(intent)
                                } else {
                                    isTap = false
                                    Toast.makeText(this@SelectionActivity, resources.getText(R.string.feature_not_supported), Toast.LENGTH_SHORT).show()
                                }
                            }
                            else {
                                Log.e("cccccccccccccccccccc","aaaaaaaaaaaaaaaaa$value_flow")
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    var intent: Intent = if (value_not_show_again8)
                                        Intent(this, HotSpotActivity::class.java)
                                    else
                                        Intent(this, InstructionsScreenActivity::class.java)
                                    intent.putExtra("wifiOrHotspot", "receive_hotspot$value_flow")
                                    intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                                    startActivity(intent)
                                } else {
                                    isTap = false
                                    Toast.makeText(this@SelectionActivity, resources.getText(R.string.feature_not_supported), Toast.LENGTH_SHORT).show()
                                }
                            }



                        }
                    }


                }
            }else{
                snackbar.createSnackbar(R.string.connection_not_available)?.show()
            }
        }
    }


    override fun onResume() {
        super.onResume()
        isTap = false
//        loadNative()
        sharedPreferences_not_show_agin=getSharedPreferences("sharedPreferences_not_show_again_instruction", MODE_PRIVATE)

        try {
            ReceivingTask.serverSocket.close()
        }catch (e:Exception){
            Log.e("Cccccccaasda","receiver"+e.message.toString())
        }
        try {
            SendingTask.socket!!.close()
        }catch (e:Exception){
            Log.e("Cccccccaasda",e.message.toString())
        }
        value_not_show_again1=sharedPreferences_not_show_agin.getBoolean("value_not_show_again_instruction_send_wifi0",false)
        value_not_show_again2=sharedPreferences_not_show_agin.getBoolean("value_not_show_again_instruction_send_hotspot0",false)
        value_not_show_again3=sharedPreferences_not_show_agin.getBoolean("value_not_show_again_instruction_receive_wifi0",false)
        value_not_show_again4=sharedPreferences_not_show_agin.getBoolean("value_not_show_again_instruction_receive_hotspot0",false)
        value_not_show_again5=sharedPreferences_not_show_agin.getBoolean("value_not_show_again_instruction_send_wifi1",false)
        value_not_show_again6=sharedPreferences_not_show_agin.getBoolean("value_not_show_again_instruction_send_hotspot1",false)
        value_not_show_again7=sharedPreferences_not_show_agin.getBoolean("value_not_show_again_instruction_receive_wifi1",false)
        value_not_show_again8=sharedPreferences_not_show_agin.getBoolean("value_not_show_again_instruction_receive_hotspot1",false)

    }


    private val snackbar = SnackbarPlacementProvider { resId, objects ->
        return@SnackbarPlacementProvider binding!!.mainScreen.let {
            Snackbar.make(it, getString(resId, objects), Snackbar.LENGTH_LONG).addCallback(
                object : BaseTransientBottomBar.BaseCallback<Snackbar>() {
                    override fun onShown(transientBottomBar: Snackbar?) {
                        super.onShown(transientBottomBar)
                    }

                    override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                        super.onDismissed(transientBottomBar, event)
                    }
                }
            )
        }
    }


    private fun connectivityViaWifi() {

        try {
            val dialog = Dialog(this@SelectionActivity)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCancelable(false)
            dialog.setContentView(R.layout.dialog_connectivity_via_wifi_otp) //get layout from ExitDialog folder
            if (dialog.window != null)
                dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))


            val okayBtn = dialog.findViewById<MaterialCardView>(R.id.okay_btn)
            val scannerBtn = dialog.findViewById<MaterialCardView>(R.id.scanner_btn)
            val wifiName1 = dialog.findViewById<TextView>(R.id.wifiName1)
            val closeBtn = dialog.findViewById<ImageView>(R.id.closeBtn)
            wifiName1.text=getWifiName()


            closeBtn.setOnClickListener {
                dialog?.dismiss()
            }

            scannerBtn.setOnClickListener {
                dialog.dismiss()
                if (Controller.isSender) {
                    Log.d("IzharMalik" , "Show exit dialog")
                    exitDialog()
//                    }

                } else {
                    Log.d("IzharMalik" , "Show exit dialog 1")
                    exitDialog1()
                }
                isTap = false



            }

            okayBtn.setOnClickListener {
                dialog.dismiss()
                isTap = false

            }

            dialog.show()
        } catch (e: RuntimeException) {
            e.printStackTrace()
            Log.e("checkFlow","error   ${e.message}")

        }

    }



    private fun exitDialog() {

        try {
            dialog12 = Dialog(this@SelectionActivity)
            dialog12.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog12.setCancelable(false)
            dialog12.setContentView(R.layout.dialog_loading_otp) //get layout from ExitDialog folder
            if (dialog12.window != null)
                dialog12.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))


            val editText = dialog12.findViewById<EditText>(R.id.editText)
            val okayBtn = dialog12.findViewById<MaterialButton>(R.id.okay_btn)
            val scannerBtn = dialog12.findViewById<MaterialButton>(R.id.scanner_btn)
            val closeBtn = dialog12.findViewById<ImageView>(R.id.closeBtn)


            closeBtn.setOnClickListener{
                dialog12.dismiss()
            }

            scannerBtn.setOnClickListener {
                isTap = false
                QRScanner=true
                if (!checkPermission1()) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(
                            this@SelectionActivity,
                            Manifest.permission.CAMERA
                        )
                    ) {

                        try {
                            AlertDialog.Builder(this@SelectionActivity)
                                .setMessage(resources.getText(R.string.camera_permission1))
                                .setCancelable(false)
                                .setPositiveButton(
                                    resources.getText(R.string.permission_dialog_positive_btn)
                                ) { _, _ ->
                                    try {
                                        val intent =
                                            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                                data = Uri.fromParts("package", packageName, null)
                                                addCategory(Intent.CATEGORY_DEFAULT)
                                                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                                addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                                                addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
                                            }

                                        startActivity(intent)
                                    } catch (e: Exception) {

                                    }

                                    isClicked = false
                                    isTap = false
                                }
                                .setNegativeButton(R.string.cancel) { _, _ ->
                                    isClicked = false
                                    isTap = false
                                }
                                .show()
                        } catch (e: IllegalStateException) {
                            e.printStackTrace()
                            isClicked = false
                        }

                    } else {

                        ActivityCompat.requestPermissions(
                            this, arrayOf(
                                Manifest.permission.CAMERA
                            ), CODE_REQ_PERMISSIONS3
                        )


                    }

                }
                else {
                    val intent = Intent(this@SelectionActivity, ScannerActivity::class.java)
                    startActivityForResult(intent, 101)
                    dialog12.dismiss()
                }
            }

            okayBtn.setOnClickListener {
                isTap = false
                QRScanner=false
                if(editText.text.toString()!="" ) {

                    val phoneNumber = editText.text.toString().trim { it <= ' ' }
                    val strLastFourDi = if (phoneNumber.length >= 4) phoneNumber.substring(phoneNumber.length - 4) else ""
                    val wm: WifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
                    val ip: String = Formatter.formatIpAddress(wm.connectionInfo.ipAddress)
                    val ipaa=ip.split("\\.(?=[^.]+$)".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0]

                    Controller.hostAddress = ipaa + "." + phoneNumber.dropLast(4)
                    try {
                        Constants.PORT = strLastFourDi.toInt()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    dialog12.dismiss()
                    var intent1 = Intent(this@SelectionActivity, DataTransferActivity::class.java)
                    intent1.putExtra("HOST_ADDRESS", Controller.hostAddress)
                    intent1.putExtra("intentFrom","activity")
                    intent1.putExtra("FROM_WIFI", Keyword.QR_CODE_TYPE_WIFI)
                    intent1.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP

                    startActivity(intent1)
                } else {
                    Toast.makeText(applicationContext, "Text field cannot be empty", Toast.LENGTH_SHORT).show()
                }

            }

            dialog12.show()
        } catch (e: RuntimeException) {
            Log.d("IzharMalik" , "${e.message}")
            e.printStackTrace()
        }

    }



    private fun exitDialog1() {

        val min = 2000
        val max = 8000
        val random = Random().nextInt(max - min + 1) + min

        Constants.PORT=random

        val wm: WifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val ip: String = Formatter.formatIpAddress(wm.connectionInfo.ipAddress)
        var text=ip.split(".").takeLast(1).joinToString(".")
        text+=random
        try {
            dialog12 = Dialog(this@SelectionActivity)
            dialog12.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog12.setCancelable(false)
            dialog12.setContentView(R.layout.dialog_generate_otp)
            if (dialog12.window != null) {
                dialog12.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            }

            val editText = dialog12.findViewById<TextView>(R.id.editText)
            val qrcode = dialog12.findViewById<ImageView>(R.id.qrCode)
            val closeBtn = dialog12.findViewById<ImageView>(R.id.closeBtn)

            editText.text = text
            createQRCode(text,qrcode)

            val receivingTask = ReceivingTask(this@SelectionActivity)
            receivingTask.asyncCall()

            closeBtn.setOnClickListener {
                dialog12?.dismiss()
                isTap = false
            }


            dialog12.show()
        } catch (e: RuntimeException) {
            e.printStackTrace()
        }

    }

    private fun createQRCode(qrValue: String,qrcode: ImageView) {
        Log.e(HotSpotActivity.TAG, "275 check bitmapvalue====$qrValue")
        Thread {
            val logo = BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher)
            val bitmap = CodeUtils.createQRCode(qrValue, 300, logo)
            runOnUiThread {
                qrcode.setImageBitmap(bitmap)
            }
        }.start()
    }


    private fun getWifiName(): String? {
        val wifiManager = applicationContext.getSystemService(WIFI_SERVICE) as WifiManager
        val wifiInfo: WifiInfo = wifiManager.connectionInfo
        val wifiName = if (wifiInfo.supplicantState == SupplicantState.COMPLETED) {
            wifiInfo.ssid
        } else {
            "Not Connected"
        }
        return wifiName
    }

    fun isGPSAllowed(): Boolean {
        var gpsEnabled: Boolean
        var networkEnabled: Boolean
        val lm: LocationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        try {
            gpsEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
        } catch (e: RuntimeException) {
            gpsEnabled = false
            e.printStackTrace()
        }
        try {
            networkEnabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        } catch (e: RuntimeException) {
            networkEnabled = false
            e.printStackTrace()
        }
        return (gpsEnabled && networkEnabled)
    }


    private fun checkPermission(): Boolean {
        val result = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)

        return result == PackageManager.PERMISSION_GRANTED

    }

    private fun checkPermission1(): Boolean {

        val result =
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            )

        return result == PackageManager.PERMISSION_GRANTED

    }


    fun hasInternetConnection(): Boolean {
        val connectivityManager = this.getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager

        val activeNetwork = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false

        return when {
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    }



    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CODE_REQ_PERMISSIONS)  {
            for (i in grantResults.indices) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(
                        this@SelectionActivity,
                        resources.getText(R.string.allow_all_permission),
                        Toast.LENGTH_SHORT
                    ).show()
                    isAllDone = false

                    return
                }
            }

            isAllDone = true
            val path = File(resources.getString(R.string.download_path))
            if (!path.exists()) {
                path.mkdirs()
            }
            /////////////////////////////////////////////////////////////////////////////////

            if (isGPSAllowed()) {

                if (flowCheck == "0") {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        var intent: Intent = if (value_not_show_again4)
                            Intent(this, HotSpotActivity::class.java)
                        else
                            Intent(this, InstructionsScreenActivity::class.java)
                        intent.putExtra("wifiOrHotspot", "receive_hotspot$value_flow")
                        intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                        startActivity(intent)
                    } else {
                        isTap = false
                        Toast.makeText(
                            this@SelectionActivity,
                            resources.getText(R.string.feature_not_supported),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        var intent: Intent = if (value_not_show_again8)
                            Intent(this, HotSpotActivity::class.java)
                        else
                            Intent(this, InstructionsScreenActivity::class.java)
                        intent.putExtra("wifiOrHotspot", "receive_hotspot$value_flow")
                        intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                        startActivity(intent)
                    } else {
                        isTap = false
                        Toast.makeText(
                            this@SelectionActivity,
                            resources.getText(R.string.feature_not_supported),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
            else {
                try {
                    AlertDialog.Builder(this@SelectionActivity)
                        .setMessage(resources.getText(R.string.permission_dialog_msg))
                        .setCancelable(false)
                        .setPositiveButton(
                            resources.getText(R.string.permission_dialog_positive_btn)
                        ) { _, _ ->
                            startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                            isClicked = false
                        }
                        .setNegativeButton(R.string.cancel) { _, _ ->
                            isClicked = false
                        }
                        .show()
                } catch (e: IllegalStateException) {
                    e.printStackTrace()
                    isClicked = false
                }
            }

            /////////////////////////////////////////////////////////////////////////////////
        }
        else if (requestCode == CODE_REQ_PERMISSIONS5)  {
            for (i in grantResults.indices) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(
                        this@SelectionActivity,
                        resources.getText(R.string.allow_all_permission),
                        Toast.LENGTH_SHORT
                    ).show()
                    isAllDone = false

                    return
                }
            }

            isAllDone = true
            val path = File(resources.getString(R.string.download_path))
            if (!path.exists()) {
                path.mkdirs()
            }
            /////////////////////////////////////////////////////////////////////////////////

            if (isGPSAllowed()) {
                connectivityViaWifi()

            }
            else {
                try {
                    AlertDialog.Builder(this@SelectionActivity)
                        .setMessage(resources.getText(R.string.permission_dialog_msg))
                        .setCancelable(false)
                        .setPositiveButton(
                            resources.getText(R.string.permission_dialog_positive_btn)
                        ) { _, _ ->
                            startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                            isClicked = false
                        }
                        .setNegativeButton(R.string.cancel) { _, _ ->
                            isClicked = false
                        }
                        .show()
                } catch (e: IllegalStateException) {
                    e.printStackTrace()
                    isClicked = false
                }
            }
            /////////////////////////////////////////////////////////////////////////////////
        }
        else if(requestCode == CODE_REQ_PERMISSIONS1){
            for (i in grantResults.indices) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(
                        this@SelectionActivity,
                        resources.getText(R.string.allow_all_permission),
                        Toast.LENGTH_SHORT
                    ).show()
                    isAllDone = false
                    return
                }
            }
            isAllDone = true
            val path = File(resources.getString(R.string.download_path))
            if (!path.exists()) {
                path.mkdirs()
            }

            ///////////////////////////////////////////////////////////////////////////////////////
            if(flowCheck=="0"){
                var intent:Intent = if(value_not_show_again2)
                    Intent(this, ScannerActivity::class.java)
                else
                    Intent(this, InstructionsScreenActivity::class.java)
                intent.putExtra("wifiOrHotspot","send_hotspot$flowCheck")
                intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                //  startActivity(intent)
                startActivityForResult(intent, 1)
            }else{
                var intent:Intent = if(value_not_show_again6)
                    Intent(this, ScannerActivity::class.java)
                else
                    Intent(this, InstructionsScreenActivity::class.java)
                intent.putExtra("wifiOrHotspot","send_hotspot$flowCheck")
                intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                //  startActivity(intent)
                startActivityForResult(intent, 1)

            }

            ///////////////////////////////////////////////////////////////////////////////////////
        }
        else if(requestCode == CODE_REQ_PERMISSIONS2){
            for (i in grantResults.indices) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(
                        this@SelectionActivity,
                        resources.getText(R.string.allow_all_permission),
                        Toast.LENGTH_SHORT
                    ).show()
                    isAllDone = false
                    return
                }
            }
            isAllDone = true
            val path = File(resources.getString(R.string.download_path))
            if (!path.exists()) {
                path.mkdirs()
            }

            if(flowCheck=="0"){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    var intent:Intent = if(value_not_show_again4)
                        Intent(this, HotSpotActivity::class.java)
                    else
                        Intent(this, InstructionsScreenActivity::class.java)
                    intent.putExtra("wifiOrHotspot","receive_hotspot$value_flow")
                    intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                    startActivity(intent)
                }
                else{
                    isTap = false
                    Toast.makeText(this@SelectionActivity,resources.getText(R.string.feature_not_supported),Toast.LENGTH_SHORT).show()
                }
            }else{
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    var intent:Intent = if(value_not_show_again8)
                        Intent(this, HotSpotActivity::class.java)
                    else
                        Intent(this, InstructionsScreenActivity::class.java)
                    intent.putExtra("wifiOrHotspot","receive_hotspot$value_flow")
                    intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                    startActivity(intent)
                }
                else{
                    isTap = false
                    Toast.makeText(this@SelectionActivity,resources.getText(R.string.feature_not_supported),Toast.LENGTH_SHORT).show()
                }
            }

            ///////////////////////////////////////////////////////////////////////////////////////
        }
        else if(requestCode == CODE_REQ_PERMISSIONS3){
            for (i in grantResults.indices) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(
                        this@SelectionActivity,
                        resources.getText(R.string.allow_all_permission),
                        Toast.LENGTH_SHORT
                    ).show()
                    isAllDone = false
                    return
                }
            }
            isAllDone = true
            val intent = Intent(this@SelectionActivity, ScannerActivity::class.java)
            startActivityForResult(intent, 101)
            try {
                dialog12.dismiss()
            }catch (e:Exception){

            }


            ///////////////////////////////////////////////////////////////////////////////////////
        }
        else if(requestCode == CODE_REQ_PERMISSIONS4){
            for (i in grantResults.indices) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(
                        this@SelectionActivity,
                        resources.getText(R.string.allow_all_permission),
                        Toast.LENGTH_SHORT
                    ).show()
                    isAllDone = false
                    return
                }
            }
            isAllDone = true


            ///////////////////////////////////////////////////////////////////////////////////////
        }

    }

}