package com.wifishare.filesharing.datashare.smartshare.model

import android.app.Activity
import android.content.Context
import android.os.Environment
import android.os.StatFs
import androidx.lifecycle.*
import com.wifishare.filesharing.datashare.smartshare.ui.activities.SplashActivity.Companion.fileUpload
import com.wifishare.filesharing.datashare.smartshare.filesinfo.FileInfo
import com.wifishare.filesharing.datashare.smartshare.filesinfo.FileUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.text.DecimalFormat
import kotlin.math.log10
import kotlin.math.pow

class BackgroundViewModel :  ViewModel() {

    val imgSize = MutableLiveData<Int>()
    val videoSize = MutableLiveData<Int>()
    val audioSize = MutableLiveData<Int>()
    val docSize = MutableLiveData<Int>()
    val apkSize = MutableLiveData<Int>()
    companion object{
        var imgFile : List<FileInfo>?=ArrayList()
        var videoFile : List<FileInfo>?=ArrayList()
        var audioFile : List<FileInfo>?=ArrayList()
        var docFile : List<FileInfo>?=ArrayList()
        var apkFile : List<FileInfo>?=ArrayList()
    }

    val percentageRec = MutableLiveData<Float>()
    val totalUsed = MutableLiveData<String>()
    val totalFree = MutableLiveData<String>()

    val files: MutableLiveData<List<FileInfo>> by lazy {
        MutableLiveData<List<FileInfo>>()
    }
    private val _index = MutableLiveData<Int>()
    val type: LiveData<Int> = _index.map {
        it
    }

    fun setIndex(index: Int) {
        _index.value = index
    }


    fun getIndex(): Int? {
        return _index.value
    }


    fun loadFiles(context: Activity) {
        if(fileUpload){
            fileUpload=false
            viewModelScope.launch {
                getSize()
                imagesSize(context)
                videoSize(context)
                audioSize(context)
                docSize(context)
                apkSize(context)
            }
        }

    }

    suspend fun imagesSize(context: Context) {
        var sFileInfoList: List<FileInfo>?

        try {
            withContext(Dispatchers.IO) {
                sFileInfoList = FileUtils.getSpecificTypeFiles(
                    context, FileInfo.EXTEND_JPG,
                    arrayOf(FileInfo.EXTEND_JPG, FileInfo.EXTEND_JPEG, FileInfo.EXTEND_PNG)
                )
            }

            imgSize.value = sFileInfoList!!.size
            imgFile =
                FileUtils.getDetailFileInfos(
                    context,
                    sFileInfoList,
                    FileInfo.TYPE_JPG
                )

        }catch (e:Exception){

        }

//        files.postValue(imgFile)

    }

    suspend fun videoSize(context: Context) {
        var sFileInfoList: List<FileInfo>?
        withContext(Dispatchers.IO) {
            sFileInfoList = FileUtils.getSpecificTypeFiles(
                context, FileInfo.EXTEND_MP4,
                arrayOf(FileInfo.EXTEND_MP4)
            )
        }
        videoSize.value = sFileInfoList!!.size
        videoFile =
            FileUtils.getDetailFileInfos(
                context,
                sFileInfoList,
                FileInfo.TYPE_MP4
            )

//        files.postValue(videoFile)

    }

    suspend fun audioSize(context: Context) {
        var sFileInfoList: List<FileInfo>?
        withContext(Dispatchers.IO) {
            sFileInfoList = FileUtils.getSpecificTypeFiles(
                context, FileInfo.EXTEND_MP3,
                arrayOf(FileInfo.EXTEND_MP3, FileInfo.EXTEND_FLAC, FileInfo.EXTEND_WAV, FileInfo.EXTEND_M4A)
            )
        }
        audioSize.value = sFileInfoList!!.size
        audioFile =
            FileUtils.getDetailFileInfos(
                context,
                sFileInfoList,
                FileInfo.TYPE_MP3
            )
//        files.postValue(audioFile)

    }

    suspend fun apkSize(context: Context) {
        var sFileInfoList: List<FileInfo>?
        withContext(Dispatchers.IO) {
            sFileInfoList = FileUtils.getAppsOnly(context)
        }
        apkSize.value = sFileInfoList!!.size
        apkFile =
            FileUtils.getDetailFileInfos(
                context,
                sFileInfoList,
                FileInfo.TYPE_APK
            )
//        files.postValue(apkFile)

    }

    suspend fun docSize(context: Context) {
        var sFileInfoList: List<FileInfo>
        withContext(Dispatchers.Default) {
            sFileInfoList = FileUtils.getSpecificTypeFiles(
                context, FileInfo.EXTEND_DOC,
                arrayOf(
                    FileInfo.EXTEND_DOC,
                    FileInfo.EXTEND_DOCX,
                    FileInfo.EXTEND_PDF,
                    FileInfo.EXTEND_XLS,
                    FileInfo.EXTEND_XLSX,
                    FileInfo.EXTEND_PPT,
                    FileInfo.EXTEND_PPTX,
                    FileInfo.EXTEND_TXT
                )
            )
        }
        docSize.value = sFileInfoList.size
        docFile =
            FileUtils.getDetailFileInfos(
                context,
                sFileInfoList,
                FileInfo.TYPE_DOC
            )

        files.postValue(docFile)

    }

    suspend fun getSize() {

        var percentage: Double
        var freeSpace: String
        var usedSpace: String

        withContext(Dispatchers.Default) {
            // Fetching internal memory information
            val iPath: File = Environment.getDataDirectory()
            val stat = StatFs(Environment.getExternalStorageDirectory().path)
            val iStat = StatFs(iPath.path)
            val iBlockSize = iStat.blockSizeLong
            val iAvailableBlocks = iStat.availableBlocksLong
            val iTotalBlocks = iStat.blockCountLong

            val totalAvl = iAvailableBlocks * iBlockSize
            val totalSpc = iTotalBlocks * iBlockSize
            val used = totalSpc - totalAvl
            percentage = (used.toDouble() / totalSpc) * 100
            val iAvailableSpace = formatSize(totalAvl)
            val iTotalSpace = formatSize(totalSpc)
            usedSpace = formatSize(used)
            freeSpace = iAvailableSpace

        }
        percentageRec.value = percentage.toFloat()
        totalUsed.value = usedSpace
        totalFree.value = freeSpace
    }

    // Function to convert byter to KB and MB
    private fun formatSize(size: Long): String {
        val units = arrayOf("B", "kB", "MB", "GB", "TB")
        val digitGroups = (log10(size.toDouble()) / log10(1024.0)).toInt()
        return DecimalFormat("#,##0.#").format(size / 1024.0.pow(digitGroups.toDouble()))
            .toString() + " " + units[digitGroups]
    }

}