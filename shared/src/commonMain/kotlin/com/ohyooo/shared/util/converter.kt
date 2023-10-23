package com.ohyooo.shared.util

import kotlinx.datetime.Clock
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime

// private val monthYearFromDateFormatter = DateTimeFormatter("yyyy年MM月", Locale.CHINA)

fun monthYearFromDate(date: LocalDate): String {
    // return date.toJavaLocalDateTime().format(monthYearFromDateFormatter)
    val yearMonth = "${date.year}年${date.monthNumber}月"
    return yearMonth
}

// private val hourMinuteSecondNowFormatter = DateTimeFormatter("HH:mm:ss", Locale.CHINA)

fun hourMinuteSecondNow(): String {
    val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
    // return now.toJavaLocalDateTime().format(hourMinuteSecondNowFormatter)
    val time = "${now.hour.toString().padStart(2, '0')}:${now.minute.toString().padStart(2, '0')}:${now.second.toString().padStart(2, '0')}"
    return time
}

// private val yearMonthDayNowFormatter = DateTimeFormatter("yyyy年MM月dd日", Locale.CHINA)

fun yearMonthDayWithLunarNow(): String {
    val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
    val dayOfWeekString = dayOfWeek(now.dayOfWeek.ordinal + 1)
    val fullDate = "${now.year}年${now.monthNumber}月${now.dayOfMonth}日"

    return "$fullDate，星期$dayOfWeekString ${yearMonthDayNowLunar(now.date)}"
    // return "${now.toJavaLocalDateTime().format(yearMonthDayNowFormatter)}，星期$dayOfWeekString ${yearMonthDayNowLunar()}"
}

fun yearMonthDayNowLunar(now: LocalDate): String {
    val ld = LunarDate().apply {
        lunarMonth = ""
        lunarDay = ""
        lunarFestival = ""
    }
    LunarCalendarFestivalUtils.initLunarCalendarInfo(now, ld)
    return ld.lunarMonth + ld.lunarDay
}

// ... Assuming LunarCalendarFestivalUtils and LunarDate are properly defined elsewhere

fun dayOfWeek(day: Int): String {
    return when (day) {
        1 -> "一"
        2 -> "二"
        3 -> "三"
        4 -> "四"
        5 -> "五"
        6 -> "六"
        7 -> "日"
        else -> throw IndexOutOfBoundsException("day=$day")
    }
}

fun saturdayOfWeek(day: Int): String {
    val adjustedDay = if (day == 7) 1 else day + 1
    return dayOfWeek(adjustedDay)
}

// ... Assuming firstDay is properly defined elsewhere

fun getDay(day: Int): String {
    val date = firstDay.plus(DatePeriod(days = day - 1))
    return "$day\n${date.year} ${date.monthNumber} ${date.dayOfMonth}"
}

// ... Assuming LunarCalendarFestivalUtils and LunarDate are properly defined elsewhere

fun getLunarDay(day: Int): String {
    try {
        val date = firstDay.plus(DatePeriod(days = day - 1))
        val ld = LunarDate().apply {
            lunarDay = ""
            lunarFestival = ""
            lunarTerm = ""
        }
        LunarCalendarFestivalUtils.initLunarCalendarInfo(date, ld)
        return "${date.dayOfMonth}\n${ld.lunarFestival.orEmpty().ifBlank { ld.lunarTerm.orEmpty().ifBlank { ld.lunarDay } }}"
    } catch (e: Exception) {
        return "$day"
    }
}
