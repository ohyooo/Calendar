package com.ohyooo.calendar.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime

@Preview
@Composable
fun CalendarMain(date: LocalDate = LocalDate.now()) {
    val currentMonth = remember {
        mutableStateOf(LocalDateTime.now())
    }

    Column(modifier = Modifier.background(mainBgColor)) {
        Column(modifier = Modifier.padding(start = 12.dp, top = 16.dp, end = 12.dp, bottom = 16.dp)) {
            NowTime()
        }

        Divider(color = Color.Gray)

        CalendarTitle(currentMonth.value)

        CalendarHeader()
        CalendarMonth(currentMonth)
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

        Text(text = hourMinuteSecond(date), fontSize = 32.sp, color = clockColor)

        Text(text = yearMonthDay(date) + "，星期" + dayOfWeek(date), fontSize = 14.sp, color = dateColor)
    }
}

@Composable
fun CalendarTitle(date: LocalDateTime) {
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

        ScrollIcon(Icons.Outlined.ExpandMore, size.dp, "UP")
        ScrollIcon(Icons.Outlined.ExpandLess, size.dp, "DOWN")
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CalendarHeader() {
    LazyVerticalGrid(cells = GridCells.Fixed(7), content = {
        items(count = 7) {
            Text(text = dayOfWeek(it), textAlign = TextAlign.Center, color = dayOfWeekColor)
        }
    })
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CalendarMonth(currentMonth: MutableState<LocalDateTime>) {
    val nowYearMonth = currentLocaleDate

    val nowDayOfMonth = nowYearMonth.dayOfMonth

    val days = prevDaySize.toInt()

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
                    currentMonth.value = getMonthDay(currentMonthRange.first)
                }

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
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(4.dp)
                    ) {
                        val modifier = Modifier.fillMaxSize()
                        Box(
                            modifier = if (days == day - 1) modifier.border(2.dp, color = mainBgColor) else modifier,
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = getDay(day),
                                color = if (day in currentMonthRange) dayOfHighlightedColor else dayOfNormalColor,
                                textAlign = TextAlign.Center,
                            )
                        }
                    }
                }
            }
        }
    )
}

@Composable
fun ScrollIcon(imageVector: ImageVector, size: Dp, contentDescription: String) {
    Box(
        modifier = Modifier
            .width(size)
            .height(size),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = imageVector,
            modifier = Modifier.size(32.dp), contentDescription = contentDescription, tint = monthTitleColor
        )
    }
}
