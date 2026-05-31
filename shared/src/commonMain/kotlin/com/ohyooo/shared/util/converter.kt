package com.ohyooo.shared.util

private val weekdays = listOf("一", "二", "三", "四", "五", "六", "日")

fun dayOfWeek(day: Int): String = weekdays.getOrElse(day - 1) {
    throw IndexOutOfBoundsException("day=$day")
}

fun saturdayOfWeek(day: Int): String {
    val adjustedDay = if (day == 7) 1 else day + 1
    return dayOfWeek(adjustedDay)
}
