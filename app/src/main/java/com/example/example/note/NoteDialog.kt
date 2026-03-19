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
    private val onSave: (Note) -> Unit,
    private val currentNotesCount: Int,
    private val onDelete: (Note) -> Unit
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

        //Проверка лимита заметок
        val isLimitReached = !isEditing() && currentNotesCount >= 3

        builder.setView(view)
            .setTitle(if (existingNote == null) "Новая заметка" else "Редактирование заметки")
            .setMessage("Дата: $dateText")
            .setPositiveButton(if (isEditing()) "Сохранить" else "Добавить") { _, _ ->
                val text = editText.text.toString()
                if (text.isNotBlank()) {
                    if (isEditing()) {
                        // Редактирование существующей заметки
                        val updatedNote = existingNote!!.copy(text = text)
                        onSave(updatedNote)
                    } else if (!isLimitReached) {
                        // Новая заметка (проверяем лимит)
                        val newNote = Note(date, text)
                        onSave(newNote)
                    }
                }
            }
            .setNegativeButton("Отмена", null)
        if (isEditing()) {
            builder.setNeutralButton("Удалить") { _, _ ->
                showDeleteConfirmation()
            }
        }

        if (!isEditing() && isLimitReached) {
            builder.setPositiveButton("Лимит достигнут", null)
        }
        return builder.create()
    }

    private fun isEditing() = existingNote != null

    private fun getDialogTitle(): String {
        return when {
            isEditing() -> "Редактирование заметки"
            currentNotesCount >= 3 -> "Лимит достигнут"
            else -> "Новая заметка"
        }
    }

    private fun getLimitInfo(): String {
        return if (!isEditing()) {
            "\nЗаметок на этот день: $currentNotesCount/3"
        } else {
            ""
        }
    }

    // ДОБАВЛЕНО: подтверждение удаления
    private fun showDeleteConfirmation() {
        AlertDialog.Builder(requireActivity())
            .setTitle("Удаление заметки")
            .setMessage("Вы уверены, что хотите удалить эту заметку?")
            .setPositiveButton("Да") { _, _ ->
                onDelete(existingNote!!)
            }
            .setNegativeButton("Нет", null)
            .show()
    }

}