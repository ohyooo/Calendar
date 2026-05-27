package com.ohyooo.shared.calendar

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class CalendarStore(
    private val scope: CoroutineScope,
    private val clock: CalendarClock = SystemCalendarClock,
    private val scrollSettleDelayMillis: Long = 300L,
) {
    private val _state = MutableStateFlow(CalendarDateCalculator.initialState(clock.now()))
    val state: StateFlow<CalendarUiState> = _state.asStateFlow()

    private val _effects = MutableSharedFlow<CalendarEffect>(extraBufferCapacity = 16)
    val effects: SharedFlow<CalendarEffect> = _effects.asSharedFlow()

    private var clockJob: Job? = null
    private var viewportJob: Job? = null

    init {
        clockJob = scope.launch {
            while (isActive) {
                reduce { currentState ->
                    currentState.copy(clock = CalendarDateCalculator.clockUiState(clock.now()))
                }
                delay(1000)
            }
        }
    }

    fun dispatch(intent: CalendarIntent) {
        when (intent) {
            CalendarIntent.TodaySelected -> scrollTo(_state.value.initialFirstVisibleItemIndex)
            is CalendarIntent.MonthNavigationSelected -> {
                val currentState = _state.value
                val targetIndex = CalendarDateCalculator.targetIndexFor(
                    direction = intent.direction,
                    firstVisibleItemIndex = currentState.firstVisibleItemIndex,
                    totalDayCount = currentState.totalDayCount,
                )
                scrollTo(targetIndex)
            }

            is CalendarIntent.ViewportChanged -> onViewportChanged(intent)
        }
    }

    fun dispose() {
        clockJob?.cancel()
        viewportJob?.cancel()
    }

    private fun onViewportChanged(intent: CalendarIntent.ViewportChanged) {
        val currentState = _state.value
        val firstVisibleItemIndex = intent.firstVisibleItemIndex.coerceIn(
            minimumValue = 0,
            maximumValue = (currentState.totalDayCount - 1).coerceAtLeast(0),
        )
        val visibleItemCount = intent.visibleItemCount.coerceAtLeast(0)

        reduce { state ->
            state.copy(
                firstVisibleItemIndex = firstVisibleItemIndex,
                visibleItemCount = visibleItemCount,
            )
        }

        viewportJob?.cancel()
        viewportJob = scope.launch {
            if (!intent.force) {
                delay(scrollSettleDelayMillis)
            }
            val settledState = _state.value
            val visibleEndIndex = (firstVisibleItemIndex + visibleItemCount)
                .coerceAtMost((settledState.totalDayCount - 1).coerceAtLeast(0))
            val visibleRange = firstVisibleItemIndex..visibleEndIndex
            val highlightedRange = CalendarDateCalculator.highlightedRange(
                startDate = settledState.calendarStartDate,
                visibleRange = visibleRange,
            )
            val currentMonth = CalendarDateCalculator.dateForIndex(
                startDate = settledState.calendarStartDate,
                index = highlightedRange.first,
            )

            reduce { state ->
                state.copy(
                    highlightedRange = highlightedRange,
                    currentMonth = currentMonth,
                )
            }
        }
    }

    private fun scrollTo(index: Int) {
        if (!_effects.tryEmit(CalendarEffect.ScrollToItem(index))) {
            scope.launch {
                _effects.emit(CalendarEffect.ScrollToItem(index))
            }
        }
    }

    private inline fun reduce(transform: (CalendarUiState) -> CalendarUiState) {
        _state.update(transform)
    }
}

fun interface CalendarClock {
    fun now(): LocalDateTime
}

object SystemCalendarClock : CalendarClock {
    @OptIn(ExperimentalTime::class)
    override fun now(): LocalDateTime {
        return Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
    }
}
