package com.ohyooo.shared.calendar

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CalendarDateCalculatorTest {
    @Test
    fun initialStateAnchorsTodayAndCurrentMonth() {
        val state = CalendarDateCalculator.initialState(LocalDateTime(2026, 5, 27, 12, 34, 56))

        assertEquals(LocalDate(2026, 5, 27), state.today)
        assertEquals(LocalDate(2026, 5, 27), CalendarDateCalculator.dateForIndex(state.calendarStartDate, state.todayIndex))
        assertEquals(LocalDate(2026, 5, 1), CalendarDateCalculator.dateForIndex(state.calendarStartDate, state.initialFirstVisibleItemIndex))
        assertEquals("12:34:56", state.clock.time)
        assertTrue(state.todayIndex in state.highlightedRange)
    }

    @Test
    fun monthNavigationTargetsAreClampedToCalendarBounds() {
        assertEquals(
            0,
            CalendarDateCalculator.targetIndexFor(
                direction = CalendarScrollDirection.Previous,
                firstVisibleItemIndex = 10,
                totalDayCount = 100,
            )
        )
        assertEquals(
            99,
            CalendarDateCalculator.targetIndexFor(
                direction = CalendarScrollDirection.Next,
                firstVisibleItemIndex = 90,
                totalDayCount = 100,
            )
        )
    }

    @Test
    fun highlightedRangeSelectsTheMonthWithTheMostVisibleDays() {
        val state = CalendarDateCalculator.initialState(LocalDateTime(2026, 5, 27, 12, 34, 56))
        val highlightedRange = CalendarDateCalculator.highlightedRange(
            startDate = state.calendarStartDate,
            visibleRange = state.initialFirstVisibleItemIndex..state.initialFirstVisibleItemIndex + 42,
        )

        assertEquals(LocalDate(2026, 5, 1), CalendarDateCalculator.dateForIndex(state.calendarStartDate, highlightedRange.first))
        assertEquals(LocalDate(2026, 5, 31), CalendarDateCalculator.dateForIndex(state.calendarStartDate, highlightedRange.last))
    }
}
