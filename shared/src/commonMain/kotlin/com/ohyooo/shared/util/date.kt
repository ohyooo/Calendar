package com.ohyooo.shared.util

import kotlinx.datetime.DatePeriod
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.until
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
val currentLocaleDate: LocalDate get() = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date

val baseDate: LocalDate = LocalDate(1900, 1, 31)
val baseDateDayOfWeek = baseDate.dayOfWeek.ordinal + 1  // dayOfWeek is an enum in kotlinx-datetime, so we convert it to an int

val prevDaySize get() = (baseDate.until(currentLocaleDate, DateTimeUnit.DAY) - baseDateDayOfWeek - 2).toInt()

val firstDay: LocalDate get() = currentLocaleDate.minus(DatePeriod(days = prevDaySize))

fun getMonthDay(day: Int): LocalDate {
    return firstDay.plus(DatePeriod(days = day - 1))
}

fun getHighlightRange(visibleRange: LongRange): LongRange {
    var currentMonth = 0L
    var currentMonthDayCount = 0L
    var maxMonthDayCount = 0L
    var lastMaxDaysIndex = 0L
    var isFirstVisibleMonth = 0L

    visibleRange.forEach { day ->
        getMonthDay(day.toInt()).apply {
            if (currentMonth == (month.ordinal).toLong()) {
                ++currentMonthDayCount
            } else {
                if (isFirstVisibleMonth >= 2) return@forEach
                isFirstVisibleMonth++

                currentMonthDayCount = 1
                currentMonth = (month.ordinal).toLong()
            }

            if (currentMonthDayCount > maxMonthDayCount) {
                maxMonthDayCount = currentMonthDayCount
                lastMaxDaysIndex = day
            }
        }
    }
    return lastMaxDaysIndex - maxMonthDayCount + 1..lastMaxDaysIndex
}
