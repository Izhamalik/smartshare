package com.wifishare.filesharing.datashare.smartshare.ui.activities.work

import android.annotation.TargetApi
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.*
import com.wifishare.filesharing.datashare.smartshare.task.SendingTask
import com.wifishare.filesharing.datashare.smartshare.Controller
import com.wifishare.filesharing.datashare.smartshare.ui.activities.DataTransferActivity
import com.wifishare.filesharing.datashare.smartshare.broadcast.Helper
import com.wifishare.filesharing.datashare.smartshare.model.FileTransfer
import com.wifishare.filesharing.datashare.smartshare.task.ReceivingTask
import com.wifishare.filesharing.datashare.smartshare.util.Md5Util
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.DataInputStream
import java.io.File
import java.util.*


class WorkFortransferFiles(appContext: Context,var workerParams: WorkerParameters): CoroutineWorker(appContext, workerParams) {
    private var mNotifyManager: NotificationManager? = null
    private var mBuilder: NotificationCompat.Builder? = null
    private val PROGRESS = "PROGRESS"

    var context12:Context=appContext
        var NOTIFICATION_ID=101
    override suspend fun getForegroundInfo(): ForegroundInfo {
        Log.e("checkedWorkcoroutine", "getForegroundInfo: ")

//        return ForegroundInfo(NOTIFICATION_ID, createNotification(applicationContext, id, applicationContext.getString(R.string.app_name)))
        return ForegroundInfo(NOTIFICATION_ID, createNotification(applicationContext, id, applicationContext.getString(com.wifishare.filesharing.datashare.smartshare.R.string.app_name)))
    }

    override suspend fun doWork(): Result  {

        if(Controller.isSender){
            try {2

                val sendingTask = SendingTask(applicationContext, Controller.hostAddress, DataTransferActivity.isFrom)

                val arr: ArrayList<FileTransfer> = ArrayList()
                Controller.mFileInfoMap.forEach {
                    val outputFile = File(it.value.filePath.toString())
                    val fileTransfer = FileTransfer()
                    val fileName = outputFile.name
                    val fileMa5 = Md5Util.getMd5(outputFile)
                    val fileLength = outputFile.length()
                    fileTransfer.fileName = fileName
                    fileTransfer.md5 = fileMa5
                    fileTransfer.fileLength = fileLength
                    fileTransfer.totalFiles = Controller.mFileInfoMap.size
                    fileTransfer.filePath = outputFile.path
                    arr.add(fileTransfer)
                    DataTransferActivity.transferDone = true
                }
                sendingTask.asyncCall(arr)
            } catch (exception: InterruptedException) {

            }catch (e:Exception){

            }
        }
        else{
//            try {
            CoroutineScope(Dispatchers.IO).launch{
                try {
                    val inputStream = DataInputStream(
                        withContext(Dispatchers.IO) {
                            ReceivingTask.client.getInputStream()
                        }
                    )
                    withContext(Dispatchers.IO) {
                            Helper(applicationContext).receiveFiles(inputStream)
                    }

                    Log.e("aaaaaaaaaaaaa", "receiveFiles==========aaaaaaaaaaaaaaaaa: ")

                }catch (e:UninitializedPropertyAccessException){

                }
                catch (e:Exception){
                }
            }
//            }catch (e:Exception){
//
//            }
        }
        return Result.success()
    }

    fun createNotification(
        context: Context,
        workRequestId: UUID,
        notificationTitle: String
    ): Notification {
        val channelId = context.getString(com.wifishare.filesharing.datashare.smartshare.R.string.notification_channel_id)
        val name = context.getString(com.wifishare.filesharing.datashare.smartshare.R.string.channel_name)

        val builder = NotificationCompat.Builder(context, channelId)
            .setContentTitle(notificationTitle)
            .setTicker(notificationTitle)
            .setSmallIcon(com.wifishare.filesharing.datashare.smartshare.R.mipmap.ic_launcher)


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(context, channelId, name).also {
                builder.setChannelId(it.id)
            }
        }
        return builder.build()
    }

    @TargetApi(Build.VERSION_CODES.O)
    fun createNotificationChannel(
        context: Context,
        channelId: String,
        name: String,
        notificationImportance: Int = NotificationManager.IMPORTANCE_HIGH
    ): NotificationChannel {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        return NotificationChannel(channelId, name, notificationImportance
        ).also { channel -> notificationManager.createNotificationChannel(channel)
        }
    }

}