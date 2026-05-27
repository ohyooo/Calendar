package com.ohyooo.shared.compose

import androidx.compose.animation.core.AnimationConstants
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ExpandLess
import androidx.compose.material.icons.outlined.ExpandMore
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ohyooo.shared.calendar.CalendarDateCalculator
import com.ohyooo.shared.calendar.CalendarEffect
import com.ohyooo.shared.calendar.CalendarIntent
import com.ohyooo.shared.calendar.CalendarScrollDirection
import com.ohyooo.shared.calendar.CalendarStore
import com.ohyooo.shared.calendar.CalendarUiState
import com.ohyooo.shared.calendar.ClockUiState
import com.ohyooo.shared.common.Text
import com.ohyooo.shared.util.saturdayOfWeek
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.datetime.LocalDate

@Composable
fun CalendarMain(
    store: CalendarStore = rememberCalendarStore(),
) {
    val uiState by store.state.collectAsState()
    val state = rememberSaveable(saver = LazyGridState.Saver) {
        LazyGridState(
            firstVisibleItemIndex = uiState.initialFirstVisibleItemIndex,
            firstVisibleItemScrollOffset = 0,
        )
    }

    CalendarEffects(store, state)
    CalendarViewportObserver(store, state)

    Column(modifier = Modifier.background(mainBgColor)) {
        Clock(clock = uiState.clock) {
            store.dispatch(CalendarIntent.TodaySelected)
        }

        Divider(color = Color.Gray)

        CalendarTitle(uiState.currentMonth) { direction ->
            store.dispatch(CalendarIntent.MonthNavigationSelected(direction))
        }

        CalendarWeekDays()

        CalendarMonth(state, uiState)
    }
}

@Composable
private fun rememberCalendarStore(): CalendarStore {
    val scope = rememberCoroutineScope()
    val store = remember { CalendarStore(scope) }

    DisposableEffect(store) {
        onDispose {
            store.dispose()
        }
    }

    return store
}

@Composable
private fun CalendarEffects(store: CalendarStore, state: LazyGridState) {
    LaunchedEffect(store, state) {
        store.effects.collect { effect ->
            when (effect) {
                is CalendarEffect.ScrollToItem -> {
                    state.animateScrollToItem(effect.index)
                    delay(AnimationConstants.DefaultDurationMillis.toLong())
                    store.dispatch(
                        CalendarIntent.ViewportChanged(
                            firstVisibleItemIndex = state.firstVisibleItemIndex,
                            visibleItemCount = state.layoutInfo.visibleItemsInfo.size,
                            force = true,
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun CalendarViewportObserver(store: CalendarStore, state: LazyGridState) {
    LaunchedEffect(store, state) {
        snapshotFlow {
            CalendarViewport(
                firstVisibleItemIndex = state.firstVisibleItemIndex,
                visibleItemCount = state.layoutInfo.visibleItemsInfo.size,
            )
        }.distinctUntilChanged()
            .collect { viewport ->
                store.dispatch(
                    CalendarIntent.ViewportChanged(
                        firstVisibleItemIndex = viewport.firstVisibleItemIndex,
                        visibleItemCount = viewport.visibleItemCount,
                    )
                )
            }
    }
}

@Composable
private fun Clock(clock: ClockUiState, onClick: () -> Unit) {
    Column(modifier = Modifier.padding(start = 12.dp, top = 16.dp, end = 12.dp, bottom = 16.dp)) {
        Text(text = clock.time, fontSize = 32.sp, color = clockColor)

        Text(text = clock.date, fontSize = 14.sp, color = dateColor, modifier = Modifier.clickable(onClick = onClick))
    }
}

@Composable
private fun CalendarTitle(date: LocalDate, onClick: (CalendarScrollDirection) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = CalendarDateCalculator.monthTitle(date),
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .padding(start = 12.dp, top = 16.dp, end = 12.dp, bottom = 16.dp),
            color = monthTitleColor,
            fontSize = 16.sp
        )

        repeat(5) {
            ScrollIcon(null, "space") { }
        }

        ScrollIcon(Icons.Outlined.ExpandMore, "NEXT_MONTH") { onClick(CalendarScrollDirection.Next) }
        ScrollIcon(Icons.Outlined.ExpandLess, "PREVIOUS_MONTH") { onClick(CalendarScrollDirection.Previous) }
    }
}

@Composable
private fun CalendarWeekDays() {
    Row(content = {
        repeat(7) {
            Text(text = saturdayOfWeek(it), textAlign = TextAlign.Center, color = dayOfWeekColor, modifier = Modifier.weight(1F))
        }
    })
}

@Composable
private fun CalendarMonth(state: LazyGridState, uiState: CalendarUiState) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(7),
        state = state,
        content = {
            items(count = uiState.totalDayCount) { day ->
                CalendarDay(day, uiState)
            }
        }
    )
}

@Composable
private fun CalendarDay(day: Int, uiState: CalendarUiState) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .aspectRatio(1F)
            .background(if (uiState.todayIndex == day) todayBgColor else Color.Transparent),
        contentAlignment = Alignment.Center
    ) {
        val addText = @Composable {
            Text(
                text = remember(uiState.calendarStartDate, day) {
                    CalendarDateCalculator.dayText(uiState.calendarStartDate, day)
                },
                color = if (day in uiState.highlightedRange) dayOfHighlightedColor else dayOfNormalColor,
                textAlign = TextAlign.Center,
            )
        }
        if (uiState.todayIndex == day) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(2.dp)
            ) {
                val modifier = Modifier.fillMaxSize()
                Box(
                    modifier = modifier.border(2.dp, color = mainBgColor),
                    contentAlignment = Alignment.Center
                ) {
                    addText()
                }
            }
        } else {
            addText()
        }
    }
}

@Composable
private fun RowScope.ScrollIcon(imageVector: ImageVector?, contentDescription: String, onClick: () -> Unit) {
    var modifier = Modifier
        .wrapContentHeight()
        .weight(1F)
        .aspectRatio(1F)

    if (imageVector != null) {
        modifier = modifier.clickable(onClick = onClick)
    }
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        imageVector?.let {
            Icon(
                imageVector = imageVector,
                modifier = Modifier.size(32.dp),
                contentDescription = contentDescription,
                tint = monthTitleColor
            )
        }
    }
}

private data class CalendarViewport(
    val firstVisibleItemIndex: Int,
    val visibleItemCount: Int,
)
