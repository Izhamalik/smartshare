package com.wifishare.filesharing.datashare.smartshare.task

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.work.OneTimeWorkRequest
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkManager
import com.wifishare.filesharing.datashare.smartshare.ui.activities.ReceivingDataActivity
import com.wifishare.filesharing.datashare.smartshare.ui.activities.work.WorkFortransferFiles
import com.wifishare.filesharing.datashare.smartshare.common.Constants
import com.wifishare.filesharing.datashare.smartshare.ui.fragments.HomeFragment
import com.wifishare.filesharing.datashare.smartshare.service.ForegroundService
import java.net.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicReference

class ReceivingTask(var context: Context) {

    private val TAG = "ReceivingTask"

    companion object {
        lateinit var client: Socket
        var isConnected = false
        lateinit var serverSocket: ServerSocket
    }

    fun asyncCall() {
        val getResult: AtomicReference<String> = AtomicReference()
        val executor: ExecutorService = Executors.newSingleThreadExecutor()
        executor.execute {
            val answerHTTP: String? = null
            serverSocket = ServerSocket()
            try {
                    if (!isConnected) {

                        serverSocket.bind(InetSocketAddress(Constants.PORT))
                        client = serverSocket.accept()
                        Log.e("aaaaaaaaaa","999999999 ${Constants.PORT}")

                        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.TIRAMISU){
                            try {
                                HomeFragment.isServiceRunning =true
                                val work = OneTimeWorkRequest.Builder(WorkFortransferFiles::class.java)
                                    .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                                    .build()
                                WorkManager.getInstance().enqueue(work)
                            }catch (e: IllegalStateException){
                                e.printStackTrace()
                            }
                        }else{

                            try {
                                if(!HomeFragment.isServiceRunning){
                                    ForegroundService.startService(context, "somes")
                                }

                            } catch (e: IllegalStateException) {
                                e.printStackTrace()
                            }
                        }

                        isConnected = true
                        Log.e("aaaaaaaaaa","ifffgff ${Constants.PORT}")

                    }
                    else{
                        Log.e("aaaaaaaaaa","else")
                    }


                if (isConnected) {
                    val intent = Intent(context, ReceivingDataActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                    intent.putExtra("intentFrom","receiveTask")

                    context.startActivity(intent)
                    Log.e("aaaaaaaaaa","isConnected")
                }
            }catch (e: SocketException){
                Log.e("aaaaaaaaaa","crash ${e.message}")

            }
            catch (e: RuntimeException) {
                Log.e("aaaaaaaaaa","crash2")

            } finally {


                if(!HomeFragment.isServiceRunning ){
                    Handler(Looper.getMainLooper()).postDelayed({
                        serverSocket.close()
                        isConnected = false
                    },15000)


                    Log.e("FlowCheck","Finally ")

                    Log.e("aaaaaaaaaa","Finally")
                }

            }
            getResult.set(answerHTTP)

        }
    }
}