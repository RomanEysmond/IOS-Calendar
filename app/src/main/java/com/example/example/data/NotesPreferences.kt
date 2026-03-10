package com.example.example.data

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.example.note.Note
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.time.LocalDate

data class NoteData(
    val date: String,
    val text: String
) {
    // Конвертация в Note
    @RequiresApi(Build.VERSION_CODES.O)
    fun toNote(): Note? = try {
        Note(LocalDate.parse(date), text)
    } catch (e: Exception) {
        null
    }

    companion object {
        // Конвертация из Note
        fun fromNote(note: Note): NoteData = NoteData(note.date.toString(), note.text)
    }
}

class NotesPreferences(private val context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("notes_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()
    private val notesKey = "notes_list"

    fun saveNotes(notes: List<Note>) {
        try {
            val notesData = notes.map { note -> NoteData.fromNote(note) }
            val notesJson = gson.toJson(notesData)
            prefs.edit().putString(notesKey, notesJson).apply()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun loadNotes(): MutableList<Note> {
        val notesJson = prefs.getString(notesKey, null) ?: return mutableListOf()

        return try {
            val type = object : TypeToken<List<NoteData>>() {}.type
            val notesData: List<NoteData> = gson.fromJson(notesJson, type)
            notesData.mapNotNull { it.toNote() }.toMutableList()
        } catch (e: Exception) {
            e.printStackTrace()
            // Если ошибка - возвращаем пустой список
            mutableListOf()
        }
    }

}