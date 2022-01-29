package com.ohyooo.calendar.util

import androidx.annotation.IntRange
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.util.*

private val monthYearFromDateFormatter = SimpleDateFormat("yyyy年MM月", Locale.CHINA)

fun monthYearFromDate(date: LocalDateTime): String {
    return monthYearFromDateFormatter.format(date.toInstant(ZoneOffset.ofHours(8)).toEpochMilli())
}

private val hourMinuteSecondNowFormatter = SimpleDateFormat("HH:mm:ss", Locale.CHINA)

fun hourMinuteSecondNow(): String {
    return hourMinuteSecondNowFormatter.format(System.currentTimeMillis())
}

private val yearMonthDayNowFormatter = SimpleDateFormat("yyyy年MM月DD日", Locale.CHINA)

fun yearMonthDayWithLunarNow(): String {
    return yearMonthDayNowFormatter.format(System.currentTimeMillis()) + "，星期" + dayOfWeek(LocalDate.now().dayOfWeek.value) + " " + yearMonthDayNowLunar()
}

fun yearMonthDayNowLunar(): String {
    val ld = LunarDate().apply {
        lunarMonth = ""
        lunarDay = ""
        lunarFestival = ""
    }
    LunarCalendarFestivalUtils.initLunarCalendarInfo(LocalDate.now(), ld)
    return ld.lunarMonth + ld.lunarDay
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

fun getDay(day: Int): String {
    val date = firstDay.plusDays(day.toLong() - 1)
    return "$day\n${date.year} ${date.monthValue} ${date.dayOfMonth}"
    // return getLunarDay(day)
}

fun getLunarDay(day: Int): String {
    val date = firstDay.plusDays(day.toLong() - 1).toLocalDate()

    val ld = LunarDate().apply {
        lunarDay = ""
        lunarFestival = ""
        lunarTerm = ""
    }

    LunarCalendarFestivalUtils.initLunarCalendarInfo(date, ld)

    return "${date.dayOfMonth}\n${ld.lunarFestival.orEmpty().ifBlank { ld.lunarTerm.orEmpty().ifBlank { ld.lunarDay } }}"
}
