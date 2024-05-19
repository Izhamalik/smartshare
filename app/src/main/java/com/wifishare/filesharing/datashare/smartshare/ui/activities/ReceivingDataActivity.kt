package com.wifishare.filesharing.datashare.smartshare.ui.activities

import android.content.SharedPreferences
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import com.wifishare.filesharing.datashare.smartshare.Controller
import com.wifishare.filesharing.datashare.smartshare.Controller.Companion.totalReceived
import com.wifishare.filesharing.datashare.smartshare.R
import com.wifishare.filesharing.datashare.smartshare.broadcast.Helper
import com.wifishare.filesharing.datashare.smartshare.databinding.ActivityReceivingDataBinding
import com.wifishare.filesharing.datashare.smartshare.interfaces.OnProgressChangListener
import com.wifishare.filesharing.datashare.smartshare.service.ForegroundService
import com.wifishare.filesharing.datashare.smartshare.ui.fragments.HomeFragment.Companion.isServiceRunning
import com.wifishare.filesharing.datashare.smartshare.ui.fragments.HomeFragment.Companion.isSocketClosed
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

class ReceivingDataActivity : AppCompatActivity() , OnProgressChangListener {

    private var binding : ActivityReceivingDataBinding? = null

    private lateinit var helper: Helper
    var isFromIntent=""
    lateinit var prefs: SharedPreferences
    lateinit var editor12: SharedPreferences.Editor
    var singleShow=true

    companion object{
        var totalCount = 0
    }

    private var complete = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReceivingDataBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        SplashActivity.activity =this@ReceivingDataActivity

        prefs = getSharedPreferences("rating_preference", MODE_PRIVATE)
        editor12=prefs.edit()
        isFromIntent = intent.getStringExtra("intentFrom").toString()


        helper = Helper(this)
        helper.setProgressChangListener(this as OnProgressChangListener)

        try {
            if(isFromIntent=="service"){
                if(ForegroundService.valueDataTransfer >=99){
                    binding?.totalSend?.text = resources.getText(R.string.data_received_successfully)
                    binding?.circleView?.visibility = View.GONE
                    binding?.layout1?.visibility= View.GONE
                    binding?.percentAge?.visibility = View.GONE
                    binding?.tickGreen?.visibility = View.VISIBLE
                    binding?.btnDone1?.visibility = View.VISIBLE
                    totalFilesCounter()
                    if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.TIRAMISU) {

                        try {
                            ForegroundService.stopService(this@ReceivingDataActivity)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
                Log.e("ccccccccccccca", ""+ ForegroundService.valueDataTransfer)
            }


        }catch (e:Exception){
            Log.e("ccccccccccccca", "=====${e.message}")
        }

        binding?.backpressBtn?.setOnClickListener {
            onBackPressed()
        }


        binding?.btnDone1?.setOnClickListener {
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.TIRAMISU) {

                try {
                    ForegroundService.stopService(this@ReceivingDataActivity)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            isServiceRunning=false
            onBackPressed()

        }
    }

    override fun onResume() {
        super.onResume()
        if (!isServiceRunning)
            isSocketClosed = true

    }

    override fun totalFiles(totalFiles: Int) {
        runOnUiThread {
            totalCount = totalFiles
            binding?.totalSend?.text = "Received  $totalReceived out of $totalCount "
        }
    }

    override fun onProgressChanged(fileName: String?, progress: Float) {

        CoroutineScope(Dispatchers.Main).launch {
            binding?.totalSend?.text = "Received  $totalReceived out of $totalCount "

            binding?.circleView?.progress=progress.roundToInt()
            binding?.percentAge?.text = "${progress.roundToInt()}%"

            if(progress.roundToInt()==100){

                binding?.totalSend?.text = resources.getText(R.string.data_received_successfully)
                binding?.circleView?.visibility = View.GONE
                binding?.layout1?.visibility=View.GONE
                binding?.percentAge?.visibility = View.GONE
                binding?.tickGreen?.visibility = View.VISIBLE
                binding?.btnDone1?.visibility = View.VISIBLE
                totalFilesCounter()
                totalReceived=0
                isServiceRunning=false
                totalCount=0

            }



        }



        if(progress.roundToInt()%5==0 && progress!=0f) {
//            CoroutineScope(Dispatchers.IO).launch {

            try {
                ForegroundService.updateProgress(this@ReceivingDataActivity, progress.roundToInt())
            } catch (e: Exception) {

            }
//            }
        }

        if(progress>=99){
            try {
                ForegroundService.updateProgress(this@ReceivingDataActivity, 100)
            } catch (e: Exception) {

            }
        }
    }

    override fun onTransferFinished(file: String?) {

        CoroutineScope(Dispatchers.Main).launch {
//            totalReceived++
//            when {
//                totalReceived < totalCount -> {
//                    binding.btnDone1.visibility = View.GONE
//                    binding.tickGreen.visibility = View.GONE
//                    binding.totalSend.text = "Received  $totalReceived out of $totalCount "
//                    binding.circleView.progress=0
//                    Log.e(TAG, "onTransferFinished: sent Grater $totalReceived <  total Transfer $totalCount")
//                }
//
//                (totalReceived == totalCount) and (totalCount != 0) -> {

            binding?.totalSend?.text = "Data Received Successfully!"
            totalFilesCounter()
            binding?.circleView?.visibility = View.GONE
            binding?.layout1?.visibility=View.GONE
//                    try {
//                        ForegroundService.stopService(this@ReceivingDataActivity)
//                    }catch (e:Exception){
//
//                    }
            binding?.tickGreen?.visibility = View.VISIBLE
            binding?.btnDone1?.visibility = View.VISIBLE
            totalReceived=0
            totalCount=0
//                }

            /* totalSent >= totalTransfer -> {
                 binding.totalSend.text = "Data Received Successfully!"
                 totalFilesCounter()
                 binding.circleView.visibility = View.GONE
                 binding.tickGreen.visibility = View.VISIBLE
                 binding.btnDone.visibility = View.VISIBLE
             }*/

//            }
        }
    }

    private fun totalFilesCounter() {
        Handler(Looper.getMainLooper()).postDelayed({
            binding?.llTabBar?.visibility = View.VISIBLE

            binding?.tabBar?.tvImagesSize?.text = Controller.totalImages.toString()
            binding?.tabBar?.tvVideoSize?.text = Controller.totalVideos.toString()
            binding?.tabBar?.tvAudioSize?.text = Controller.totalAudios.toString()
            binding?.tabBar?.tvDocumentsSize?.text = Controller.totalDocuments.toString()
            binding?.tabBar?.tvAppsSize?.text = Controller.totalAPK.toString()
            binding?.totalData?.text = "Total Received Files: ${Controller.totalImages+Controller.totalVideos+Controller.totalAudios+Controller.totalDocuments+Controller.totalAPK}"

        },1000)

    }
}