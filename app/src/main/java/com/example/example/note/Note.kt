package com.example.example.note

import java.io.Serializable
import java.time.LocalDate

data class Note(
    val date: LocalDate,
    val text: String
) : Serializable
