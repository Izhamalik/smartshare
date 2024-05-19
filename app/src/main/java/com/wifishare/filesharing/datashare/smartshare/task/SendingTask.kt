package com.wifishare.filesharing.datashare.smartshare.task


import android.content.Context
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import com.wifishare.filesharing.datashare.smartshare.ui.activities.SelectionActivity.Companion.QRScanner
import com.wifishare.filesharing.datashare.smartshare.ui.activities.SplashActivity.Companion.activity
import com.wifishare.filesharing.datashare.smartshare.broadcast.Helper
import com.wifishare.filesharing.datashare.smartshare.common.Constants
import com.wifishare.filesharing.datashare.smartshare.ui.fragments.HomeFragment.Companion.isServiceRunning
import com.wifishare.filesharing.datashare.smartshare.model.FileTransfer
import com.wifishare.filesharing.datashare.smartshare.otherutils.Keyword
import com.wifishare.filesharing.datashare.smartshare.service.ForegroundService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.BufferedOutputStream
import java.io.DataOutputStream
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.Socket
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicReference


class SendingTask(var context: Context, var hostAddress: String?, var isFrom: String?) {

    val TAG = "SendingTask"

    companion object{
        var socket: Socket? = null
    }

    fun asyncCall(arr: ArrayList<FileTransfer>) {
        val getResult: AtomicReference<String> = AtomicReference()
        val executor: ExecutorService = Executors.newSingleThreadExecutor()
        val handler = Handler(Looper.getMainLooper())
        executor.execute {
            val answerHTTP: String? = null
            try{
                socket!!.close()
            }catch (e:Exception){

            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                try {
                    socket = Socket()

                    socket!!.bind(null)
//                    try{
                    Log.e(TAG, "asyncCall: Connected${Constants.PORT} ========= $hostAddress")

                    socket!!.connect(InetSocketAddress(hostAddress, Constants.PORT), 10000)

                    socket!!.remoteSocketAddress.toString()

                    val outputStream = DataOutputStream(BufferedOutputStream(socket!!.getOutputStream()))
                    Helper(context).preparePacketForServer(arr, outputStream)

//                    }catch (e: Exception){
////                        Toast.makeText(context,"Failed to connect to /192.168.49.1 (port 1995)",Toast.LENGTH_LONG)
//                        Log.e(TAG, "File sending Exception: " + e.message)
//                    }

                } catch (e: Exception) {

                    Log.e(TAG, "File sending Exception: " + e.message)
                    CoroutineScope(Dispatchers.Main).launch {
                        if(!QRScanner)
                            Toast.makeText(context,"Invalid OTP Or Device is not on same wifi",Toast.LENGTH_SHORT).show()
                        else
                            Toast.makeText(context,"Invalid QR Code Or Device is not on same wifi",Toast.LENGTH_SHORT).show()
                    }

                    try {
                        socket!!.close()
                    }catch (e:Exception){

                    }

                    try{
                        activity.finish()
                        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.TIRAMISU) {
                            try {
                                ForegroundService.stopService(context)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }else{
                            isServiceRunning=false
                        }

                    }catch (e:Exception){

                    }

                } finally {
                    Log.e("FlowCheck","Finally 111111")

                    Log.e(TAG, "Finally Call")
//                    if (socket != null) {
//                        if (socket!!.isConnected ) {
//                            try {
//                                Log.e("FlowCheck","Finally 123333333")
//
//                                Handler(Looper.getMainLooper()).postDelayed({
//                                    socket!!.close()
//                                },1000)
//
//
//                            } catch (e: Exception) {
//                                e.printStackTrace()
//                            }
//                        }
//                    }
                }
            } else {
                if (isFrom.equals(Keyword.QR_CODE_TYPE_WIFI)) {
                    try {
                        socket = Socket()
                        socket!!.bind(null)
//                        try{
                    Log.e(TAG, "asyncCall: Catchsssssss $hostAddress===== ${Constants.PORT}")

                    socket!!.connect(InetSocketAddress(hostAddress, Constants.PORT), 10000)
                        val senderDeviceName = InetSocketAddress(hostAddress, Constants.PORT).hostName
                        val senderIpAddress = socket!!.inetAddress
                        val senderHostName = InetAddress.getByName(senderIpAddress.hostAddress).hostName


                        Log.e("aaaaaaaaaaaaa","bb  "+senderDeviceName+"=====${senderHostName}"+"=====${socket!!.inetAddress.canonicalHostName}")

                        val outputStream = DataOutputStream(BufferedOutputStream(socket!!.getOutputStream()))
                        Helper(context).preparePacketForServer(arr, outputStream)
//                        }catch (e: ConnectException){
//
////                            Toast.makeText(context,"Connection refused Please make a new Connection",Toast.LENGTH_LONG)
//                        }


                    }
                    catch (e: Exception) {
//                        Toast.makeText(context,"Connection refused Please make a new Connection",Toast.LENGTH_LONG)
                        Log.e(TAG, "File sending Exception: " + e.message)
                        CoroutineScope(Dispatchers.Main).launch {
                            if(!QRScanner)
                                Toast.makeText(context,"Invalid OTP Or Device is not on same wifi",Toast.LENGTH_SHORT).show()
                            else
                                Toast.makeText(context,"Invalid QR Code Or Device is not on same wifi",Toast.LENGTH_SHORT).show()

                        }
                        try{
                            socket!!.close()
                        }catch (e:Exception){

                        }
                        try{
                            activity.finish()
                            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.TIRAMISU) {

                                try {
                                    ForegroundService.stopService(context)
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }else{
                                isServiceRunning=false
                            }

                        }catch (e:Exception){

                        }
                    }
                    finally {
                        Log.e("FlowCheck","Finally ")

                        Log.e(TAG, "Finally Call")
                    }
                } else {
                    try{
                        Log.e(TAG, "asyncCall: aaaaaaaaaaa $hostAddress===== ${Constants.PORT}")

                        socket = Socket()
                        socket!!.bind(null)

                        socket!!.connect(InetSocketAddress(hostAddress, Constants.PORT), 10000)
                        Log.e("aaaaaaaaaaaaa","cc  "+ socket!!.inetAddress.hostName+"=====${socket!!.inetAddress.address}"+"=====${socket!!.inetAddress.canonicalHostName}")

                        socket.use {
                            val outputStream = DataOutputStream(
                                BufferedOutputStream(
                                    socket!!.getOutputStream()
                                )
                            )
                            Helper(context).preparePacketForServer(
                                arr,
                                outputStream
                            )
                            Log.e(TAG, "asyncCall: Connected")
                        }
                    }catch (e : Exception){
                        e.printStackTrace()
                        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.TIRAMISU) {
                            try {
                                ForegroundService.stopService(context)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                        else{
                            isServiceRunning=false
                        }


                    }
                    finally {
//                        try {
//                            Handler(Looper.getMainLooper()).postDelayed({
//                                socket!!.close()
//                            },15000)
//
//                        } catch (e: Exception) {
//                            e.printStackTrace()
//                        }

                    }

                }

                getResult.set(answerHTTP)
                handler.post {
                    //UI Thread work here
                    if (getResult.get() != null && !getResult.get().equals("")) {
                        Log.e(TAG, "asyncCall: " + getResult.get())
                    }
                }
            }
        }
    }

}