package com.ohyooo.shared.calendar

import com.ohyooo.shared.util.LunarCalendarFestivalUtils
import com.ohyooo.shared.util.LunarDate
import com.ohyooo.shared.util.dayOfWeek
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.Month
import kotlinx.datetime.number
import kotlinx.datetime.plus
import kotlinx.datetime.until

object CalendarDateCalculator {
    private val baseDate = LocalDate(1900, 1, 31)
    private val baseDateDayOfWeek = baseDate.dayOfWeek.ordinal + 1
    private const val DAYS_IN_CALENDAR_PAGE = 7 * 6

    fun initialState(now: LocalDateTime): CalendarUiState {
        val today = now.date
        val previousDayCount = previousDayCount(today)
        val startDate = today.minusDays(previousDayCount)
        val initialFirstVisibleItemIndex = previousDayCount - today.day + 2
        val highlightedRange = initialFirstVisibleItemIndex..
                previousDayCount + monthDays(today.year, today.month) - today.day + 1

        return CalendarUiState(
            clock = clockUiState(now),
            today = today,
            calendarStartDate = startDate,
            totalDayCount = previousDayCount * 2,
            todayIndex = previousDayCount + 1,
            initialFirstVisibleItemIndex = initialFirstVisibleItemIndex,
            firstVisibleItemIndex = initialFirstVisibleItemIndex,
            visibleItemCount = 0,
            currentMonth = today,
            highlightedRange = highlightedRange,
        )
    }

    fun clockUiState(now: LocalDateTime): ClockUiState {
        val weekday = dayOfWeek(now.dayOfWeek.ordinal + 1)
        val fullDate = "${now.year}年${now.month.number}月${now.day}日"
        val lunarDate = lunarMonthDay(now.date)

        return ClockUiState(
            time = "${now.hour.padded()}:${now.minute.padded()}:${now.second.padded()}",
            date = "$fullDate，星期$weekday $lunarDate",
        )
    }

    fun monthTitle(date: LocalDate): String {
        return "${date.year}年${date.month.number}月"
    }

    fun dateForIndex(startDate: LocalDate, index: Int): LocalDate {
        return startDate.plus(DatePeriod(days = index - 1))
    }

    fun dayText(startDate: LocalDate, index: Int): String {
        return try {
            val date = dateForIndex(startDate, index)
            val lunarDate = LunarDate().apply {
                lunarDay = ""
                lunarFestival = ""
                lunarTerm = ""
            }
            LunarCalendarFestivalUtils.initLunarCalendarInfo(date, lunarDate)
            val subText = lunarDate.lunarFestival.orEmpty()
                .ifBlank { lunarDate.lunarTerm.orEmpty() }
                .ifBlank { lunarDate.lunarDay }

            "${date.day}\n$subText"
        } catch (e: Exception) {
            index.toString()
        }
    }

    fun highlightedRange(startDate: LocalDate, visibleRange: IntRange): IntRange {
        var currentMonthKey: Int? = null
        var currentMonthDayCount = 0
        var maxMonthDayCount = 0
        var lastMaxDaysIndex = visibleRange.first
        var visibleMonthCount = 0

        visibleRange.forEach { index ->
            val date = dateForIndex(startDate, index)
            val monthKey = date.year * 12 + date.month.number

            if (currentMonthKey == monthKey) {
                currentMonthDayCount++
            } else {
                if (visibleMonthCount >= 2) return@forEach
                visibleMonthCount++
                currentMonthDayCount = 1
                currentMonthKey = monthKey
            }

            if (currentMonthDayCount > maxMonthDayCount) {
                maxMonthDayCount = currentMonthDayCount
                lastMaxDaysIndex = index
            }
        }

        return lastMaxDaysIndex - maxMonthDayCount + 1..lastMaxDaysIndex
    }

    fun targetIndexFor(direction: CalendarScrollDirection, firstVisibleItemIndex: Int, totalDayCount: Int): Int {
        val lastIndex = (totalDayCount - 1).coerceAtLeast(0)
        return (firstVisibleItemIndex + DAYS_IN_CALENDAR_PAGE * direction.pageDelta).coerceIn(0, lastIndex)
    }

    private fun previousDayCount(today: LocalDate): Int {
        return (baseDate.until(today, DateTimeUnit.DAY) - baseDateDayOfWeek - 2).toInt()
    }

    private fun monthDays(year: Int, month: Month): Int {
        val start = LocalDate(year, month, 1)
        val end = start.plus(1, DateTimeUnit.MONTH)
        return start.until(end, DateTimeUnit.DAY).toInt()
    }

    private fun lunarMonthDay(date: LocalDate): String {
        val lunarDate = LunarDate().apply {
            lunarMonth = ""
            lunarDay = ""
            lunarFestival = ""
        }
        LunarCalendarFestivalUtils.initLunarCalendarInfo(date, lunarDate)
        return lunarDate.lunarMonth + lunarDate.lunarDay
    }

    private fun LocalDate.minusDays(days: Int): LocalDate {
        return plus(DatePeriod(days = -days))
    }

    private fun Int.padded(): String {
        return toString().padStart(2, '0')
    }
}
