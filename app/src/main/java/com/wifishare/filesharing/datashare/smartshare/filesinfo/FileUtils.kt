package com.wifishare.filesharing.datashare.smartshare.filesinfo

import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.PixelFormat
import android.graphics.drawable.Drawable
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.text.DecimalFormat


object FileUtils {


    const val DEFAULT_ROOT_PATH = "/mnt/download/Wifi Date Share/"

    val FORMAT = DecimalFormat("####.##")
    val FORMAT_ONE = DecimalFormat("####.#")

    private fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents" == uri.authority
    }

    private fun isDownloadsDocument(uri: Uri): Boolean {
        return "com.android.providers.downloads.documents" == uri.authority
    }

    private fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents" == uri.authority
    }

    private fun isGooglePhotosUri(uri: Uri): Boolean {
        return "com.google.android.apps.photos.content" == uri.authority
    }

    fun isWhatsAppFile(uri: Uri): Boolean {
        return "com.whatsapp.provider.media" == uri.authority
    }

    suspend fun getSpecificTypeFiles(
        context: Context,
        type: String,
        extension: Array<String>
    ): List<FileInfo> {

        CoroutineScope(Dispatchers.IO).apply {

        val fileInfoList: MutableList<FileInfo> = ArrayList()
        val fileUri = when (type) {
            FileInfo.EXTEND_JPG -> {
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            }
            FileInfo.EXTEND_MP4 -> {
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI
            }
            FileInfo.EXTEND_MP3 -> {
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
            }
            else -> {
                MediaStore.Files.getContentUri("external")
            }
        }


            // val fileUri = MediaStore.Files.getContentUri("external")
            val projection = arrayOf(
                MediaStore.Files.FileColumns.DATA, MediaStore.Files.FileColumns.TITLE
            )

            var selection = ""
            for (i in extension.indices) {
                if (i != 0) {
                    selection = "$selection OR "
                }
                selection =
                    selection + MediaStore.Files.FileColumns.DATA + " LIKE '%" + extension[i] + "'"
            }
            val sortOrder = MediaStore.Files.FileColumns.DATE_MODIFIED
            val cursor =
                context.contentResolver.query(
                    fileUri,
                    projection,
                    selection,
                    null,
                    sortOrder
                )
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    try {
                        val data = cursor.getString(0)
                        val fileInfo = FileInfo()
                        fileInfo.filePath = data
                        //       Log.e(TAG, "getSpecificTypeFiles:  $fileUri")
                        var size: Long = 0
                        try {
                            val file = File(data)
                            size = file.length()

                            fileInfo.size = size

                        } catch (e: Exception) {
                        }
                        if(size!=0L){
                            fileInfoList.add(fileInfo)
                        }
                    } catch (e: Exception) {
                        Log.i("FileUtils", "------>>>" + e.message)
                    }
                }
            }
//            delay(500)
            return fileInfoList
        }

    }

    suspend fun getAppsOnly(context: Context): List<FileInfo> {
        val fileInfoList: MutableList<FileInfo> = ArrayList()
        CoroutineScope(Dispatchers.IO).apply {
            val manager = context.packageManager
            val mainIntent = Intent(Intent.ACTION_MAIN, null)
            mainIntent.addCategory(Intent.CATEGORY_LAUNCHER)
            val availableActivities = manager.queryIntentActivities(mainIntent, 0)
            for (resolveInfo in availableActivities) {
                val fileInfo = FileInfo()
                fileInfo.name = resolveInfo.loadLabel(manager).toString()
                fileInfo.filePkg = resolveInfo.activityInfo.packageName
                fileInfo.appIcon = resolveInfo.activityInfo.loadIcon(manager)
                val applicationInfo: ApplicationInfo =
                    manager.getApplicationInfo(resolveInfo.activityInfo.packageName, 0)
                val file = File(applicationInfo.publicSourceDir)
                fileInfo.filePath = File(applicationInfo.sourceDir).toString()
                val size: Long = file.length()
                fileInfo.size = size
                fileInfoList.add(fileInfo)
            }
            return fileInfoList
        }

    }

