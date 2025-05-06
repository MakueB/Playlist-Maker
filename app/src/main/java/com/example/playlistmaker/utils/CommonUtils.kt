package com.example.playlistmaker.utils

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Parcelable
import android.util.TypedValue
import com.example.playlistmaker.search.domain.models.Track
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

    // Конвертирует строку формата "ММ:СС" или "ЧЧ:ММ:СС" в секунды
    fun toDurationInSeconds(time: String): Int {
        val parts = time.split(":").map { it.toIntOrNull() ?: 0 }
        return when (parts.size) {
            2 -> parts[0] * 60 + parts[1] // ММ:СС
            3 -> parts[0] * 3600 + parts[1] * 60 + parts[2] // ЧЧ:ММ:СС
            else -> 0
        }
    }

    // Конвертирует секунды в строку формата "ЧЧ:ММ:СС"
    fun toFormattedDuration(seconds: Int): String {
        val hours = seconds / 3600
        val minutes = (seconds % 3600) / 60
        val seconds = seconds % 60
        return if (hours > 0) {
            String.format(Locale("ru", "RU"),"%d:%02d:%02d", hours, minutes, seconds)
        } else {
            String.format(Locale("ru", "RU"),"%02d:%02d", minutes, seconds)
        }
    }

    fun getTotalDurationInMinutes(tracks: List<Track>): Int {
        return tracks.sumOf { track ->
            // Преобразуем строку формата "mm:ss" в минуты
            val parts = track.trackDuration.split(":")
            val minutes = parts.getOrNull(0)?.toIntOrNull() ?: 0
            val seconds = parts.getOrNull(1)?.toIntOrNull() ?: 0
            minutes + (seconds / 60)
        }
    }

    fun formatMinutesText(minutes: Int): String {
        val suffix = when {
            minutes % 100 in 11..14 -> "минут"
            minutes % 10 == 1 -> "минута"
            minutes % 10 in 2..4 -> "минуты"
            else -> "минут"
        }
        return "$minutes $suffix"
    }
}