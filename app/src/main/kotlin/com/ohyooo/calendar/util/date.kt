package com.ohyooo.calendar.util

import java.time.LocalDate
import java.time.LocalDateTime


val currentLocaleDate: LocalDateTime get() = LocalDate.now().atStartOfDay()

val prevDaySize get() = 7L * 5200 + currentLocaleDate.dayOfWeek.value - 1 // offset -> - 1 items start from 0

val firstDay: LocalDateTime get() = currentLocaleDate.minusDays(prevDaySize)

fun getMonthDay(day: Int): LocalDateTime {
    return firstDay.plusDays(day.toLong() - 1)
}