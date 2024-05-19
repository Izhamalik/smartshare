package com.wifishare.filesharing.datashare.smartshare

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.support.multidex.MultiDex
import android.support.multidex.MultiDexApplication
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.initialize
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.wifishare.filesharing.datashare.smartshare.filesinfo.FileInfo
import java.util.concurrent.ConcurrentHashMap

class Controller : MultiDexApplication() {

    companion object {
        var showAd1=true
        var wifiORhotspot=""
        lateinit  var AD_UNIT_ID:String
        private  val LOG_TAG = "AppOpenAdManager"
        private var loadTime: Long = 0

        var currentActivity: Activity? = null
        var recovernoAdsApp: Boolean = false
        var isInterstitialAd: Boolean = false
        var home_native_reload_control = 1L
        var currentPosition = 0
        var app_open_ad=true

        var Splash_IAP =false
        var reload_permission_native =false
        var review_screen_home =false
        var languageNativePosition =false
        var onboarding_inter_check =false
        var permissionNativePosition =false
        var send_file_native_control =false
        var reviewScreenHome =false
        var inter_direction =false
        var Choosefiles_IAP=false
        var show_later_btn=false
        var immediate_update =false
        var showPremiumCrossSplash=""
        var showPremiumCrossChosefiles=""

        @SuppressLint("StaticFieldLeak")
        @JvmStatic
        var context: Context? = null

        var forceupdate = false
        var showrateusdialog = true
        var showrateusdialogcount = 3

        var isPurchasedApp=false
        var showAfterCount=2
        var currentCount=0
        /////////////Count Total Sent/ Received Files
        var totalReceived = 0
        var totalImages = 0
        var totalVideos = 0
        var totalAudios = 0
        var totalAPK = 0
        var totalDocuments = 0

        var mainImages = "Calculating..."
        var mainVideos = "Calculating..."
        var mainAudios = "Calculating..."
        var mainAPK = "Calculating..."
        var mainDocuments = "Calculating..."

        var isSender = true
        var totalSize = 0
        var otherDeviceName = ""
        var hostAddress = ""
        /////////Map to send files
        var mFileInfoMap: ConcurrentHashMap<String, FileInfo> = ConcurrentHashMap()

        fun addFileInfo(fileInfo: FileInfo) {
            if (!mFileInfoMap.containsKey(fileInfo.filePath)) {
                mFileInfoMap[fileInfo.filePath.toString()] = fileInfo
            }
        }

        fun isExist(fileInfo: FileInfo): Boolean {
            return mFileInfoMap.containsKey(fileInfo.filePath)
        }

        fun delFileInfo(fileInfo: FileInfo) {
            if (mFileInfoMap.containsKey(fileInfo.filePath)) {
                mFileInfoMap.remove(fileInfo.filePath)
            }
        }

        fun removeAll() {
            mFileInfoMap = ConcurrentHashMap()
        }
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    override fun onCreate() {
        super.onCreate()
        Firebase.initialize(this)
        FirebaseAnalytics.getInstance(this)
        FirebaseCrashlytics.getInstance()
        mFileInfoMap = ConcurrentHashMap()

        if (FirebaseApp.getApps(this@Controller).isEmpty()) {
            FirebaseApp.initializeApp(this@Controller)
            Log.e(ContentValues.TAG, "onCreate: Initialize")
            fetchAndActivate()
        } else {
            fetchAndActivate()
        }

        context = applicationContext

    }


    private fun fetchAndActivate() {
        val firebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
        val configSettings = com.google.firebase.remoteconfig.ktx.remoteConfigSettings {
            minimumFetchIntervalInSeconds = 3000.toLong()
        }
        firebaseRemoteConfig.setConfigSettingsAsync(configSettings)
        firebaseRemoteConfig.fetchAndActivate()
            .addOnCompleteListener {
                if (it.isSuccessful) {

                    forceupdate = firebaseRemoteConfig.getBoolean("forceupdate")
                    showrateusdialog = firebaseRemoteConfig.getBoolean("enable_rateusdialog")
                    showrateusdialogcount =
                        firebaseRemoteConfig.getString("rateusdialogshow_duration").toInt()

                }
            }
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
}