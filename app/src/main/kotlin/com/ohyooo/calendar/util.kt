package com.ohyooo.calendar

import androidx.annotation.IntRange
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

fun daysInMonthArray(date: LocalDate, selectedDate: LocalDate = LocalDate.now()): List<String> {
    val daysInMonthArray = ArrayList<String>()

    val yearMonth = YearMonth.from(date)

    val daysInMonth = yearMonth.lengthOfMonth()

    val firstOfMonth = selectedDate.withDayOfMonth(1)

    val dayOfWeek = firstOfMonth.dayOfWeek.value

    for (i in 1..42) {
        if (i <= dayOfWeek || i > daysInMonth + dayOfWeek) {
            daysInMonthArray.add("")
        } else {
            daysInMonthArray.add((i - dayOfWeek).toString())
        }
    }
    return daysInMonthArray
}

fun daysInMonthArray(ym: YearMonth, selectedDate: LocalDate = LocalDate.now()): List<String> {
    val daysInMonthArray = ArrayList<String>()

    val daysInMonth = ym.lengthOfMonth()

    val firstOfMonth = selectedDate.withDayOfMonth(1)

    val dayOfWeek = firstOfMonth.dayOfWeek.value

    for (i in 1..42) {
        if (i <= dayOfWeek || i > daysInMonth + dayOfWeek) {
            daysInMonthArray.add("")
        } else {
            daysInMonthArray.add((i - dayOfWeek).toString())
        }
    }
    return daysInMonthArray
}

fun monthYearFromDate(date: LocalDate): String {
    val formatter = DateTimeFormatter.ofPattern("yyyy年MMMM")
    return date.format(formatter)
}

fun hourMinuteSecond(date: Long): String {
    val formatter = SimpleDateFormat("hh:mm:ss", Locale.getDefault())
    return formatter.format(date)
}

fun yearMonthDay(date: Long): String {
    val formatter = SimpleDateFormat("yyyy年MMMMDD日", Locale.getDefault())
    return formatter.format(date)
}

fun dayOfWeek(time: Long): String {
    return dayOfWeek(Date(time).toInstant().atZone(ZoneId.systemDefault()).dayOfWeek.value)
}

fun dayOfWeek(@IntRange(from = 0L, to = 6L) day: Int): String {
    return when (day + 1) {
        1 -> "一"
        2 -> "二"
        3 -> "三"
        4 -> "四"
        5 -> "五"
        6 -> "六"
        7 -> "日"
        else -> throw IndexOutOfBoundsException("")
    }
}

fun getHighlightRange(visibleRange: kotlin.ranges.IntRange): kotlin.ranges.IntRange {
    var currentMonth = 0
    var currentMonthDayCount = 0
    var maxMonthDayCount = 0
    var lastMaxDaysIndex = 0
    var isFirstVisibleMonth = 0

    visibleRange.forEach { day ->
        getMonthDay(day).apply {
            if (currentMonth == first) {
                ++currentMonthDayCount
            } else {
                if (isFirstVisibleMonth >= 2) return@forEach
                isFirstVisibleMonth++

                currentMonthDayCount = 1
                currentMonth = first
            }

            if (currentMonthDayCount > maxMonthDayCount) {
                maxMonthDayCount = currentMonthDayCount
                lastMaxDaysIndex = day
            }
        }
    }
    return lastMaxDaysIndex - currentMonthDayCount + 1..lastMaxDaysIndex
}


val currentLocaleDate: LocalDateTime get() = LocalDate.now().atStartOfDay()

fun getMonthDay(day: Int): Pair<Int, Int> {
    val date = currentLocaleDate.minusYears(10).plusDays(day.toLong() - 1)
    return date.monthValue to date.dayOfMonth
}

fun getDay(day: Int): String {
    val date = currentLocaleDate.minusYears(10).plusDays(day.toLong() - 1)
    return "$day\n${date.year} ${date.monthValue} ${date.dayOfMonth}"
}