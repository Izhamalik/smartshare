package com.wifishare.filesharing.datashare.smartshare.ui.activities

import android.content.Intent
import android.net.wifi.SupplicantState
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.camera.view.PreviewView
import com.google.zxing.Result
import com.king.zxing.CameraScan
import com.king.zxing.DefaultCameraScan
import com.wifishare.filesharing.datashare.smartshare.R
import com.wifishare.filesharing.datashare.smartshare.databinding.ActivityScannerBinding
import com.wifishare.filesharing.datashare.smartshare.util.NetworkConnectionLiveData

class ScannerActivity : AppCompatActivity(), CameraScan.OnScanResultCallback {

    private var binding : ActivityScannerBinding? = null
    private val isContinuousScan = false
    private var mCameraScan: CameraScan? = null
    private var previewView: PreviewView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScannerBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        binding?.backpressBtn?.setOnClickListener {
            onBackPressed()
        }

        previewView = findViewById(R.id.previewView)
        mCameraScan = DefaultCameraScan(this, previewView!!)
        mCameraScan!!.setOnScanResultCallback(this)
            .setVibrate(true)
            .startCamera()


        NetworkConnectionLiveData(this)
            .observe(this) { isConnected ->
                if (!isConnected) {

                    // Internet Not Available
                    return@observe
                }

                // Internet Available
            }
    }

    override fun onScanResultCallback(result: Result?): Boolean {
        handleBarcode(result!!.text.toString())
        Log.e("checkQRCODE",result!!.text.toString())
        return isContinuousScan
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

    @Synchronized
    private fun handleBarcode(code: String) {
        val intent = Intent()
        intent.putExtra("QR_RECEIVED", code)
        setResult(RESULT_OK, intent)
        finish()
    }
}