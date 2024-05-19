package com.wifishare.filesharing.datashare.smartshare.common

import android.content.Context
import android.util.Log
import com.wifishare.filesharing.datashare.smartshare.util.SwitchPreference

object Constants {
     var PORT = 58761
    const val PORT1 = 27000
    const val PRIVACY_LINK="https://sites.google.com/view/recover-deleted-messages/smart-switch"
    const val MORE_APPS="https://play.google.com/store/apps/dev?id=6089798073505044276"
    const val RATE_US="https://play.google.com/store/apps/details?id=com.app.smartswitchmobile.transfermydata"

    fun isFreeAvailable(context: Context): Boolean {
        val switchPreference = SwitchPreference(context)
        return if (switchPreference.isPurchasedApp) {
            true
        } else {
            if (System.currentTimeMillis() - switchPreference.isDemo < 15 * 60 * 1000) {
                Log.e("isFree", "Within the 15 Min")
                true
            } else {
                Log.e("isFree", "Above the 15 Min")
                false
            }
        }
    }


     fun isFileVideoOrGif(filename: String): Boolean {
        val splits = filename.split("\\.".toRegex()).toTypedArray()
        when (splits[splits.size - 1]) {
            "mp4", "gif" -> {
                return true
            }
        }
        return false
    }


     fun isFileImage(filename: String): Boolean {
        val splits = filename.split("\\.".toRegex()).toTypedArray()
        when (splits[splits.size - 1]) {
            "jpg", "png", "webp" -> {
                return true
            }
        }
        return false
    }


     fun isFileSticker(filename: String): Boolean {
        val splits = filename.split("\\.".toRegex()).toTypedArray()
        when (splits[splits.size - 1]) {
            "webp" -> {
                return true
            }
        }
        return false
    }


     fun isFileAudio(filename: String): Boolean {
        val splits = filename.split("\\.".toRegex()).toTypedArray()
        when (splits[splits.size - 1]) {
            "mp3", "m4a", "aac" -> {
                return true
            }
        }
        return false
    }


     fun isFileVoice(filename: String): Boolean {
        val splits = filename.split("\\.".toRegex()).toTypedArray()
        when (splits[splits.size - 1]) {
            "opus", "ogg" -> {
                return true
            }
        }
        return false
    }

    fun isFileAPK(filename: String): Boolean {
        val splits = filename.split("\\.".toRegex()).toTypedArray()
        when (splits[splits.size - 1]) {
            "apk" -> {
                return true
            }
        }
        return false
    }

     fun isFileDocument(filename: String): Boolean {
        val splits = filename.split("\\.".toRegex()).toTypedArray()
        when (splits[splits.size - 1]) {
            "pdf", "xls", "ppt", "pptx", "doc", "docx", "xlsx", "txt" -> {
                return true
            }
        }
        return false
    }

}