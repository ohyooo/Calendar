package com.ohyooo.calendar.util

import java.time.LocalDate
import java.time.LocalDateTime


val currentLocaleDate: LocalDateTime get() = LocalDate.now().atStartOfDay()

val prevDaySize get() = 7L * 5200 + currentLocaleDate.dayOfWeek.value - 2 // offset -> - 1 - 1, items start from 0, dayOfWeek - 1

val firstDay: LocalDateTime get() = currentLocaleDate.minusDays(prevDaySize)

fun getMonthDay(day: Int): LocalDateTime {
    return firstDay.plusDays(day.toLong() - 1)
}