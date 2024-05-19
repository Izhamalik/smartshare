package com.wifishare.filesharing.datashare.smartshare.model

import android.app.Activity
import android.util.Log
import androidx.lifecycle.*
import com.wifishare.filesharing.datashare.smartshare.R
import com.wifishare.filesharing.datashare.smartshare.common.Constants
import com.wifishare.filesharing.datashare.smartshare.filesinfo.FileInfo
import com.wifishare.filesharing.datashare.smartshare.filesinfo.FileUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.*

class HistoryPageViewModel : ViewModel() {


    val files: MutableLiveData<List<FileInfo>> by lazy {
        MutableLiveData<List<FileInfo>>()
    }

    val TAG = "historyviewmodel"

    val _index = MutableLiveData<Int>()
    val type: LiveData<Int> = _index.map {
        it
    }

    fun getIndex(): Int? {
        return _index.value
    }

    fun loadFiles(context: Activity) {
        viewModelScope.launch {
            loadData(context)
        }
    }


    fun setIndex(index: Int) {
        _index.value = index
    }


    private suspend fun loadData(context: Activity) {
        withContext(Dispatchers.IO) {
            var sFileInfoList: List<FileInfo>? = ArrayList()
            val arr = ArrayList<FileInfo>()
            val path = File(context.getString(R.string.download_path))
            val filesList = path.listFiles()
            if (filesList != null) {
                when (_index.value) {
                    FileInfo.TYPE_APK -> {
                        filesList.forEach {
                            if (Constants.isFileAPK(it.name)) {
                                val fileInfo = FileInfo()
                                fileInfo.name = it.name
                                fileInfo.filePath = it.absolutePath
                                fileInfo.isSelected = false
                                try {
                                    val packageManager = context.packageManager
                                    val packageInfo =
                                        packageManager.getPackageArchiveInfo(it.path, 0)
                                    fileInfo.filePkg =
                                        packageManager.getPackageArchiveInfo(
                                            it.path.toString(),
                                            0
                                        )?.packageName
                                    packageInfo!!.applicationInfo.sourceDir = it.path
                                    packageInfo.applicationInfo.publicSourceDir = it.path

                                    val icon = packageInfo.applicationInfo.loadIcon(packageManager)
                                    if(icon!=null){
                                        fileInfo.appIcon = icon
                                    }else{
                                        fileInfo.appIcon=context.resources.getDrawable(R.drawable.ic_androidapps)
                                    }


                                    fileInfo.name = it.name
                                    val size = it.length()
                                    fileInfo.sizeDesc = FileUtils.getFileSize(size)

                                } catch (e: RuntimeException) {
                                    e.printStackTrace()
                                }
                                arr.add(fileInfo)
                            }
                            sFileInfoList = arr
                            /*  FileUtils.getDetailFileInfos(
                                  context,
                                  arr,
                                  FileInfo.TYPE_APK
                              )*/
                        }
                    }
                    FileInfo.TYPE_JPG -> {
                        filesList.forEach {
                            if (Constants.isFileImage(it.name)) {
                                val fileInfo = FileInfo()
                                fileInfo.name = it.name
                                fileInfo.filePath = it.absolutePath
                                fileInfo.isSelected = false
                                try {
                                    val size = it.length()
                                    fileInfo.size = size
                                } catch (e: RuntimeException) {
                                    e.printStackTrace()
                                }
                                arr.add(fileInfo)
                            }
                        }
                        sFileInfoList =
                            FileUtils.getDetailFileInfos(
                                context,
                                arr,
                                FileInfo.TYPE_JPG
                            )
                    }
                    FileInfo.TYPE_MP3 -> {
                        filesList.forEach {
                            if (Constants.isFileAudio(it.name)) {
                                val fileInfo = FileInfo()
                                fileInfo.name = it.name
                                fileInfo.filePath = it.absolutePath
                                fileInfo.isSelected = false
                                try {
                                    val size = it.length()
                                    fileInfo.size = size
                                } catch (e: RuntimeException) {
                                    e.printStackTrace()
                                }
                                arr.add(fileInfo)
                            }
                        }
                        sFileInfoList =
                            FileUtils.getDetailFileInfos(
                                context,
                                arr,
                                FileInfo.TYPE_MP3
                            )
                    }
                    FileInfo.TYPE_MP4 -> {
                        filesList.forEach {
                            if (Constants.isFileVideoOrGif(it.name)) {
                                val fileInfo = FileInfo()
                                fileInfo.name = it.name
                                fileInfo.filePath = it.absolutePath
                                fileInfo.isSelected = false
                                try {
                                    val size = it.length()
                                    fileInfo.size = size
                                } catch (e: RuntimeException) {
                                    e.printStackTrace()
                                }
                                arr.add(fileInfo)
                            }
                        }
                        sFileInfoList =
                            FileUtils.getDetailFileInfos(
                                context,
                                arr,
                                FileInfo.TYPE_MP4
                            )
                    }
                    FileInfo.TYPE_DOC -> {
                        filesList.forEach {
                            if (Constants.isFileDocument(it.name)) {
                                val fileInfo = FileInfo()
                                fileInfo.name = it.name
                                fileInfo.filePath = it.absolutePath
                                fileInfo.isSelected = false
                                try {
                                    val size = it.length()
                                    fileInfo.size = size
                                } catch (e: RuntimeException) {
                                    e.printStackTrace()
                                }
                                arr.add(fileInfo)
                            }
                        }

                        sFileInfoList =
                            FileUtils.getDetailFileInfos(
                                context,
                                arr,
                                FileInfo.TYPE_DOC
                            )
                    }
                    else -> {

                        filesList.forEach {
                                val fileInfo = FileInfo()
                                fileInfo.name = it.name
                                fileInfo.filePath = it.absolutePath
                                fileInfo.isSelected = false
                                try {
                                    val size = it.length()
                                    fileInfo.size = size
                                } catch (e: RuntimeException) {
                                    e.printStackTrace()
                                }
                                arr.add(fileInfo)
                            }
                        sFileInfoList =
                            FileUtils.getDetailFileInfos(
                                context,
                                arr,
                                FileInfo.TYPE_DOC
                            )
                    }
                }
            }
            files.postValue(sFileInfoList)
        }
    }


    fun deleteFiles(context: Activity, fileInfos: List<FileInfo>) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                fileInfos.forEach { fileInfo ->
                    val file = File(fileInfo.filePath)
                    val deleted = file.delete()
                    if (deleted) {
                        Log.d(TAG, "File deleted successfully: ${fileInfo.filePath}")
                    } else {
                        Log.e(TAG, "Failed to delete file: ${fileInfo.filePath}")
                    }
                }
                loadData(context)
            }
        }
    }


}