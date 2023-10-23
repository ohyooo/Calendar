package com.ohyooo.shared.compose

import androidx.compose.animation.core.AnimationConstants
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
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
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ExpandLess
import androidx.compose.material.icons.outlined.ExpandMore
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ohyooo.shared.util.StateClass
import com.ohyooo.shared.util.currentLocaleDate
import com.ohyooo.shared.util.getHighlightRange
import com.ohyooo.shared.util.getLunarDay
import com.ohyooo.shared.util.getMonthDay
import com.ohyooo.shared.util.hourMinuteSecondNow
import com.ohyooo.shared.util.monthYearFromDate
import com.ohyooo.shared.util.prevDaySize
import com.ohyooo.shared.util.saturdayOfWeek
import com.ohyooo.shared.util.yearMonthDayWithLunarNow
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import kotlinx.datetime.plus
import kotlinx.datetime.until

@Composable
fun CalendarMain() {
    val currentLocaleDate = currentLocaleDate
    val prevDaySize = prevDaySize

    var currentMonth by remember { mutableStateOf(currentLocaleDate) }

    val state = rememberSaveable(saver = LazyGridState.Saver) {
        LazyGridState(prevDaySize - currentLocaleDate.dayOfMonth + 2, 0)
    }

    val coroutineScope = rememberCoroutineScope()

    val scrollState = StateClass()

    Column(modifier = Modifier.background(mainBgColor)) {
        Clock {
            coroutineScope.launch {
                state.animateScrollToItem(prevDaySize - currentLocaleDate.dayOfMonth + 2)
                delay(AnimationConstants.DefaultDurationMillis.toLong())
                scrollState.onScroll(true)
            }
        }

        Divider(color = Color.Gray)

        CalendarTitle(currentMonth) { isUp ->
            coroutineScope.launch {
                state.animateScrollToItem(state.firstVisibleItemIndex + 7 * 6 * if (isUp) 1 else -1)
                delay(AnimationConstants.DefaultDurationMillis.toLong())
                scrollState.onScroll(true)
            }
        }

        CalendarWeekDays()

        CalendarMonth(state, scrollState, currentLocaleDate, prevDaySize) {
            currentMonth = it
        }
    }
}

@Composable
fun Clock(onClick: () -> Unit) {
    Column(modifier = Modifier.padding(start = 12.dp, top = 16.dp, end = 12.dp, bottom = 16.dp)) {
        var time by rememberSaveable { mutableStateOf("") }
        var dat by rememberSaveable { mutableStateOf("") }

        LaunchedEffect(this) {
            while (true) {
                time = hourMinuteSecondNow()
                dat = yearMonthDayWithLunarNow()
                delay(1000)
            }
        }

        Text(text = time, fontSize = 32.sp, color = clockColor)

        Text(text = dat, fontSize = 14.sp, color = dateColor, modifier = Modifier.clickable(onClick = onClick))
    }
}

@Composable
fun CalendarTitle(date: LocalDate, onClick: (Boolean) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = monthYearFromDate(date),
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .padding(start = 12.dp, top = 16.dp, end = 12.dp, bottom = 16.dp),
            color = monthTitleColor,
            fontSize = 16.sp
        )

        repeat(5) {
            ScrollIcon(null, "space") { }
        }

        ScrollIcon(Icons.Outlined.ExpandMore, "UP") { onClick(true) }
        ScrollIcon(Icons.Outlined.ExpandLess, "DOWN") { onClick(false) }
    }
}

@Composable
fun CalendarWeekDays() {
    Row(content = {
        repeat(7) {
            Text(text = saturdayOfWeek(it), textAlign = TextAlign.Center, color = dayOfWeekColor, modifier = Modifier.weight(1F))
        }
    })
}

fun monthDays(year: Int, month: Month): Int {
    val start = LocalDate(year, month, 1)
    val end = start.plus(1, DateTimeUnit.MONTH)
    return start.until(end, DateTimeUnit.DAY)
}

@Composable
fun CalendarMonth(state: LazyGridState, scrollState: StateClass, nowLocaleDate: LocalDate, days: Int, onDateChange: (LocalDate) -> Unit) {
    var currentMonthRange by remember {
        val nowLocaleDateLengthOfMonth = monthDays(nowLocaleDate.year, nowLocaleDate.month)
        mutableStateOf(state.firstVisibleItemIndex..days + nowLocaleDateLengthOfMonth - nowLocaleDate.dayOfMonth + 1)
    }

    val coroutineScope = rememberCoroutineScope()

    var job by rememberSaveable { mutableStateOf<Job?>(null) }

    var firstItem = state.firstVisibleItemIndex

    scrollState.onScroll = scroll@{
        if (firstItem == state.firstVisibleItemIndex && !it) return@scroll
        firstItem = state.firstVisibleItemIndex

        job?.cancel()
        job = coroutineScope.launch {
            delay(AnimationConstants.DefaultDurationMillis.toLong())
            if (!this.isActive) return@launch
            currentMonthRange = getHighlightRange(state.firstVisibleItemIndex..state.firstVisibleItemIndex + state.layoutInfo.visibleItemsInfo.size)
            if (!this.isActive) return@launch
            onDateChange(getMonthDay(currentMonthRange.first))
        }
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(7),
        state = state,
        modifier = Modifier.scrollable(
            orientation = Orientation.Vertical,
            state = rememberScrollableState { delta ->
                scrollState.onScroll(false)
                delta
            }
        ),
        content = {
            items(count = days * 2) { day ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .aspectRatio(1F)
                        .background(if (days == day - 1) todayBgColor else Color.Transparent)
                        .clickable {},
                    contentAlignment = Alignment.Center
                ) {
                    val addText = @Composable {
                        Text(
                            text = getLunarDay(day),
                            color = if (day in currentMonthRange) dayOfHighlightedColor else dayOfNormalColor,
                            textAlign = TextAlign.Center,
                        )
                    }
                    if (days == day - 1) {
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
        }
    )
}

@Composable
fun RowScope.ScrollIcon(imageVector: ImageVector?, contentDescription: String, onClick: () -> Unit) {
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
                modifier = Modifier.size(32.dp), contentDescription = contentDescription, tint = monthTitleColor
            )
        }
    }
}
