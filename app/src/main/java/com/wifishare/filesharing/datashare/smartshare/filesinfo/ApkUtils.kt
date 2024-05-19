package com.wifishare.filesharing.datashare.smartshare.filesinfo

import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.PixelFormat
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

object ApkUtils {

    fun getApkThumbnail(context: Context?, apk_path: String?): Drawable? {
        if (context == null) {
            return null
        }
        val pm = context.packageManager
        val packageInfo = pm.getPackageArchiveInfo(apk_path!!, PackageManager.GET_ACTIVITIES)
        val appInfo = packageInfo!!.applicationInfo
        /**获取apk的图标  */
        appInfo!!.sourceDir = apk_path
        appInfo.publicSourceDir = apk_path
        return if (appInfo != null) {
            appInfo.loadIcon(pm)
        } else null
    }

    fun getDrawableSize(drawable: Drawable): Int {

        // 取 drawable 的长宽
        val w = drawable.intrinsicWidth
        val h = drawable.intrinsicHeight

        // 取 drawable 的颜色格式
        val config =
            if (drawable.opacity != PixelFormat.OPAQUE) Bitmap.Config.ARGB_8888 else Bitmap.Config.RGB_565
        // 建立对应 bitmap
        val bitmap = Bitmap.createBitmap(w, h, config)
        // 建立对应 bitmap 的画布
        val canvas = Canvas(bitmap)
        //        drawable.setBounds(0, 0, w, h);
        // 把 drawable 内容画到画布中
        drawable.draw(canvas)

//        Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.fav_jpg);
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val len = baos.toByteArray().size
        println("#############>>>>>>>>>$len")
        return len
    }

    fun getBitmapSize(bitmap: Bitmap): Int {
        /*
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        int len = baos.toByteArray().length;
        System.out.println("#############>>>>>>>>>" + len);
        return len;
        */
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
            bitmap.byteCount
        } else bitmap.rowBytes * bitmap.height
        // Pre HC-MR1
    }

    fun drawableToBitmap(drawable: Drawable?): Bitmap? {
        if (drawable == null) {
            return null
        }

        // 取 drawable 的长宽
        val w = drawable.intrinsicWidth
        val h = drawable.intrinsicHeight
        // 取 drawable 的颜色格式
        val config =
            if (drawable.opacity != PixelFormat.OPAQUE) Bitmap.Config.ARGB_8888 else Bitmap.Config.RGB_565
        //建立对应的Bitmap
        val bitmap = Bitmap.createBitmap(w, h, config)
        // 建立对应 bitmap 的画布
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, w, h)
        // 把 drawable 内容画到画布中
        drawable.draw(canvas)
        return bitmap
    }


    fun bitmapToByteArray(bitmap: Bitmap?): ByteArray? {
        if (bitmap == null) {
            return null
        }
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
        return baos.toByteArray()
    }


    fun bitmapToSDCard(bitmap: Bitmap?, resPath: String?): Boolean {
        if (bitmap == null) {
            return false
        }
        val resFile = File(resPath)
        return try {
            val fos = FileOutputStream(resFile)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
            fos.close()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun compressBitmap(srcBitmap: Bitmap, maxKByteCount: Int): Bitmap? {
        var baos: ByteArrayOutputStream? = null
        try {
            baos = ByteArrayOutputStream()
            srcBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
            var option = 98
            while (baos.toByteArray().size / 1024 >= maxKByteCount && option > 0) {
                baos.reset()
                srcBitmap.compress(Bitmap.CompressFormat.JPEG, option, baos)
                option -= 2
            }
        } catch (e: Exception) {
        }
        //        bitmap = BitmapFactory.decodeByteArray(bitmapByte, 0, bitmapByte.length);
        val bais =
            ByteArrayInputStream(baos!!.toByteArray()) //把压缩后的数据baos存放到ByteArrayInputStream中
        return BitmapFactory.decodeStream(bais, null, null)
    }

    fun compressBitmap(srcBitmap: Bitmap?, maxKByteCount: Int, targetPath: String?): Boolean {
        var srcBitmap = srcBitmap
        var result = false
        try {
            val baos = ByteArrayOutputStream()
            srcBitmap!!.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            var option = 98
            while (baos.toByteArray().size / 1024 >= maxKByteCount && option > 0) {
                baos.reset()
                srcBitmap.compress(Bitmap.CompressFormat.JPEG, option, baos)
                option -= 2
            }
            val bitmapByte = baos.toByteArray()
            val targetFile = File(targetPath)
            if (!targetFile.exists()) {
                targetFile.createNewFile()
            }
            val fos = FileOutputStream(targetFile)
            fos.write(bitmapByte)
            result = true
            try {
                fos.close()
                baos.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            if (!srcBitmap.isRecycled) {
                srcBitmap.recycle()
                srcBitmap = null
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return result
    }

    fun isAppInstalled(context: Context, packagename: String?): Boolean {
        var packageInfo: PackageInfo?
        try {
            packageInfo = context.packageManager.getPackageInfo(packagename!!, 0)
        } catch (e: PackageManager.NameNotFoundException) {
            packageInfo = null
            e.printStackTrace()
        }
        return packageInfo != null
    }

    fun getPackageName(context: Context?, filePath: String?): String {
        var packageName = ""
        if (context == null || TextUtils.isNullOrBlank(filePath)) {
            return packageName
        }
        val pm = context.packageManager
        val info = pm.getPackageArchiveInfo(filePath!!, PackageManager.GET_ACTIVITIES)
        var appInfo: ApplicationInfo? = null
        if (info != null) {
            appInfo = info.applicationInfo
            packageName = appInfo.packageName
        }
        return packageName
    }

    fun isInstalled(context: Context, filePath: String?): Boolean {
        val packageName = getPackageName(context, filePath)
        return isAppInstalled(context, packageName)
    }

    fun install(context: Context?, apkFilePath: String?) {
        if (context == null) {
            throw RuntimeException("ApkUtils install apk method and parameter context  == null?")
        }
        val file = File(apkFilePath)
        if (!file.exists()) {
            return
        }
        val intent = Intent()
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT)
        intent.action = Intent.ACTION_VIEW
        intent.setDataAndType(
            Uri.fromFile(file),
            "application/vnd.android.package-archive"
        )
        context.startActivity(intent)
    }
}