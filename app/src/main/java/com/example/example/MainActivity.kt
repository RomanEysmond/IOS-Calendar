package com.example.example

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CalendarAdapter
    private lateinit var monthYearText: TextView
    private lateinit var prevMonthButton: ImageButton
    private lateinit var nextMonthButton: ImageButton

    @RequiresApi(Build.VERSION_CODES.O)
    private var currentYear: Int = LocalDate.now().year

    @RequiresApi(Build.VERSION_CODES.O)
    private var currentMonth: Int = LocalDate.now().monthValue

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.calendarRecyclerView)
        monthYearText = findViewById(R.id.monthYearText)
        prevMonthButton = findViewById(R.id.prevMonthButton)
        nextMonthButton = findViewById(R.id.nextMonthButton)

        recyclerView.layoutManager = GridLayoutManager(this, 7)


        adapter = CalendarAdapter(emptyList()) { date ->
            Toast.makeText(this, "Выбрана $date", Toast.LENGTH_SHORT).show()
        }
        recyclerView.adapter = adapter

        // Загрузить текущий месяц
        loadMonth(currentYear, currentMonth)

        // Обработчики кнопок
        prevMonthButton.setOnClickListener {
            // Перейти на предыдущий месяц
            if (currentMonth == 1) {
                currentMonth = 12
                currentYear -= 1
            } else {
                currentMonth -= 1
            }
            loadMonth(currentYear, currentMonth)
        }

        nextMonthButton.setOnClickListener {
            // Перейти на следующий месяц
            if (currentMonth == 12) {
                currentMonth = 1
                currentYear += 1
            } else {
                currentMonth += 1
            }
            loadMonth(currentYear, currentMonth)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun loadMonth(year: Int, month: Int) {
        val days = generateDaysForMonth(year, month)
        adapter.updateDays(days)

        // Обновить заголовок (название месяца и год)
        val monthName =
            YearMonth.of(year, month).month.getDisplayName(TextStyle.FULL_STANDALONE, Locale("ru"))
        monthYearText.text = "${monthName.replaceFirstChar { it.uppercase() }} $year"
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun generateDaysForMonth(year: Int, month: Int): List<DayItem> {
    val yearMonth = YearMonth.of(year, month)
    val firstOfMonth = yearMonth.atDay(1)
    val lastOfMonth = yearMonth.atEndOfMonth()

    // День недели первого числа (понедельник = 1, воскресенье = 7, но в Java Sunday = 7, а нам нужно под нашу неделю)
    // Пусть неделя начинается с воскресенья, как в iOS (или понедельника, смотря как вам нужно)
    val firstDayOfWeek =
        firstOfMonth.dayOfWeek.value % 7  // в Java Monday=1, Sunday=7; преобразуем: воскресенье станет 0
    // Для iOS-календаря неделя обычно начинается с воскресенья. Тогда индекс первого дня: если воскресенье = 0, понедельник = 1, …, суббота = 6.
    // В Java DayOfWeek.SUNDAY.getValue() = 7, поэтому firstDayOfWeekIndex = (7 - firstDayOfWeekValue) % 7? Проще вычислить смещение.

    // Упростим: будем считать, что дни недели начинаются с воскресенья.
    val startOffset = when (firstOfMonth.dayOfWeek) {
        DayOfWeek.SUNDAY -> 6
        DayOfWeek.MONDAY -> 0
        DayOfWeek.TUESDAY -> 1
        DayOfWeek.WEDNESDAY -> 2
        DayOfWeek.THURSDAY -> 3
        DayOfWeek.FRIDAY -> 4
        DayOfWeek.SATURDAY -> 5
    }

    val days = mutableListOf<DayItem>()

    // Дни предыдущего месяца (пустые)
    for (i in 0 until startOffset) {
        days.add(DayItem(null, 0))
    }

    // Дни текущего месяца
    for (day in 1..yearMonth.lengthOfMonth()) {
        days.add(DayItem(yearMonth.atDay(day), day))
    }

    // Дни следующего месяца (пустые) до 42
    val remaining = 42 - days.size
    for (i in 0 until remaining) {
        days.add(DayItem(null, 0))
    }

    return days
}

