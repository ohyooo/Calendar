package com.ohyooo.calendar.util

import java.time.LocalDate
import java.time.LocalDateTime


val currentLocaleDate: LocalDateTime get() = LocalDate.now().atStartOfDay()

val prevDaySize get() = 7L * 5200 + currentLocaleDate.dayOfWeek.value - 1 // offset -> - 1 items start from 0

val firstDay: LocalDateTime get() = currentLocaleDate.minusDays(prevDaySize)

fun getMonthDay(day: Int): LocalDateTime {
    return firstDay.plusDays(day.toLong() - 1)
}

fun getHighlightRange(visibleRange: IntRange): IntRange {
    var currentMonth = 0
    var currentMonthDayCount = 0
    var maxMonthDayCount = 0
    var lastMaxDaysIndex = 0
    var isFirstVisibleMonth = 0

    visibleRange.forEach { day ->
        getMonthDay(day).apply {
            if (currentMonth == monthValue) {
                ++currentMonthDayCount
            } else {
                if (isFirstVisibleMonth >= 2) return@forEach
                isFirstVisibleMonth++

                currentMonthDayCount = 1
                currentMonth = monthValue
            }

            if (currentMonthDayCount > maxMonthDayCount) {
                maxMonthDayCount = currentMonthDayCount
                lastMaxDaysIndex = day
            }
        }
    }
    return lastMaxDaysIndex - maxMonthDayCount + 1..lastMaxDaysIndex
}