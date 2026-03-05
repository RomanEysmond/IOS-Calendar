package com.example.example

import android.graphics.Color
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
            tvDay.text = dayItem.dayOfMonth.toString()

            // Цвет текста: чёрный для текущего месяца, серый для остальных
            if (dayItem.isCurrentMonth) {
                tvDay.setTextColor(Color.BLACK)
            } else {
                tvDay.setTextColor(Color.parseColor("#B0B0B0")) // светло-серый
            }

            // Подсветка сегодняшнего дня (красный кружок)
            if (dayItem.date == today) {
                tvDay.setBackgroundResource(R.drawable.circle_today)
            } else {
                tvDay.background = null
            }

            itemView.setOnClickListener {
                dayItem.date?.let { onClick(it) }
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


data class DayItem(
    val date: LocalDate?,
    val dayOfMonth: Int,
    val isCurrentMonth: Boolean
)