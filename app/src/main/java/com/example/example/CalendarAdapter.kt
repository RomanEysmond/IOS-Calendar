package com.example.example

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import java.time.LocalDate

class CalendarAdapter(
    private var days: List<DayItem>,
    private val onItemClick: (LocalDate) -> Unit
) : RecyclerView.Adapter<CalendarAdapter.ViewHolder>() {

    @RequiresApi(Build.VERSION_CODES.O)
    private val today = LocalDate.now()

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvDay: TextView = itemView.findViewById(R.id.tvDay)

        fun bind(dayItem: DayItem, today: LocalDate, onClick: (LocalDate) -> Unit) {
            if (dayItem.dayOfMonth == 0) {
                // Пустая ячейка
                tvDay.text = ""
                tvDay.background = null
                itemView.setOnClickListener(null)
            } else {
                tvDay.text = dayItem.dayOfMonth.toString()
                // Подсветка сегодняшнего дня
                if (dayItem.date == today) {
                    tvDay.setBackgroundResource(R.drawable.circle_today) // круглый фон
                } else {
                    tvDay.background = null
                }
                itemView.setOnClickListener {
                    dayItem.date?.let { onClick(it) }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_calendar_day, parent, false)
        return ViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(days[position], today, onItemClick)
    }

    override fun getItemCount() = days.size

    fun updateDays(newDays: List<DayItem>) {
        days = newDays
        notifyDataSetChanged()
    }

}

// Дата-класс для элемента сетки
data class DayItem(
    val date: LocalDate?,        // null для пустых ячеек
    val dayOfMonth: Int           // 0 для пустых ячеек
)