package com.example.example.note

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate

data class Note(
    val date: LocalDate,
    val text: String
) {
    // Для GSON нужно хранить дату как строку
    fun toMap(): Map<String, Any> = mapOf(
        "date" to date.toString(),
        "text" to text
    )

    companion object {
        @RequiresApi(Build.VERSION_CODES.O)
        fun fromMap(map: Map<String, Any>): Note {
            val dateStr = map["date"] as String
            val text = map["text"] as String
            return Note(LocalDate.parse(dateStr), text)
        }
    }
}