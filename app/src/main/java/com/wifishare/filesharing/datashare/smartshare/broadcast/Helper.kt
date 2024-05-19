package com.wifishare.filesharing.datashare.smartshare.broadcast

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.widget.Toast
import com.wifishare.filesharing.datashare.smartshare.R
import com.wifishare.filesharing.datashare.smartshare.Controller
import com.wifishare.filesharing.datashare.smartshare.Controller.Companion.totalReceived
import com.wifishare.filesharing.datashare.smartshare.ui.activities.DataTransferActivity.Companion.totalSent
import com.wifishare.filesharing.datashare.smartshare.ui.activities.ReceivingDataActivity.Companion.totalCount
import com.wifishare.filesharing.datashare.smartshare.ui.activities.SplashActivity
import com.wifishare.filesharing.datashare.smartshare.interfaces.OnProgressChangListener
import com.wifishare.filesharing.datashare.smartshare.model.FileTransfer
import java.io.*
import java.util.*


class Helper(var context: Context) {
    var TAG = "Helper"
    var TAG1 = "HelperNaeem"

    fun setProgressChangListener(progressChangListener: OnProgressChangListener) {
        progressChangListener1 = progressChangListener
//        Log.e(TAG1, "setProgressChangListener")
    }

    fun preparePacketForServer(uriList: ArrayList<FileTransfer>, outputStream: DataOutputStream) {
        try {
//            Log.e("serverCount", "preparePacketForServer: Called")
            writeFiles(uriList, outputStream)
            outputStream.close()
        } catch (e: IOException) {
//            Log.e(TAG, "PreparePacket Exception    " + e.message)
        }
    }

    @Throws(IOException::class)
    private fun writeFiles(uriList: ArrayList<FileTransfer>, outputStream: DataOutputStream) {
        //number of files
            outputStream.writeInt(uriList.size)
            outputStream.flush()
        var a=0f
        var totalFileSize=0F
        var conditon_once=true
        var currentBytesWritten = 0f

        uriList.forEach {
            totalFileSize += it.fileLength
//            Log.e("FlowCheck","====${totalFileSize}")
        }



        if (progressChangListener1 != null) {
                progressChangListener1!!.totalFiles(uriList.size)
        }

            for (i in uriList.indices) {
                //name of file
                conditon_once=true
                val fileName = uriList[i].fileName
                outputStream.writeUTF(fileName)
                outputStream.flush()

                var fileSize = uriList[i].fileLength
                outputStream.writeLong(fileSize)
                outputStream.flush()
                val buf = ByteArray(1024)
                var len: Int = -2
                val inputStream: InputStream = FileInputStream(uriList[i].filePath)
                while (fileSize > 0 && inputStream.read(buf, 0, buf.size.toLong().coerceAtMost(fileSize).toInt()).also { len = it } != -1) {
                    outputStream.write(buf, 0, len)
                    fileSize -= len.toLong()
                    currentBytesWritten += len
                    var b =(currentBytesWritten/totalFileSize)*100
                    if(b.toInt()== (100/uriList.size) && conditon_once && uriList.size>1 && b==1f){
                        conditon_once=false
                        a += b
                    }

                    var e= a+b

//                    if(totalSent== totalTransfer-1){
//                        try {
//                            ForegroundService.stopService(context)
//                        }catch (e:Exception){
//                            e.printStackTrace()
//                        }
//                        if (progressChangListener1 != null) {
//                            progressChangListener1!!.onTransferFinished(
//                                uriList[i].fileName
//                            )
//                        }
//                    }
                    Log.e("FlowCheck","$currentBytesWritten=====$totalFileSize====$b====$e")

//                    Log.e("checkReceive","send File    $progress   ======== $total")

                    if (progressChangListener1 != null) {
//                        Log.e(TAG, " Progress $progress")
                        progressChangListener1!!. onProgressChanged(
                            uriList[i].fileName, e
                        )
                    }
                }

                totalSent++

                when (File(uriList[i].filePath.toString()).absolutePath.substring(
                    File(uriList[i].filePath.toString()).absolutePath.lastIndexOf(".")).lowercase()) {
                    ".jpg", ".png", ".jpeg" ,".PNG" -> {
                        Controller.totalImages++
                    }
                    ".mp4" , ".mov", ".avi", ".wmv", ".mkv" -> {
                        Controller.totalVideos++
                    }
                    ".mp3", ".wav", ".m4a", ".flac" -> {
                        Controller.totalAudios++
                    }
                    ".apk" -> {
                        Controller.totalAPK++
                    }
                    ".doc", ".pdf", ".docx", ".xls", ".xlsx", ".ppt", ".pptx", ".txt" -> {
                        Controller.totalDocuments++
                    }
                }

                outputStream.flush()


        }
    }



