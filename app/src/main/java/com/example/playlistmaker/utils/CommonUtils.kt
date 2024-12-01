package com.example.playlistmaker.utils

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Parcelable
import android.util.TypedValue
import java.text.SimpleDateFormat
import java.util.Locale

object CommonUtils {
    inline fun <reified T : Parcelable> Intent.parcelable(key: String): T? = when {
        Build.VERSION.SDK_INT >= 33 -> getParcelableExtra(key, T::class.java)
        else -> @Suppress("DEPRECATION") getParcelableExtra(key) as? T
    }

    fun formatMillisToMmSs(timeInMillis: Long) =
        SimpleDateFormat("mm:ss", Locale.getDefault()).format(timeInMillis)

    fun dpToPx(dp: Float, context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics
        ).toInt()
    }

    fun getTrackWordForm(count: Int): String {
        val lastDigit = count % 10
        val lastTwoDigits = count % 100

        return when {
            lastTwoDigits in 11..19 -> "треков"
            lastDigit == 1 -> "трек"
            lastDigit in 2..4 -> "трека"
            else -> "треков"
        }
    }
}