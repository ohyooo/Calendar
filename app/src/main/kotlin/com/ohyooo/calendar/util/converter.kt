package com.ohyooo.calendar.util

import androidx.annotation.IntRange
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.util.*

fun monthYearFromDate(date: LocalDateTime): String {
    val formatter = SimpleDateFormat("yyyy年MMMM", Locale.getDefault())
    return formatter.format(date.toInstant(ZoneOffset.ofHours(8)).toEpochMilli())
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

fun saturdayOfWeek(@IntRange(from = 0L, to = 6L) day: Int): String {
    return when (day + 1) {
        1 -> "日"
        2 -> "一"
        3 -> "二"
        4 -> "三"
        5 -> "四"
        6 -> "五"
        7 -> "六"
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
    return lastMaxDaysIndex - currentMonthDayCount + 1..lastMaxDaysIndex
}

fun getDay(day: Int): String {
    val date = firstDay.plusDays(day.toLong() - 1)
    // return "$day\n${date.year} ${date.monthValue} ${date.dayOfMonth}"
    return "${date.dayOfMonth}"
}