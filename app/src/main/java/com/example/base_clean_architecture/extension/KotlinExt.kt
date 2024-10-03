package com.example.base_clean_architecture.extension

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.media.AudioManager
import android.net.Uri
import android.os.*
import android.provider.Settings
import android.provider.Settings.Secure
import android.widget.Toast
import androidx.core.app.NotificationManagerCompat
import com.example.base_clean_architecture.BuildConfig
import com.example.base_clean_architecture.utils.Constant
import timber.log.Timber
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

fun Long.convertTimeMillisToMinuteSecond(): String {
    val minutes = this / 60000
    val seconds = this / 1000 - minutes * 60
    return "${if (minutes < 10) "0$minutes" else "$minutes"}:${if (seconds < 10) "0$seconds" else "$seconds"}"
}

@SuppressLint("HardwareIds")
fun Context.getDeviceID(): String {
    return Secure.getString(
        contentResolver, Secure.ANDROID_ID
    )
}

fun Context.getStatusModeDevice(): Int {
    val am = getSystemService(Context.AUDIO_SERVICE) as AudioManager
    return am.ringerMode
}

fun Context.getStatusNotification(): Boolean {
    return NotificationManagerCompat.from(this).areNotificationsEnabled()
}

fun Context.openAppOnPlayStore() {
    val uri = Uri.parse("market://details?id=${BuildConfig.APPLICATION_ID}")
    openURI(uri, "Play Store not found in your device")
}

@SuppressLint("QueryPermissionsNeeded")
fun Context.openURI(
    uri: Uri?, errorMsg: String?
) {
    val i = Intent(Intent.ACTION_VIEW, uri)
    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
    if (packageManager.queryIntentActivities(i, 0).size > 0) {
        startActivity(i)
    } else if (errorMsg != null) {
        Toast.makeText(this, errorMsg, Toast.LENGTH_SHORT).show()
    }
}

fun Context.openExternalBrowser(url: String) {
    try {
        val browserIntent =
            Intent(Intent.ACTION_VIEW, Uri.parse(url)).addCategory(Intent.CATEGORY_BROWSABLE)
        startActivity(browserIntent)
    } catch (e: Exception) {
        Timber.e("openExternalBrowser: $e")
    }
}

@SuppressLint("SimpleDateFormat")
fun Context.createImageFile(): File? {
    try {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        val storageDir: File? = getExternalFilesDir(
            Environment.DIRECTORY_PICTURES
        )
        return File.createTempFile(
            imageFileName, /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        )
    } catch (e: IOException) {
        Timber.e("createImageFile: $e")
        return null
    }
}

fun Context.triggerRebirth() {
    val packageManager = packageManager
    val intent = packageManager.getLaunchIntentForPackage(packageName)
    val componentName = intent!!.component
    val mainIntent = Intent.makeRestartActivityTask(componentName)
    startActivity(mainIntent)
    Runtime.getRuntime().exit(0)
}

fun Context.isTablet(): Boolean {
    return (resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK >= Configuration.SCREENLAYOUT_SIZE_LARGE)
}

fun Long.checkTimeAvailable(): Boolean {
    val calendarExpired: Calendar = Calendar.getInstance()
    calendarExpired.timeInMillis = this
    val calendarNow: Calendar = Calendar.getInstance()
    calendarNow.timeInMillis = System.currentTimeMillis()
    return calendarExpired.time.before(calendarNow.time)
}

fun Activity.openAppSetting() {
    try {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri: Uri = Uri.fromParts("package", packageName, null)
        intent.data = uri
        startActivity(intent)
    } catch (ex: ActivityNotFoundException) {
        val intent = Intent(Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS)
        startActivity(intent)
    }
}

fun Context.startActivity(packageName: String, uri: Uri) {
    try {
        this.startActivity(Intent(Intent.ACTION_VIEW, uri))
    } catch (e: ActivityNotFoundException) {
        try {
            this.startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(String.format(Constant.PLAY_STORE_URI, packageName))
                )
            )
        } catch (e: ActivityNotFoundException) {
            this.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(String.format(Constant.BROWSER_URI, packageName))))
        }
    }
}
