package com.example.playlistmaker.utils

import java.util.Locale

object DurationFormatter {
    // Конвертирует секунды в строку формата "ЧЧ:ММ:СС" или "ММ:СС"
    fun Int.toFormattedDuration(): String {
        val hours = this / 3600
        val minutes = (this % 3600) / 60
        val seconds = this % 60
        return if (hours > 0) {
            String.format(Locale("ru", "RU"), "%d:%02d:%02d", hours, minutes, seconds)
        } else {
            String.format(Locale("ru", "RU"), "%02d:%02d", minutes, seconds)
        }
    }

    fun String.toDurationInSeconds(): Int {
        val parts = this.split(":").map { it.toIntOrNull() ?: 0 }
        return when (parts.size) {
            2 -> parts[0] * 60 + parts[1] // ММ:СС
            3 -> parts[0] * 3600 + parts[1] * 60 + parts[2] // ЧЧ:ММ:СС
            else -> 0
        }
    }
}