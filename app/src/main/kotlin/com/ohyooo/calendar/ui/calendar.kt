package com.ohyooo.calendar.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyGridState
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ohyooo.calendar.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneOffset
import java.util.concurrent.TimeUnit

@Preview
@Composable
fun CalendarMain(date: LocalDate = LocalDate.now()) {
    Column {
        NowTime()
        Divider()
        CalendarTitle(date)
        CalendarHeader()
        CalendarMonth()
    }
}

@Composable
fun NowTime() {
    Column {
        var date by remember {
            mutableStateOf(System.currentTimeMillis())
        }

        LaunchedEffect(this) {
            while (true) {
                date = System.currentTimeMillis()
                delay(1000)
            }
        }

        Text(text = hourMinuteSecond(date), fontSize = 32.sp)

        Text(text = yearMonthDay(date) + "，星期" + dayOfWeek(date))
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CalendarTitle(date: LocalDate) {
    Row {
        Text(text = monthYearFromDate(date))
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CalendarHeader() {
    LazyVerticalGrid(cells = GridCells.Fixed(7), content = {
        items(count = 7) {
            Text(text = dayOfWeek(it), textAlign = TextAlign.Center)
        }
    })
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CalendarMonth() {
    val nowYearMonth = currentLocaleDate

    val yearsAgo = nowYearMonth.minusYears(10)
    val nowDayOfMonth = nowYearMonth.dayOfMonth

    val diff = nowYearMonth.toInstant(ZoneOffset.ofHours(8)).toEpochMilli() - yearsAgo.toInstant(ZoneOffset.ofHours(8)).toEpochMilli()

    val days = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS).toInt()

    nowYearMonth.toLocalDate().lengthOfMonth()

    var currentMonthRange by remember {
        mutableStateOf(days - nowDayOfMonth + 2..days + nowYearMonth.toLocalDate().lengthOfMonth() - nowDayOfMonth + 1)
    }

    val state = rememberSaveable(saver = LazyGridState.Saver) {
        LazyGridState(days - nowDayOfMonth, 0)
    }

    val coroutineScope = rememberCoroutineScope()
    var job: Job? = null

    var firstItem = state.firstVisibleItemIndex

    LazyVerticalGrid(
        cells = GridCells.Fixed(7),
        state = state,
        modifier = Modifier.scrollable(
            orientation = Orientation.Vertical,
            state = rememberScrollableState { delta ->
                if (firstItem == state.firstVisibleItemIndex) return@rememberScrollableState delta
                firstItem = state.firstVisibleItemIndex

                job?.cancel()
                job = coroutineScope.launch {
                    currentMonthRange = getHighlightRange(state.firstVisibleItemIndex..state.firstVisibleItemIndex + state.layoutInfo.visibleItemsInfo.size)
                }

                delta
            }
        ),
        content = {
            items(count = days * 2) { day ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .aspectRatio(1F)
                        .padding(0.5.dp)
                        .background(if (day in currentMonthRange) Color.Magenta else Color.DarkGray),
                    contentAlignment = Alignment.Center
                ) {
                    Day(day)
                }
            }
        }
    )
}

@Composable
private fun Day(day: Int) {
    Text(
        text = getDay(day),
        color = Color.White,
        textAlign = TextAlign.Center,
        fontSize = 8.sp,
    )
}
