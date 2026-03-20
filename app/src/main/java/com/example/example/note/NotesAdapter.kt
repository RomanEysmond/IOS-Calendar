package com.example.example.note

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.example.R

class NotesAdapter(
    private var notes: List<Note>,
    private val onNoteClick: (Note) -> Unit,
    private val onNoteLongClick: (Note) -> Boolean
) : RecyclerView.Adapter<NotesAdapter.NoteViewHolder>() {

    class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val noteText: TextView = itemView.findViewById(R.id.noteText)
        private val noteDate: TextView = itemView.findViewById(R.id.noteDate)
        private val noteNumber: TextView = itemView.findViewById(R.id.noteNumber)


        @SuppressLint("SetTextI18n")
        fun bind(note: Note, position: Int) {
            noteText.text = note.text
            noteDate.text = note.date.toString()
            noteNumber.text = "#${position + 1}"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_note, parent, false)
        return NoteViewHolder(view)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val note = notes[position]
        holder.bind(note, position)

        holder.itemView.setOnClickListener {
            onNoteClick(note)
        }

        holder.itemView.setOnLongClickListener {
            onNoteLongClick(note)
        }
    }

    override fun getItemCount() = notes.size

    fun updateNotes(newNotes: List<Note>) {
        notes = newNotes
        notifyDataSetChanged()
    }
}