package com.example.example

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.time.DayOfWeek
import java.time.YearMonth

class MainActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CalendarAdapter

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main) // ваш XML-файл с разметкой

        recyclerView = findViewById(R.id.calendarRecyclerView)
        // Устанавливаем GridLayoutManager с 7 колонками
        recyclerView.layoutManager = GridLayoutManager(this, 7)

        // Генерируем дни для марта 2026 (можно заменить на текущую дату)
        val days = generateDaysForMonth(2026, 3)

        adapter = CalendarAdapter(days) { date ->
            Toast.makeText(this, "Выбрана $date", Toast.LENGTH_SHORT).show()
        }
        recyclerView.adapter = adapter
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun generateDaysForMonth(year: Int, month: Int): List<DayItem> {
    val yearMonth = YearMonth.of(year, month)
    val firstOfMonth = yearMonth.atDay(1)
    val lastOfMonth = yearMonth.atEndOfMonth()

    // День недели первого числа (понедельник = 1, воскресенье = 7, но в Java Sunday = 7, а нам нужно под нашу неделю)
    // Пусть неделя начинается с воскресенья, как в iOS (или понедельника, смотря как вам нужно)
    val firstDayOfWeek = firstOfMonth.dayOfWeek.value % 7  // в Java Monday=1, Sunday=7; преобразуем: воскресенье станет 0
    // Для iOS-календаря неделя обычно начинается с воскресенья. Тогда индекс первого дня: если воскресенье = 0, понедельник = 1, …, суббота = 6.
    // В Java DayOfWeek.SUNDAY.getValue() = 7, поэтому firstDayOfWeekIndex = (7 - firstDayOfWeekValue) % 7? Проще вычислить смещение.

    // Упростим: будем считать, что дни недели начинаются с воскресенья.
    val startOffset = when (firstOfMonth.dayOfWeek) {
        DayOfWeek.SUNDAY -> 0
        DayOfWeek.MONDAY -> 1
        DayOfWeek.TUESDAY -> 2
        DayOfWeek.WEDNESDAY -> 3
        DayOfWeek.THURSDAY -> 4
        DayOfWeek.FRIDAY -> 5
        DayOfWeek.SATURDAY -> 6
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