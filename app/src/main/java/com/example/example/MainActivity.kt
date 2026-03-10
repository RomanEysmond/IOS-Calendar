package com.example.example

import android.content.Context
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.example.data.NotesPreferences
import com.example.example.note.Note
import com.example.example.note.NoteDialog
import com.example.example.note.NotesAdapter
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CalendarAdapter
    private lateinit var monthYearText: TextView
    private lateinit var prevMonthButton: ImageButton
    private lateinit var nextMonthButton: ImageButton

    // Новые элементы для заметок
    private lateinit var notesRecyclerView: RecyclerView
    private lateinit var notesAdapter: NotesAdapter
    private lateinit var addNoteButton: Button
    private lateinit var selectedDateText: TextView
    private lateinit var emptyNotesText: TextView

    private lateinit var notesPreferences: NotesPreferences
    private val notes = mutableListOf<Note>()

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
        notesPreferences = NotesPreferences(this)
        loadNotesFromStorage()
        findViewById<View>(android.R.id.content).rootView.setBackgroundColor(
            ContextCompat.getColor(this, R.color.calendar_background)
        )

        // Инициализируем все views
        initViews()

        // Настраиваем календарь
        setupCalendar()

        // Настраиваем заметки
        setupNotes()

        // Загружаем текущий месяц
        loadMonth(currentYear, currentMonth)
    }



    //Zametki
    private fun initViews() {
        recyclerView = findViewById(R.id.calendarRecyclerView)
        monthYearText = findViewById(R.id.monthYearText)
        prevMonthButton = findViewById(R.id.prevMonthButton)
        nextMonthButton = findViewById(R.id.nextMonthButton)

        // Инициализация элементов для заметок
        notesRecyclerView = findViewById(R.id.notesRecyclerView)
        addNoteButton = findViewById(R.id.addNoteButton)
        selectedDateText = findViewById(R.id.selectedDateText)
        emptyNotesText = findViewById(R.id.emptyNotesText)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupCalendar() {
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
                updateSelectedDateText()
                showNotesForSelectedDate()
                //Toast.makeText(this, "Выбрана ${formatDate(date)}", Toast.LENGTH_SHORT).show()
            }
        }
        recyclerView.adapter = adapter

        // Обработчики кнопок навигации по месяцам
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
    private fun setupNotes() {
        notesAdapter = NotesAdapter(
            notes = emptyList(),
            onNoteClick = { note ->
                // Редактирование заметки
                showNoteDialog(note.date, note)
            },
            onNoteLongClick = { note ->
                // Удаление заметки по долгому нажатию
                showDeleteConfirmation(note)
                true
            }
        )

        notesRecyclerView.layoutManager = LinearLayoutManager(this)
        notesRecyclerView.adapter = notesAdapter

        addNoteButton.setOnClickListener {
            selectedDate?.let { date ->
                showNoteDialog(date, null)
            } ?: run {
                Toast.makeText(this, "Сначала выберите дату", Toast.LENGTH_SHORT).show()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun loadMonth(year: Int, month: Int) {
        val days = generateDaysForMonth(year, month)
        adapter.updateDays(days)
        adapter.setNotes(notes)

        // Обновить заголовок (название месяца и год)
        val monthName = YearMonth.of(year, month)
            .month
            .getDisplayName(TextStyle.FULL_STANDALONE, Locale("ru"))
        monthYearText.text = "${monthName.replaceFirstChar { it.uppercase() }} $year"

        // Сбрасываем выбранную дату при смене месяца
        selectedDate = null
        adapter.setSelectedDate(null)
        updateSelectedDateText()

        // Скрываем заметки
        notesRecyclerView.visibility = View.GONE
        emptyNotesText.visibility = View.GONE
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun showNoteDialog(date: LocalDate, existingNote: Note?) {
        val dialog = NoteDialog(
            date = date,
            existingNote = existingNote,
            onSave = { note ->
                if (note.text.isEmpty()) {
                    // Удаление заметки
                    notes.removeAll { it.date == note.date }
                } else {
                    // Добавление или обновление заметки
                    val existingIndex = notes.indexOfFirst { it.date == note.date }
                    if (existingIndex >= 0) {
                        notes[existingIndex] = note
                    } else {
                        notes.add(note)
                    }
                }

                // Сохраняем в SharedPreferences
                saveNotesToStorage()

                // Обновление UI
                adapter.setNotes(notes)
                showNotesForSelectedDate()

                val message = when {
                    existingNote == null -> "Заметка добавлена"
                    note.text.isEmpty() -> "Заметка удалена"
                    else -> "Заметка обновлена"
                }
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            }
        )
        dialog.show(supportFragmentManager, "NoteDialog")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun showDeleteConfirmation(note: Note) {
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Удаление заметки")
            .setMessage("Вы уверены, что хотите удалить эту заметку?")
            .setPositiveButton("Да") { _, _ ->
                notes.remove(note)
                adapter.setNotes(notes)
                saveNotesToStorage() // Сохраняем после удаления
                showNotesForSelectedDate()
                Toast.makeText(this, "Заметка удалена", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Нет", null)
            .show()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun showNotesForSelectedDate() {
        selectedDate?.let { date ->
            val notesForDate = notes.filter { it.date == date }

            if (notesForDate.isEmpty()) {
                notesRecyclerView.visibility = View.GONE
                emptyNotesText.visibility = View.VISIBLE
                emptyNotesText.text = "Нет заметок для ${formatDate(date)}"
            } else {
                notesAdapter.updateNotes(notesForDate)
                notesRecyclerView.visibility = View.VISIBLE
                emptyNotesText.visibility = View.GONE
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun updateSelectedDateText() {
        selectedDate?.let { date ->
            selectedDateText.text = "Выбрана: ${formatDate(date)}"
            selectedDateText.visibility = View.VISIBLE
            addNoteButton.isEnabled = true
        } ?: run {
            selectedDateText.text = "Дата не выбрана"
            addNoteButton.isEnabled = false
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun formatDate(date: LocalDate): String {
        val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
        return date.format(formatter)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun generateDaysForMonth(year: Int, month: Int): List<DayItem> {
        val yearMonth = YearMonth.of(year, month)
        val firstOfMonth = yearMonth.atDay(1)

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

        // Дни предыдущего месяца
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

        // Дни следующего месяца (до 42 ячеек - 6 рядов по 7 дней)
        val nextMonth = yearMonth.plusMonths(1)
        var nextDay = 1
        while (days.size < 42) {
            val date = nextMonth.atDay(nextDay)
            days.add(DayItem(date, nextDay, false))
            nextDay++
        }

        return days
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun loadNotesFromStorage() {
        notes.clear()
        notes.addAll(notesPreferences.loadNotes())
    }
    private fun saveNotesToStorage() {
        notesPreferences.saveNotes(notes)
    }
}