/*    suspend fun getFileInfo(context: Context, fileName: String): FileInfo? {
        CoroutineScope(Dispatchers.IO).apply {
            val fileInfoList = getSpecificTypeFiles(context, arrayOf(fileName).toString())
            delay(500)
            return if (fileInfoList == null && fileInfoList.isEmpty()) {
                null
            } else fileInfoList[0]
        }
    }*/


    suspend fun getDetailFileInfos(
        context: Context,
        fileInfoList: List<FileInfo>?,
        type: Int
    ): List<FileInfo>? {
        CoroutineScope(Dispatchers.IO).apply {
            if (fileInfoList == null || fileInfoList.isEmpty()) {
                return fileInfoList
            }
            for (fileInfo in fileInfoList) {
                fileInfo.name = getFileName(fileInfo.filePath)
                fileInfo.sizeDesc = getFileSize(fileInfo.size)
                when (type) {
                    FileInfo.TYPE_APK -> {
                        try {
                            fileInfo.name = getAppNameFromPkgName(context, fileInfo.filePkg)
                          /*  val d: Drawable = context.packageManager.getApplicationIcon(fileInfo.filePkg.toString())
                            fileInfo.appIcon = d*/
                        } catch (e: Resources.NotFoundException) {
                            e.printStackTrace()
                        }
                    }
                    FileInfo.TYPE_MP4 -> {
                        //  fileInfo.bitmap = getScreenshotBitmap(context, fileInfo.filePath, FileInfo.TYPE_MP4)
                    }
                    FileInfo.TYPE_MP3 -> {
                    }
                    FileInfo.TYPE_JPG -> {
                    }
                }
                fileInfo.fileType = type
            }
            delay(500)
            return fileInfoList
        }
    }

    private fun getAppNameFromPkgName(context: Context, pkg: String?): String? {
        val packageManager = context.packageManager
        var applicationInfo: ApplicationInfo? = null
        try {
            applicationInfo = packageManager.getApplicationInfo(pkg.toString(), 0)
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return (if (applicationInfo != null) packageManager.getApplicationLabel(applicationInfo) else "???") as String
    }

    private fun getFileName(filePath: String?): String {
        return if (filePath == null || filePath == "") "" else filePath.substring(
            filePath.lastIndexOf(
                "/"
            ) + 1
        )
    }

    private val rootDirPath: String
        get() {
            var path = DEFAULT_ROOT_PATH
            if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
                path = Environment.getExternalStorageDirectory().toString() + "/WifiShare/"
            }
            return path
        }


    fun getFileSize(size: Long): String {
        if (size < 0) { //小于0字节则返回0
            return "0B"
        }
        var value = 0.0
        return if (size / 1024 < 1) { //0 ` 1024 byte
            size.toString() + "B"
        } else if (size / (1024 * 1024) < 1) { //0 ` 1024 kbyte
            value = (size / 1024f).toDouble()
            FORMAT.format(value) + "KB"
        } else if (size / (1024 * 1024 * 1024) < 1) {                  //0 ` 1024 mbyte
            value = (size * 100 / (1024 * 1024) / 100f).toDouble()
            FORMAT.format(value) + "MB"
        } else {                  //0 ` 1024 mbyte
            value = (size * 100L / (1024L * 1024L * 1024L) / 100f).toDouble()
            FORMAT.format(value) + "GB"
        }
    }


    fun getFileSizeArrayStr(size: Long): Array<String?> {
        val result = arrayOfNulls<String>(2)
        if (size < 0) { //小于0字节则返回0
            result[0] = "0"
            result[1] = "B"
            return result
        }
        var value = 0.0
        if (size / 1024 < 1) { //0 ` 1024 byte
            result[0] = FORMAT_ONE.format(size)
            result[1] = "B"
            //            return  size + "B";
        } else if (size / (1024 * 1024) < 1) { //0 ` 1024 kbyte
            value = (size / 1024f).toDouble()
            result[0] = FORMAT_ONE.format(value)
            result[1] = "KB"
            //            return  FORMAT.format(value) + "KB";
        } else if (size / (1024 * 1024 * 1024) < 1) {                  //0 ` 1024 mbyte
            value = (size * 100 / (1024 * 1024) / 100f).toDouble()
            result[0] = FORMAT_ONE.format(value)
            result[1] = "MB"
            //            return  FORMAT.format(value) + "MB";
        } else {                  //0 ` 1024 mbyte
            value = (size * 100L / (1024L * 1024L * 1024L) / 100f).toDouble()
            result[0] = FORMAT_ONE.format(value)
            result[1] = "GB"
            //            return  FORMAT.format(value) + "GB";
        }
        return result
    }

    private fun getTimeByArrayStr(second: Long): Array<String?> {
        val result = arrayOfNulls<String>(2)
        if (second < 0) {//return 0 if less than 0 bytes
            result[0] = "0"
            result[1] = "秒"
            return result
        }
        var value = 0.0
        if (second / (60f * 1000f) < 1) { //秒
            result[0] = (second / 1000).toString()
            result[1] = "秒"
            //            return  size + "B";
        } else if (second / (60f * 60f * 1000f) < 1) { //分
            value = (second / (60f * 1000f)).toDouble()
            result[0] = FORMAT_ONE.format(value)
            result[1] = "分"
            //            return  FORMAT.format(value) + "KB";
        } else {                              //时
            value = (second / (60f * 60f * 1000f)).toDouble()
            result[0] = FORMAT_ONE.format(value)
            result[1] = "时"
        }
        return result
    }

    /**
     * Determine whether the file is an Apk installation file
     * @param filePath
     * @return
     */
    fun isApkFile(filePath: String?): Boolean {
        if (filePath == null || filePath == "") {
            return false
        }
        return filePath.lastIndexOf(FileInfo.EXTEND_APK) > 0
    }

    /**
     * Determine if a file is an image
     * @param filePath
     * @return
     */
    fun isJpgFile(filePath: String?): Boolean {
        if (filePath == null || filePath == "") {
            return false
        }
        return filePath.lastIndexOf(FileInfo.EXTEND_JPG) > 0 || filePath.lastIndexOf(
            FileInfo.EXTEND_JPEG
        ) > 0
    }

    /**
     * Determine if the file is PNG
     * @param filePath
     * @return
     */
    fun isPngFile(filePath: String?): Boolean {
        if (filePath == null || filePath == "") {
            return false
        }
        return filePath.lastIndexOf(FileInfo.EXTEND_PNG) > 0
    }

    /**
     *Determine if the file is Mp3
     * @param filePath
     * @return
     */
    fun isMp3File(filePath: String?): Boolean {
        if (filePath == null || filePath == "") {
            return false
        }
        return filePath.lastIndexOf(FileInfo.EXTEND_MP3) > 0
    }

    /**
     * Determine if the file is Mp4
     * @param filePath
     * @return
     */
    fun isMp4File(filePath: String?): Boolean {
        if (filePath == null || filePath == "") {
            return false
        }
        return filePath.lastIndexOf(FileInfo.EXTEND_MP4) > 0
    }

    /**
     * Get the Log icon of the Apk file
     * @param context
     * @param apk_path
     * @return
     */
    fun getApkThumbnail(context: Context?, apk_path: String?): Drawable? {
        if (context == null) {
            return null
        }
        try {
            val pm = context.packageManager
            val packageInfo = pm.getPackageArchiveInfo(apk_path!!, PackageManager.GET_ACTIVITIES)
            val appInfo = packageInfo!!.applicationInfo
            /**Get the icon of the apk */
            appInfo!!.sourceDir = apk_path
            appInfo.publicSourceDir = apk_path

            val d: Drawable =
                context.packageManager.getApplicationIcon(apk_path)

            if (appInfo != null) {
                return d
            }
        } catch (e: Exception) {
        }
        return null
    }

    /**
     * @Description Get album art
     * @param filePath file path，like XXX/XXX/XX.mp3
     * @return album cover bitmap
     */
    fun createAlbumArt(filePath: String?): Bitmap? {
        var bitmap: Bitmap? = null
        //能够获取多媒体文件元数据的类
        val retriever = MediaMetadataRetriever()
        try {
            retriever.setDataSource(filePath) //设置数据源
            val embedPic = retriever.embeddedPicture //得到字节型数据
            bitmap = BitmapFactory.decodeByteArray(embedPic, 0, embedPic!!.size) //转换为图片
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                retriever.release()
            } catch (e2: Exception) {
                e2.printStackTrace()
            }
        }
        return bitmap
    }

    /**
     * Drawable to Bitmap
     *
     * @param drawable
     * @return
     */
    fun drawableToBitmap(drawable: Drawable?): Bitmap? {
        if (drawable == null) {
            return null
        }

        // Get the length and width of the drawable
        val w = drawable.intrinsicWidth
        val h = drawable.intrinsicHeight
        // Get the color format of the drawable
        val config =
            if (drawable.opacity != PixelFormat.OPAQUE) Bitmap.Config.ARGB_8888 else Bitmap.Config.RGB_565
//Create the corresponding Bitmap
        val bitmap = Bitmap.createBitmap(w, h, config)
// Create a canvas corresponding to the bitmap
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, w, h)
// draw the drawable content to the canvas
        drawable.draw(canvas)
        return bitmap
    }

    /**
     * Bitmap转ByteArray
     *
     * @param bitmap
     * @return
     */
    fun bitmapToByteArray(bitmap: Bitmap?): ByteArray? {
        if (bitmap == null) {
            return null
        }
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
        return baos.toByteArray()
    }

    /**
     * Bitmap 写入到SD卡
     *
     * @param bitmap
     * @param resPath
     * @return
     */
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

    /**
     * Get the number of files received
     * @return
     */
    val receiveFileCount: Int
        get() {
            var count = 0
            val rootDir = File(rootDirPath)
            if (rootDir != null) {
                count = getFileCount(rootDir)
            }
            return count
        }

    /**
     * Get the number of files under the specified folder
     * @param rootDir
     * @return
     */
    fun getFileCount(rootDir: File?): Int {
        var count = 0
        if (rootDir != null && rootDir.exists()) {
            for (file in rootDir.listFiles()) {
                if (file.isDirectory) {
                    count += getFileCount(file)
                } else {
                    count++
                }
            }
        }
        return count
    }

    /**
     *Get the total file size received
     * @return
     */
    val receiveFileListTotalLength: String
        get() {
            var total: Long = 0
            val rootDir = File(rootDirPath)
            if (rootDir != null) {
                total = getFileLength(rootDir)
            }
            return getFileSize(total)
        }

    /**
     * Recursively get the size of the specified folder
     * @param rootDir
     * @return
     */
    fun getFileLength(rootDir: File?): Long {
        var len: Long = 0
        if (rootDir != null && rootDir.exists()) {
            for (f in rootDir.listFiles()) {
                len = if (f.isDirectory) {
                    len + getFileLength(f)
                } else {
                    len + f.length()
                }
            }
        }
        return len
    }


    @JvmStatic
    fun main(args: Array<String>) {
        println(
            "Test getTimeByArrayStr(59 * 1000)----->>>" + getTimeByArrayStr((59 * 1000).toLong())[0]
                    + " , " + getTimeByArrayStr((59 * 1000).toLong())[1]
        )
        println(
            "Test getTimeByArrayStr(59 * 1000)----->>>" + getTimeByArrayStr((59 * 1001).toLong())[0]
                    + " , " + getTimeByArrayStr((59 * 1001).toLong())[1]
        )
        println(
            "Test getTimeByArrayStr(59 * 1000)----->>>" + getTimeByArrayStr((59 * 1000 * 100).toLong())[0]
                    + " , " + getTimeByArrayStr((59 * 1000 * 100).toLong())[1]
        )
    }
}