package com.wifishare.filesharing.datashare.smartshare.filesinfo

import android.graphics.drawable.Drawable
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.Serializable

class FileInfo : Serializable {
    var filePath: String? = null
    var filePkg: String? = null
    var fileType = 0
    var size: Long = 0
    var name: String? = null
    var sizeDesc: String? = null
    var appIcon: Drawable? = null
    var extra: String? = null
    var procceed: Long = 0
    var result = 0
    var isSelected = false

    constructor() {}
    constructor(filePath: String?, size: Long) {
        this.filePath = filePath
        this.size = size
    }

    override fun toString(): String {
        return "FileInfo{" +
                "filePath='" + filePath + '\'' +
                ", fileType=" + fileType +
                ", size=" + size +
                '}'
    }

    companion object {

        const val EXTEND_APK = ".apk"

        const val EXTEND_JPEG = ".jpeg"
        const val EXTEND_JPG = ".jpg"
        const val EXTEND_PNG = ".png"


        const val EXTEND_MP3 = ".mp3"
        const val EXTEND_M4A = ".m4a"
        const val EXTEND_FLAC = ".flac"
        const val EXTEND_WAV = ".wav"


        const val EXTEND_MP4 = ".mp4"

        const val EXTEND_DOC = ".doc"
        const val EXTEND_PDF = ".pdf"
        const val EXTEND_DOCX = ".docx"
        const val EXTEND_XLS = ".xls"
        const val EXTEND_XLSX = ".xlsx"
        const val EXTEND_PPT = ".ppt"
        const val EXTEND_PPTX = ".pptx"
        const val EXTEND_TXT = ".txt"


        const val TYPE_JPG = 1
        const val TYPE_MP4 = 2
        const val TYPE_MP3 = 3
        const val TYPE_DOC = 4
        const val TYPE_APK = 5


        // 1 成功  -1 失败
        const val FLAG_SUCCESS = 1
        const val FLAG_DEFAULT = 0
        const val FLAG_FAILURE = -1
        fun toJsonStr(fileInfo: FileInfo): String {
            val jsonStr = ""
            val jsonObject = JSONObject()
            try {
                jsonObject.put("filePath", fileInfo.filePath)
                jsonObject.put("fileType", fileInfo.fileType)
                jsonObject.put("size", fileInfo.size)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            return jsonObject.toString()
        }

        fun toObject(jsonStr: String?): FileInfo {
            val fileInfo = FileInfo()
            try {
                val jsonObject = JSONObject(jsonStr)
                val filePath = jsonObject["filePath"] as String
                val size = jsonObject.getLong("size")
                val type = jsonObject.getInt("fileType")
                fileInfo.filePath = filePath
                fileInfo.size = size
                fileInfo.fileType = type
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            return fileInfo
        }

        fun toJsonArrayStr(fileInfoList: List<FileInfo?>?): String {
            val jsonArray = JSONArray()
            if (fileInfoList != null) {
                for (fileInfo in fileInfoList) {
                    if (fileInfo != null) {
                        try {
                            jsonArray.put(JSONObject(toJsonStr(fileInfo)))
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    }
                }
            }
            return jsonArray.toString()
        }

        @JvmStatic
        fun main(args: Array<String>) {
            println("Hello world")
            val fileInfos: MutableList<FileInfo?> = ArrayList()
            var fileInfo: FileInfo? = FileInfo()
            for (i in 0..2) {
                fileInfo = FileInfo()
                fileInfo.filePath = "/sdcard/test$i.apk"
                fileInfo.fileType = TYPE_APK
                fileInfo.size = (1000 + i).toLong()
                fileInfos.add(fileInfo)
                fileInfo = null
            }
            println(
                """
    List<FileInfo> to JsonStr: 
    ${toJsonArrayStr(fileInfos)}
    """.trimIndent()
            )
        }
    }
}