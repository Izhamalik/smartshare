package com.wifishare.filesharing.datashare.smartshare.ui.activities

import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.work.OneTimeWorkRequest
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkManager
import com.wifishare.filesharing.datashare.smartshare.Controller
import com.wifishare.filesharing.datashare.smartshare.Controller.Companion.context
import com.wifishare.filesharing.datashare.smartshare.R
import com.wifishare.filesharing.datashare.smartshare.broadcast.Helper
import com.wifishare.filesharing.datashare.smartshare.databinding.ActivityDataTransferBinding
import com.wifishare.filesharing.datashare.smartshare.ui.fragments.HomeFragment.Companion.isServiceRunning
import com.wifishare.filesharing.datashare.smartshare.interfaces.OnProgressChangListener
import com.wifishare.filesharing.datashare.smartshare.service.ForegroundService
import com.wifishare.filesharing.datashare.smartshare.task.ReceivingTask
import com.wifishare.filesharing.datashare.smartshare.ui.activities.SplashActivity.Companion.activity
import com.wifishare.filesharing.datashare.smartshare.ui.activities.SplashActivity.Companion.fileUpload
import com.wifishare.filesharing.datashare.smartshare.ui.activities.work.WorkFortransferFiles
import com.wifishare.filesharing.datashare.smartshare.ui.fragments.HomeFragment.Companion.isSocketClosed
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

class DataTransferActivity : AppCompatActivity() , OnProgressChangListener {

    private var binding : ActivityDataTransferBinding? = null

    private lateinit var helper: Helper
    lateinit var prefs: SharedPreferences
    lateinit var editor12: SharedPreferences.Editor
    var singleShow=true
    companion object {
        var transferDone = false
        var isFrom = ""
        var isFromIntent = ""
        var totalSent = 0
        var totalTransfer = 0

    }

    private var complete = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDataTransferBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        activity = this@DataTransferActivity
        prefs = getSharedPreferences("rating_preference", MODE_PRIVATE)
        editor12 = prefs.edit()
        binding?.backpressBtn?.setOnClickListener { onBackPressed() }
        context = this@DataTransferActivity
        isFromIntent = intent.getStringExtra("intentFrom").toString()

        Log.d("IzharMalik" , "intentfrom : $isFromIntent")


        if (isFromIntent != "service") {
            if (!isServiceRunning) {
                val deviceName = intent.getStringExtra("DEVICE_NAME").toString()
                isFrom = intent.getStringExtra("FROM_WIFI").toString()
                binding?.transferringTo?.text = "Transferring to: $deviceName"
            }
        }

        helper = Helper(this)
        helper.setProgressChangListener(this as OnProgressChangListener)

        if (isFromIntent != "service") {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.TIRAMISU) {
                try {
                    isServiceRunning = true
                    val work = OneTimeWorkRequest.Builder(WorkFortransferFiles::class.java)
                        .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                        .build()
                    WorkManager.getInstance().enqueue(work)
                } catch (e: IllegalStateException) {
                    e.printStackTrace()
                }
            } else {

                if (!isServiceRunning) {
                    try {
                        ForegroundService.startService(this@DataTransferActivity, "some")
                    } catch (e: IllegalStateException) {
                        e.printStackTrace()
                    }
                }
            }


