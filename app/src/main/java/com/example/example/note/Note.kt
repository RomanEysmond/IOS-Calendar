package com.example.example.note

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate
import java.util.UUID

data class Note(
    val date: LocalDate,
    val text: String,
    val id: String = UUID.randomUUID().toString()
) {
    constructor(date: LocalDate, text: String) : this(date, text, UUID.randomUUID().toString())
   }