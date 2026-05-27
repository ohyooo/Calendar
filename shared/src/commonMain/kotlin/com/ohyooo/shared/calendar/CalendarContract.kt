package com.ohyooo.shared.calendar

import kotlinx.datetime.LocalDate

data class CalendarUiState(
    val clock: ClockUiState,
    val today: LocalDate,
    val calendarStartDate: LocalDate,
    val totalDayCount: Int,
    val todayIndex: Int,
    val initialFirstVisibleItemIndex: Int,
    val firstVisibleItemIndex: Int,
    val visibleItemCount: Int,
    val currentMonth: LocalDate,
    val highlightedRange: IntRange,
)

data class ClockUiState(
    val time: String,
    val date: String,
)

sealed interface CalendarIntent {
    data object TodaySelected : CalendarIntent

    data class MonthNavigationSelected(
        val direction: CalendarScrollDirection,
    ) : CalendarIntent

    data class ViewportChanged(
        val firstVisibleItemIndex: Int,
        val visibleItemCount: Int,
        val force: Boolean = false,
    ) : CalendarIntent
}

enum class CalendarScrollDirection(
    val pageDelta: Int,
) {
    Previous(-1),
    Next(1),
}

sealed interface CalendarEffect {
    data class ScrollToItem(
        val index: Int,
    ) : CalendarEffect
}
