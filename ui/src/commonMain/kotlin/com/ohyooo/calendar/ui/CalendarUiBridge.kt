package com.ohyooo.calendar.ui

import com.ohyooo.shared.calendar.CalendarDateCalculator
import kotlinx.datetime.LocalDateTime

class CalendarUiBridge {
    fun currentMonthTitle(
        year: Int,
        month: Int,
        day: Int,
        hour: Int,
        minute: Int,
        second: Int,
    ): String {
        val state = CalendarDateCalculator.initialState(
            LocalDateTime(year, month, day, hour, minute, second)
        )

        return CalendarDateCalculator.monthTitle(state.currentMonth)
    }

    fun clockText(
        year: Int,
        month: Int,
        day: Int,
        hour: Int,
        minute: Int,
        second: Int,
    ): String {
        val clock = CalendarDateCalculator.clockUiState(
            LocalDateTime(year, month, day, hour, minute, second)
        )

        return "${clock.time} ${clock.date}"
    }
}
