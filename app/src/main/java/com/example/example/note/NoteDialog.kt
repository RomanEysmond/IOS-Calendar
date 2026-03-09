package com.example.example.note

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.widget.EditText
import androidx.annotation.RequiresApi
import androidx.fragment.app.DialogFragment
import com.example.example.R
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class NoteDialog(
    private val date: LocalDate,
    private val existingNote: Note?,
    private val onSave: (Note) -> Unit
) : DialogFragment() {

    @SuppressLint("MissingInflatedId")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireActivity())
        val inflater = requireActivity().layoutInflater
        val view = inflater.inflate(R.layout.dialog_note, null)

        val editText = view.findViewById<EditText>(R.id.noteEditText)
        existingNote?.let {
            editText.setText(it.text)
        }

        val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
        val dateText = date.format(formatter)

        builder.setView(view)
            .setTitle(if (existingNote == null) "Новая заметка" else "Редактирование заметки")
            .setMessage("Дата: $dateText")
            .setPositiveButton("Сохранить") { _, _ ->
                val text = editText.text.toString()
                if (text.isNotBlank()) {
                    val note = existingNote?.copy(text = text) ?: Note(date, text)
                    onSave(note)
                }
            }
            .setNegativeButton("Отмена", null)
            .setNeutralButton("Удалить") { _, _ ->
                if (existingNote != null) {
                    AlertDialog.Builder(requireActivity())
                        .setTitle("Удаление заметки")
                        .setMessage("Вы уверены, что хотите удалить эту заметку?")
                        .setPositiveButton("Да") { _, _ ->
                            onSave(existingNote.copy(text = "")) // Пустой текст = удаление
                        }
                        .setNegativeButton("Нет", null)
                        .show()
                }
            }

        return builder.create()
    }
}