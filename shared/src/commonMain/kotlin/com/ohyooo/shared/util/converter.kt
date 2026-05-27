package com.ohyooo.shared.util

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
