package com.wifishare.filesharing.datashare.smartshare.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.os.SystemClock
import android.view.View
import android.widget.Toast
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieDrawable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private var toast: Toast? = null
var lastClickTime: Long = 0

fun View.clickWithThrottle(throttleTime: Long = 600L, action: () -> Unit) {
    this.setOnClickListener {
        val currentTime = SystemClock.elapsedRealtime()
        if (currentTime - lastClickTime >= throttleTime) {
            lastClickTime = currentTime
            action()
        }
    }
}


fun toast(context: Activity, message: String) {
    try {
        if (context.isDestroyed || context.isFinishing) return
        if (toast != null) {
            toast?.cancel()
        }
        toast = Toast.makeText(context, message, Toast.LENGTH_SHORT)
        if (context.isDestroyed || context.isFinishing) return
        toast?.show()
    } catch (e: Exception) {
        e.printStackTrace()
    }
}



fun <R> CoroutineScope.executeAsyncTask(
    onPreExecute: () -> Unit,
    doInBackground: () -> R,
    onPostExecute: (R) -> Unit
) = launch {
    onPreExecute() // runs in Main Thread
    val result = withContext(Dispatchers.IO) {
        doInBackground() // runs in background thread without blocking the Main Thread
    }
    onPostExecute(result) // runs in Main Thread
}

 fun sendMail(context: Context) {
    var info = "Device Info:\n"
    info += "\nBRAND: ${Build.BRAND} "
    info += "\nOS Version: ${System.getProperty("os.version")}(${Build.VERSION.INCREMENTAL})"
    info += "\nOS API Level: ${Build.VERSION.SDK_INT}"
    info += "\nDevice: ${Build.DEVICE}"
    info += "\nModel: ${Build.MODEL} "

    val intent = Intent(Intent.ACTION_SENDTO)
    intent.data = Uri.parse("mailto:")
    intent.putExtra(Intent.EXTRA_EMAIL, arrayOf("technolineappsfeedback@gmail.com"))
    intent.putExtra(Intent.EXTRA_SUBJECT, "Review by User")
    intent.putExtra(Intent.EXTRA_TEXT, info)
    context.startActivity(Intent.createChooser(intent, "Email via..."))
}

fun sendMailwithDetails(context: Context , info : String) {

    val intent = Intent(Intent.ACTION_SENDTO)
    intent.data = Uri.parse("mailto:")
    intent.putExtra(Intent.EXTRA_EMAIL, arrayOf("technolineappsfeedback@gmail.com"))
    intent.putExtra(Intent.EXTRA_SUBJECT, "Feedback by User")
    intent.putExtra(Intent.EXTRA_TEXT, info)
    context.startActivity(Intent.createChooser(intent, "Email via..."))
}


fun isNetworkAvailable(context: Context?): Boolean {
    if (context == null) return false
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        val capabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        if (capabilities != null) {
            when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                    return true
                }
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                    return true
                }
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> {
                    return true
                }
            }
        }
    } else {
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        if (activeNetworkInfo != null && activeNetworkInfo.isConnected) {
            return true
        }
    }
    return false
}

fun LottieAnimationView.loadAnimationFromRaw(context: Context, rawResId: Int) {
    // Load Lottie JSON from a raw resource
    setAnimation(rawResId)

    // Optional: Configure additional properties
    repeatCount = LottieDrawable.INFINITE
    playAnimation()
}