package com.example.example

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
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

    private var selectedDate: LocalDate? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<View>(android.R.id.content).rootView.setBackgroundColor(
            ContextCompat.getColor(this, R.color.calendar_background)
        )

        recyclerView = findViewById(R.id.calendarRecyclerView)
        monthYearText = findViewById(R.id.monthYearText)
        prevMonthButton = findViewById(R.id.prevMonthButton)
        nextMonthButton = findViewById(R.id.nextMonthButton)

        recyclerView.layoutManager = GridLayoutManager(this, 7)


        adapter = CalendarAdapter(emptyList()) { date ->
            // Обработка клика по дате
            if (date.monthValue != currentMonth || date.year != currentYear) {
                // Если дата из другого месяца – переключаемся на этот месяц
                currentYear = date.year
                currentMonth = date.monthValue
                selectedDate = date
                loadMonth(currentYear, currentMonth)
            } else {
                // Дата в текущем месяце
                selectedDate = date
                adapter.setSelectedDate(selectedDate)
                Toast.makeText(this, "Выбрана $date", Toast.LENGTH_SHORT).show()
            }
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


    val firstDayOfWeek = firstOfMonth.dayOfWeek.value % 7

    val startOffset = when (firstOfMonth.dayOfWeek) {
        DayOfWeek.MONDAY -> 0
        DayOfWeek.TUESDAY -> 1
        DayOfWeek.WEDNESDAY -> 2
        DayOfWeek.THURSDAY -> 3
        DayOfWeek.FRIDAY -> 4
        DayOfWeek.SATURDAY -> 5
        DayOfWeek.SUNDAY -> 6
    }

    val days = mutableListOf<DayItem>()

    // Дни предыдущего месяца (пустые)
    val prevMonth = yearMonth.minusMonths(1)
    val prevMonthLength = prevMonth.lengthOfMonth()
    val prevMonthStartDay = prevMonthLength - startOffset + 1
    for (day in prevMonthStartDay..prevMonthLength) {
        val date = prevMonth.atDay(day)
        days.add(DayItem(date, day, false))
    }
    // Дни текущего месяца
    for (day in 1..yearMonth.lengthOfMonth()) {
        val date = yearMonth.atDay(day)
        days.add(DayItem(date, day, true))
    }

    // Дни следующего месяца (пустые) до 42
    val nextMonth = yearMonth.plusMonths(1)
    var nextDay = 1
    while (days.size < 42) {
        val date = nextMonth.atDay(nextDay)
        days.add(DayItem(date, nextDay, false))
        nextDay++
    }

    return days
}