            if (intent.hasExtra("HOST_ADDRESS")) {


            } else {
                transferDone = false
                Toast.makeText(this, "Connection Terminated", Toast.LENGTH_SHORT).show()
            }

        }


        binding?.btnDone?.setOnClickListener {
            WorkManager.getInstance().cancelAllWork()

            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.TIRAMISU) {

                try {
                    ForegroundService.stopService(this@DataTransferActivity)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            isServiceRunning = false
            onBackPressed()

        }


        try {
            if (isFromIntent == "service") {
                if (ForegroundService.valueDataTransfer >= 99) {

                    binding?.totalSend?.text = resources.getText(R.string.data_send_successfully)
                    binding?.layout1?.visibility = View.GONE
                    binding?.tickGreen?.visibility = View.VISIBLE
                    binding?.btnDone?.visibility = View.VISIBLE
                    totalFilesCounter()
                    if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.TIRAMISU) {

                        try {
                            ForegroundService.stopService(this@DataTransferActivity)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }

                    isServiceRunning = false
                }
            }

        } catch (e: Exception) {
            Log.e("ccccccccccccca", "=====${e.message}")
        }
    }


    override fun onResume() {
        super.onResume()
        if (!isServiceRunning)
            isSocketClosed = true
    }


    private fun totalFilesCounter() {
        Handler(Looper.getMainLooper()).postDelayed({
            binding?.llTabBar?.visibility = View.VISIBLE

            binding?.tabBar?.tvImagesSize?.text = Controller.totalImages.toString()
            binding?.tabBar?.tvVideoSize?.text = Controller.totalVideos.toString()
            binding?.tabBar?.tvAudioSize?.text = Controller.totalAudios.toString()
            binding?.tabBar?.tvDocumentsSize?.text = Controller.totalDocuments.toString()
            binding?.tabBar?.tvAppsSize?.text = Controller.totalAPK.toString()
            binding?.totalData?.text =
                "Total Sent Files: ${Controller.totalImages + Controller.totalVideos + Controller.totalAudios + Controller.totalDocuments + Controller.totalAPK}"

        }, 1000)

    }

    override fun totalFiles(totalFiles: Int) {
        CoroutineScope(Dispatchers.Main).launch {
            totalTransfer = totalFiles
            binding?.totalSend?.text = "Sent  $totalSent out of $totalTransfer "

        }

    }

    override fun onProgressChanged(fileName: String?, progress: Float) {
        CoroutineScope(Dispatchers.Main).launch {
            binding?.totalSend?.text = "Sending  $totalSent out of $totalTransfer "

            binding?.circleView?.progress = progress.roundToInt()
            binding?.percentAge?.text = "${progress.roundToInt()}%"
            if (progress.roundToInt() == 100) {
//                if (!prefs.getBoolean("already_review", false) && singleShow){
//                    singleShow=false
//                    buildRateUsDialog()
//                }

                binding?.totalSend?.text = resources.getText(R.string.data_sent_successfully)
                binding?.layout1?.visibility = View.GONE
                binding?.tickGreen?.visibility = View.VISIBLE
                binding?.btnDone?.visibility = View.VISIBLE
                totalFilesCounter()
                isServiceRunning = false
                totalSent = 0
                totalTransfer = 0


                try {
//                socket!!.close()
                    Controller.totalReceived = 0
                    ReceivingDataActivity.totalCount = 0
                    totalSent = 0
                    totalTransfer = 0
                } catch (e: Exception) {
                }

                try {
                    Handler(Looper.getMainLooper()).postDelayed({
                        try {
                            ReceivingTask.serverSocket.close()
                        } catch (e: UninitializedPropertyAccessException) {

                        }

                    }, 15000)
                } catch (e: Exception) {

                }

                try {
                    HotSpotActivity.manager.disable()
                } catch (e: Exception) {

                }
            }
        }




        if (progress.roundToInt() % 5 == 0 && progress!=0f) {
            try {
                ForegroundService.updateProgress(this@DataTransferActivity, progress.roundToInt())
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onTransferFinished(file: String?) {
        CoroutineScope(Dispatchers.Main).launch {
//            totalSent++
//            when {
//                totalSent < totalTransfer -> {
//                    binding.totalSend.text = "Sent  $totalSent out of $totalTransfer "
//                    binding.circleView.progress=0
//                }
//                totalSent == totalTransfer -> {

//                }
//            }
//                    try {
//                        ForegroundService.stopService(this@DataTransferActivity)
//                    }catch (e:Exception){
//
//                    }
            binding?.totalSend?.text = resources.getText(R.string.data_sent_successfully)
            binding?.layout1?.visibility = View.GONE
            binding?.tickGreen?.visibility = View.VISIBLE
            binding?.btnDone?.visibility = View.VISIBLE
            totalFilesCounter()
        }

    }




    override fun onBackPressed() {
        if (isServiceRunning) {
        } else {
            fileUpload = true
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NO_HISTORY
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()

            Controller.totalDocuments = 0
            Controller.totalAPK = 0
            Controller.totalAudios = 0
            Controller.totalVideos = 0
            Controller.totalImages = 0
        }
    }
}