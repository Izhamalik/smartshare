package com.wifishare.filesharing.datashare.smartshare.ui.activities

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.wifishare.filesharing.datashare.smartshare.R
import com.wifishare.filesharing.datashare.smartshare.databinding.ActivitySplashBinding

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private var binding: ActivitySplashBinding? = null

    companion object {
        var checkAd_loaded = false
        var fileUpload = true
        var flow = false
        lateinit var activity: AppCompatActivity
        var ipAddressForSenderPrevious = ""
        var fileLoaded = false
        var refershSplahNative = 0
        var splashInterShown = false
        var i = (1..5).random()

        var priceSplash = ""
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }, 4000)
    }
}