    @SuppressLint("SuspiciousIndentation")
    fun receiveFiles(inputStream: DataInputStream): Boolean {

//        val work = OneTimeWorkRequest.Builder(WorkForRecievingrFiles::class.java)
//            .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
//            .build()
//        WorkManager.getInstance().enqueue(work)

        var noOfFiles=0
        try {
            noOfFiles = inputStream.readInt()
            totalCount=noOfFiles
        } catch (e:EOFException){
            e.printStackTrace()

        }catch (e: Exception) {
            e.printStackTrace()
        }

        var a=0f
        var totalFileSize=0F
        var conditon_once=true
            if (progressChangListener1 != null) {
                progressChangListener1!!.totalFiles(noOfFiles)
            }

        try {
            for (i in 0 until noOfFiles) {
                conditon_once=true

                var f = File(context.getString(R.string.download_path) + inputStream.readUTF())
                val dirs = File(f.parent!!.toString())
                if (!dirs.exists()) dirs.mkdirs()
                if(!f.exists())
                    f.createNewFile()
                else{
                    f=File(context.getString(R.string.download_path)+"file_"+System.currentTimeMillis()+f.absolutePath.substring(f.absolutePath.lastIndexOf(".")))
                    f.createNewFile()
                }

                var fileSize = inputStream.readLong()
                totalFileSize= fileSize.toFloat()

//                val lenOfFile: Long = f.length()
//                Log.e(TAG1, "LENGTH======= $lenOfFile")

                val buf = ByteArray(1024)
                var len: Int = -2
                var currentBytesWritten = 0f
                val outputStream = FileOutputStream(f)


                while (fileSize > 0 && inputStream.read(
                        buf,
                        0,
                        buf.size.toLong().coerceAtMost(fileSize).toInt()
                    ).also { len = it } != -1
                ) {
                    outputStream.write(buf, 0, len)
                    fileSize -= len.toLong()
                    currentBytesWritten += len
                    var b =(currentBytesWritten/totalFileSize)
                    var c= b *100
                    var d= c / noOfFiles
                    if(d.toInt()== (100/noOfFiles) && conditon_once && noOfFiles>1 && b==1f){
                        conditon_once=false
                        a += d
                        d=0f
                    }
                    var e= a+d
                    Log.e("FlowCheck", "receiveFiles: $e")

                    Log.e("FlowCheck", "currentBy == $a  ==== TotalFileSize == $c  ==== TotalFile  ==  $d  ====  Percent  ==  $e%")
                    if (progressChangListener1 != null) {
//                        Log.e(TAG, " Progress $progress")
                        progressChangListener1!!. onProgressChanged(
                            f.name, e
                        )
                    }

//                            else{
//                                Log.e(TAG1, "receiveFiles: $totalCount=========$totalReceived")
//                                    e=100f
//                                totalReceived= totalCount
//                                progressChangListener1!!.onProgressChanged(f.name, e)
////                                if(totalCount-1== totalReceived){
////
////                                    try {
////                                        progressChangListener1!!.onTransferFinished(f.name)
////                                    } catch (e: RuntimeException) {
////                                        e.printStackTrace()
////                                    }
////                                }
//
//                            }


                }

//                Log.e("images",""+f.absolutePath.substring(f.absolutePath.lastIndexOf(".")))
                totalReceived++

                when (f.absolutePath.substring(f.absolutePath.lastIndexOf(".")).lowercase()) {
                    ".jpg", ".png", ".jpeg" , ".PNG" -> {
                        Controller.totalImages++
                    }
                    ".mp4" , ".mov", ".avi", ".wmv", ".mkv" -> {
                        Controller.totalVideos++
                    }
                    ".mp3", ".wav", ".m4a", ".flac" -> {
                        Controller.totalAudios++
                    }
                    ".apk" -> {
                        Controller.totalAPK++
                    }
                    ".doc", ".pdf", ".docx", ".xls", ".xlsx", ".ppt", ".pptx", ".txt" -> {
                        Controller.totalDocuments++
                    }
                }

                outputStream.close()
            }
        }
        catch (e: IOException) {
            try{
                Toast.makeText(context,"Connection Lost", Toast.LENGTH_SHORT).show()
                SplashActivity.activity.finish()
            }catch (e:Exception){

            }
        }
        return noOfFiles != 0
    }



    fun processPacketAtServer(inputStream: DataInputStream): Boolean {
        return try {
            val res = receiveFiles(inputStream)
            inputStream.close()
            res
        } catch (e: IOException) {
            Log.e(TAG1, "Received Exception: $e")
            false
        }
    }

    companion object {
        var progressNotification=0
        var progressNotificationSender=0

        private var progressChangListener1: OnProgressChangListener? = null
        fun copyFile(inputStream: InputStream, outputStream: OutputStream): Boolean {
            val buf = ByteArray(1024)
            var len: Int
            try {
                while (inputStream.read(buf).also { len = it } != -1) {
                    outputStream.write(buf, 0, len)
                }
                outputStream.close()
                inputStream.close()
            } catch (e: IOException) {
                return false
            }
            return true
        }
    }
}