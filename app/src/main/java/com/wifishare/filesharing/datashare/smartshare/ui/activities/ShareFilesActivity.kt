package com.wifishare.filesharing.datashare.smartshare.ui.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.wifishare.filesharing.datashare.smartshare.Controller
import com.wifishare.filesharing.datashare.smartshare.R
import com.wifishare.filesharing.datashare.smartshare.databinding.ActivityShareFilesBinding
import com.wifishare.filesharing.datashare.smartshare.ui.fragments.HomeFragment
import java.io.File

class ShareFilesActivity : AppCompatActivity() {

    private var binding : ActivityShareFilesBinding? = null
    private val CODE_REQ_PERMISSIONS = 665

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShareFilesBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        binding?.sendBtn?.setOnClickListener {
            if (!checkPermission()) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    requestPermission()
                }
                ActivityCompat.requestPermissions(
                    this, arrayOf(
                        Manifest.permission.CHANGE_NETWORK_STATE,
                        Manifest.permission.ACCESS_NETWORK_STATE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_WIFI_STATE,
                        Manifest.permission.CHANGE_WIFI_STATE
                    ), CODE_REQ_PERMISSIONS
                )
            } else {

                Controller.isSender = true
                if (!HomeFragment.isServiceRunning) {
                    if (SplashActivity.fileLoaded) {

                        val intent = Intent(this, FileChoosingActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                        intent.putExtra("CurrentItem", 15)
                        startActivity(intent)
                    } else {
                        Toast.makeText(
                            this,
                            "Please wait, data is being calculated",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        this,
                        "Please wait, data is being Transferred",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }



        binding?.receiveBtn?.setOnClickListener {
            if (!checkPermission()) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    requestPermission()
                }
                ActivityCompat.requestPermissions(
                    this, arrayOf(
                        Manifest.permission.CHANGE_NETWORK_STATE,
                        Manifest.permission.ACCESS_NETWORK_STATE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_WIFI_STATE,
                        Manifest.permission.CHANGE_WIFI_STATE
                    ), CODE_REQ_PERMISSIONS
                )
            } else {
                Controller.isSender = false

                if (!HomeFragment.isServiceRunning) {

                    val intent = Intent(this, SelectionActivity::class.java)
                    intent.putExtra("checkFlow", "startActivity")
                    intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                    startActivity(intent)
                } else {
                    Toast.makeText(
                        this,
                        "Please wait, data is being Transferred",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

    }


    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val path = File(resources.getString(R.string.download_path))
                if (!path.exists()) {
                    path.mkdirs()
                }
            }
        }


    private fun requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            try {
                val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                intent.addCategory("android.intent.category.DEFAULT")
                intent.data = Uri.parse(
                    String.format(
                        "package:%s",
                        applicationContext.packageName
                    )
                )
//                startActivityForResult(intent, CODE_REQ_PERMISSIONS)
                resultLauncher.launch(intent)
            } catch (e: Exception) {
                val intent = Intent()
                intent.action = Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
                //   startActivityForResult(intent, CODE_REQ_PERMISSIONS)
                resultLauncher.launch(intent)
            }
        }
    }


    private fun checkPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Environment.isExternalStorageManager()
        } else {
            val result =
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
            val result1 =
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED
        }
    }
    
    
    
}