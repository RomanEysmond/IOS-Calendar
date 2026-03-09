package com.example.example

import android.annotation.SuppressLint
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.example.note.Note
import java.time.LocalDate

class CalendarAdapter(
    private var days: List<DayItem>,
    private val onItemClick: (LocalDate) -> Unit
) : RecyclerView.Adapter<CalendarAdapter.ViewHolder>() {

    @RequiresApi(Build.VERSION_CODES.O)
    private val today = LocalDate.now()
    private var selectedDate: LocalDate? = null

    private var notesMap: Map<LocalDate, List<Note>> = emptyMap()



    @SuppressLint("NotifyDataSetChanged")
    fun setSelectedDate(date: LocalDate?) {
        selectedDate = date
        notifyDataSetChanged() // обновляем все ячейки, чтобы снять/установить выделение
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvDay: TextView = itemView.findViewById(R.id.tvDay)

        val noteIndicator: View = itemView.findViewById(R.id.noteIndicator)

        @SuppressLint("SetTextI18n")
        fun bind(dayItem: DayItem, today: LocalDate, selectedDate: LocalDate?, onClick: (LocalDate) -> Unit) {
            val context = itemView.context
            tvDay.text = dayItem.dayOfMonth.toString()

            tvDay.background = null
            tvDay.alpha = 1.0f

            // Цвет текста по умолчанию (без фона)
            val defaultTextColor = if (dayItem.isCurrentMonth) {
                ContextCompat.getColor(context, R.color.calendar_text_current_month)
            } else {
                ContextCompat.getColor(context, R.color.calendar_text_other_month)
            }
            tvDay.setTextColor(defaultTextColor)

            // Определяем, нужно ли рисовать кружок
            val circleColor = when {
                dayItem.date == selectedDate -> R.color.calendar_selected_circle
                dayItem.date == today -> R.color.calendar_today_circle
                else -> null
            }

            if (circleColor != null) {
                // Создаём круглый фон
                val background = GradientDrawable()
                background.shape = GradientDrawable.OVAL
                background.setColor(ContextCompat.getColor(context, circleColor))
                tvDay.background = background



                // Цвет текста поверх кружка
                val textColorRes = if (dayItem.date == selectedDate) {
                    R.color.calendar_selected_text
                } else {
                    R.color.calendar_today_text
                }
                tvDay.setTextColor(ContextCompat.getColor(context, textColorRes))
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
        val dayItem = days[position]
        holder.bind(dayItem, today, selectedDate, onItemClick)

        val hasNotes = notesMap[dayItem.date]?.isNotEmpty() == true
        holder.noteIndicator.visibility = if (hasNotes && dayItem.isCurrentMonth) View.VISIBLE else View.GONE

        // Устанавливаем альфа-канал для дней не из текущего месяца
        if (!dayItem.isCurrentMonth) {
            holder.tvDay.alpha = 0.5f
            holder.noteIndicator.alpha = 0.3f
        } else {
            holder.tvDay.alpha = 1.0f
            holder.noteIndicator.alpha = 1.0f
        }
    }


    override fun getItemCount() = days.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateDays(newDays: List<DayItem>) {
        days = newDays
        notifyDataSetChanged()
    }

    fun setNotes(notes: List<Note>) {
        notesMap = notes.groupBy { it.date }
        notifyDataSetChanged()
    }

}


data class DayItem(
    val date: LocalDate?,
    val dayOfMonth: Int,
    val isCurrentMonth: Boolean
)