package com.example.example

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.time.LocalDate

class CalendarAdapter(
    private val days: List<DayItem>,      // список из 42 элементов
    private val onItemClick: (LocalDate) -> Unit
) : RecyclerView.Adapter<CalendarAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvDay: TextView = itemView.findViewById(R.id.tvDay)

        fun bind(dayItem: DayItem, onClick: (LocalDate) -> Unit) {
            tvDay.text = if (dayItem.dayOfMonth == 0) "" else dayItem.dayOfMonth.toString()
            // установка цвета, фона для текущего дня и т.д.
            itemView.setOnClickListener {
                if (dayItem.date != null) onClick(dayItem.date)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_calendar_day, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(days[position], onItemClick)
    }

    override fun getItemCount() = days.size
}

// Дата-класс для элемента сетки
data class DayItem(
    val date: LocalDate?,        // null для пустых ячеек
    val dayOfMonth: Int           // 0 для пустых ячеек
)