package com.wifishare.filesharing.datashare.smartshare.service

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.OneTimeWorkRequest
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkManager
import com.wifishare.filesharing.datashare.smartshare.R
import com.wifishare.filesharing.datashare.smartshare.Controller
import com.wifishare.filesharing.datashare.smartshare.Controller.Companion.totalReceived
import com.wifishare.filesharing.datashare.smartshare.ui.activities.DataTransferActivity
import com.wifishare.filesharing.datashare.smartshare.ui.activities.DataTransferActivity.Companion.totalSent
import com.wifishare.filesharing.datashare.smartshare.ui.activities.DataTransferActivity.Companion.totalTransfer
import com.wifishare.filesharing.datashare.smartshare.ui.activities.HotSpotActivity.Companion.manager
import com.wifishare.filesharing.datashare.smartshare.ui.activities.ReceivingDataActivity
import com.wifishare.filesharing.datashare.smartshare.ui.activities.work.WorkFortransferFiles
import com.wifishare.filesharing.datashare.smartshare.broadcast.Helper
import com.wifishare.filesharing.datashare.smartshare.ui.fragments.HomeFragment.Companion.isServiceRunning
import com.wifishare.filesharing.datashare.smartshare.task.ReceivingTask

class ForegroundService: Service() {
    override fun onCreate() {
        super.onCreate()
        service =this
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        makeForeground(this)

    }

    override fun onBind(intent: Intent?): IBinder? {
        throw UnsupportedOperationException()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        try {
            isServiceRunning=true
            val work = OneTimeWorkRequest.Builder(WorkFortransferFiles::class.java)
                .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                .build()
            WorkManager.getInstance().enqueue(work)
        }catch (e: IllegalStateException){
            e.printStackTrace()
        }
        return START_STICKY
    }



    companion object {
        private const val ONGOING_NOTIFICATION_ID = 101
//        private const val CHANNEL_ID = "10011"
        private const val CHANNEL_ID = "23421"
         lateinit var service: ForegroundService
        private var EXTRA_DEMO = ""
        private lateinit var notificationManager: NotificationManager
        lateinit var notification: Notification
        lateinit var notificationBuilder: NotificationCompat.Builder
        var valueDataTransfer=0

        fun makeForeground(context: Context) {
            val intent = if (Controller.isSender)
                Intent(context, DataTransferActivity::class.java)
            else
                Intent(context, ReceivingDataActivity::class.java)

            intent.putExtra("intentFrom","service")
//            intent.putExtra("totalnumber",total)

            val pendingIntent = PendingIntent.getActivity(
                context, 0, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )


            createServiceNotificationChannel()

            EXTRA_DEMO = if(Controller.isSender)
                "Data Transferring..."
            else
                "Data Receiving..."


//Set notification information:
            notificationBuilder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationCompat.Builder(context, CHANNEL_ID)
            }else{
                NotificationCompat.Builder(context)
            }

            var aa=0
            aa = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                NotificationManager.IMPORTANCE_HIGH
            }else{
                NotificationManager.IMPORTANCE_LOW
            }

                notificationBuilder.setOngoing(true)
                .setContentTitle( context.getString(R.string.app_name))
                .setContentText(EXTRA_DEMO)
                .setVibrate(null)
                .setPriority(aa)
                .setSound(null)
                .setCategory(NotificationCompat.CATEGORY_PROGRESS)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setAutoCancel(false)
                .setSilent(true)
                .setContentIntent(pendingIntent)


            with(NotificationManagerCompat.from(context)) {
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                    return
                }
                notify(ONGOING_NOTIFICATION_ID, notificationBuilder.build())
            }

            notification = notificationBuilder.build()
            service.startForeground(ONGOING_NOTIFICATION_ID, notification)

        }

        fun updateProgress(context: Context,progress:Int){
            if(Controller.isSender){
                Helper.progressNotificationSender=progress
            }
            else{
                Helper.progressNotification=progress
            }

            valueDataTransfer =progress

            notificationBuilder.setProgress(100,progress,false)
            if(progress==100){
                EXTRA_DEMO = if(Controller.isSender)
                    context.getString(R.string.data_send_successfully)
                else
                    context.getString(R.string.data_received_successfully)
                notificationBuilder.setContentText(EXTRA_DEMO)
            }

            with(NotificationManagerCompat.from(context)) {
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                    return
                }
                notify(ONGOING_NOTIFICATION_ID, notificationBuilder.build())
            }

        }


        private fun createServiceNotificationChannel() {
            if (Build.VERSION.SDK_INT < 26) {
                return // notification channels were added in API 26
            }

            val channel = NotificationChannel(CHANNEL_ID, "Foreground Service channel", NotificationManager.IMPORTANCE_HIGH)
            channel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            channel.enableVibration(false)
            channel.enableLights(false)
            channel.setSound(null, null);
            notificationManager.createNotificationChannel(channel)
        }



        fun startService(context: Context, demoString: String) {
            isServiceRunning=true
            val intent = Intent(context, ForegroundService::class.java)

            try {
                context.stopService(intent)
            }catch (e:RuntimeException){

            }
            try {
                intent.putExtra(EXTRA_DEMO, demoString)
                if (Build.VERSION.SDK_INT < 26) {
                    context.startService(intent)
                } else {
                    context.startForegroundService(intent)
                }
            }catch (e:Exception){

            }

        }


        fun stopService(context: Context) {
            Log.e("FlowCheck", "stopService")
            isServiceRunning = false
            val intent = Intent(context, ForegroundService::class.java)
            context.stopService(intent)
            try {
//                socket!!.close()
                totalReceived = 0
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
                manager.disable()
            } catch (e: Exception) {

            }

        }

    }

}