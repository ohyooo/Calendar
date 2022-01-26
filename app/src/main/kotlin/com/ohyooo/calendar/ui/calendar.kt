package com.ohyooo.calendar.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyGridState
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ExpandLess
import androidx.compose.material.icons.outlined.ExpandMore
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ohyooo.calendar.util.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime

@OptIn(ExperimentalFoundationApi::class)
@Preview
@Composable
fun CalendarMain(date: LocalDate = LocalDate.now()) {
    val currentLocaleDate = currentLocaleDate
    val prevDaySize = prevDaySize

    val currentMonth = remember { mutableStateOf(currentLocaleDate) }

    Column(modifier = Modifier.background(mainBgColor)) {
        NowTime()

        Divider(color = Color.Gray)

        val state = rememberSaveable(saver = LazyGridState.Saver) {
            LazyGridState(prevDaySize.toInt() - currentLocaleDate.dayOfMonth + 2, 0)
        }

        val coroutineScope = rememberCoroutineScope()
        val scrollState = StateClass()

        CalendarTitle(currentMonth.value) { isUp ->
            coroutineScope.launch {
                state.animateScrollToItem(state.firstVisibleItemIndex + 7 * 6 * if (isUp) 1 else -1)
                scrollState.onScroll(true)
            }
        }

        CalendarWeekDays()

        CalendarMonth(currentMonth, state, scrollState, currentLocaleDate, prevDaySize.toInt())
    }
}

@Composable
fun NowTime() {
    Column(modifier = Modifier.padding(start = 12.dp, top = 16.dp, end = 12.dp, bottom = 16.dp)) {
        var date by remember {
            mutableStateOf(System.currentTimeMillis())
        }

        LaunchedEffect(this) {
            while (true) {
                date = System.currentTimeMillis()
                delay(1000)
            }
        }

        Text(text = hourMinuteSecond(date), fontSize = 32.sp, color = clockColor)

        Text(text = yearMonthDay(date) + "，星期" + dayOfWeek(date), fontSize = 14.sp, color = dateColor)
    }
}

@Composable
fun CalendarTitle(date: LocalDateTime, onClick: (Boolean) -> Unit) {
    Row {
        Text(
            text = monthYearFromDate(date),
            modifier = Modifier
                .weight(1F)
                .padding(start = 12.dp, top = 16.dp, end = 12.dp, bottom = 16.dp),
            color = monthTitleColor,
            fontSize = 16.sp
        )
        val size = LocalConfiguration.current.screenWidthDp / 7

        ScrollIcon(Icons.Outlined.ExpandMore, size.dp, "UP") { onClick(true) }
        ScrollIcon(Icons.Outlined.ExpandLess, size.dp, "DOWN") { onClick(false) }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CalendarWeekDays() {
    LazyVerticalGrid(cells = GridCells.Fixed(7), content = {
        items(count = 7) {
            Text(text = saturdayOfWeek(it), textAlign = TextAlign.Center, color = dayOfWeekColor)
        }
    })
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CalendarMonth(currentMonth: MutableState<LocalDateTime>, state: LazyGridState, scrollState: StateClass, nowLocaleDate: LocalDateTime, days: Int) {
    var currentMonthRange by remember {
        mutableStateOf(state.firstVisibleItemIndex..days + nowLocaleDate.toLocalDate().lengthOfMonth() - nowLocaleDate.dayOfMonth + 1)
    }

    val coroutineScope = rememberCoroutineScope()
    var job: Job? = null

    var firstItem = state.firstVisibleItemIndex

    scrollState.onScroll = scroll@{
        if (firstItem == state.firstVisibleItemIndex && !it) return@scroll
        firstItem = state.firstVisibleItemIndex

        job?.cancel()
        job = coroutineScope.launch {
            currentMonthRange = getHighlightRange(state.firstVisibleItemIndex..state.firstVisibleItemIndex + state.layoutInfo.visibleItemsInfo.size)
            if (!this.isActive) return@launch
            currentMonth.value = getMonthDay(currentMonthRange.first)
        }
    }

    LazyVerticalGrid(
        cells = GridCells.Fixed(7),
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
                        .background(if (days == day - 1) todayBgColor else Color.Transparent),
                    contentAlignment = Alignment.Center
                ) {
                    val addText = @Composable {
                        Text(
                            text = getDay(day),
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
fun ScrollIcon(imageVector: ImageVector, size: Dp, contentDescription: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .width(size)
            .height(size)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = imageVector,
            modifier = Modifier.size(32.dp), contentDescription = contentDescription, tint = monthTitleColor
        )
    }
}
