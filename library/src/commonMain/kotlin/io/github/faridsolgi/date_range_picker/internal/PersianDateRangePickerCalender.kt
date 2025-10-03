package io.github.faridsolgi.date_range_picker.internal

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.github.faridsolgi.date_range_picker.PersianDateRangePickerState
import io.github.faridsolgi.domain.model.PersianDatePickerColors
import io.github.faridsolgi.domain.model.PersianDatePickerTokens
import io.github.faridsolgi.persiandatetime.domain.PersianDateTime
import io.github.faridsolgi.persiandatetime.domain.PersianWeekday
import io.github.faridsolgi.persiandatetime.extensions.format
import io.github.faridsolgi.persiandatetime.extensions.minusDays
import io.github.faridsolgi.persiandatetime.extensions.monthLength
import io.github.faridsolgi.persiandatetime.extensions.nowPersianDate
import io.github.faridsolgi.persiandatetime.extensions.persianDayOfWeek
import io.github.faridsolgi.persiandatetime.extensions.plusDays
import io.github.faridsolgi.persiandatetime.extensions.toEpochMilliseconds
import io.github.faridsolgi.persiandatetime.extensions.toLocalDate
import io.github.faridsolgi.share.internal.ProvideContentColorTextStyle
import kotlinx.datetime.TimeZone
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

private const val DAYS_IN_WEEK = 7
private const val YEARS_IN_ROW = 3

@Composable
fun PersianDateRangePickerCalender(
    state: PersianDateRangePickerState,
    colors: PersianDatePickerColors,
    modifier: Modifier
) {
    val weekdays = remember {
        PersianWeekday.entries
            .asSequence()
            .filterNot { it == PersianWeekday.UNKNOWN }
            .sortedBy { it.number }
            .map { it.displayName.take(1) }
            .toList()
    }

    Column(modifier = modifier) {
        YearPicker(
            displayedDate = state.initDisplayedDate,
            state = state,
            colors = colors,
            onDateChange = { state.initDisplayedDate = it }
        )
        Spacer(Modifier.height(8.dp))
        MonthsVerticalScroll(
            weekdays = weekdays,
            state = state,
            colors = colors,
            onDayClick = { clickedDate ->
                val start = state.selectedStartDate
                val end = state.selectedEndDate

                when {
                    start == null -> {
                        state.setSelection(clickedDate, null)
                    }

                    end == null -> {
                        if (clickedDate.toEpochMilliseconds() >= start.toEpochMilliseconds()) {
                            state.setSelection(start, clickedDate)
                        } else {
                            state.setSelection(clickedDate, null)
                        }
                    }

                    else -> {
                        state.setSelection(clickedDate, null)
                    }
                }
            }
        )
    }
}

@Composable
private fun MonthsVerticalScroll(
    weekdays: List<String>,
    state: PersianDateRangePickerState,
    colors: PersianDatePickerColors,
    onDayClick: (PersianDateTime) -> Unit
) {
    val startYear = state.yearRange.first
    val endYear = state.yearRange.last

    // Generate list of all months in the year range
    val allMonths = remember(startYear, endYear) {
        buildList {
            for (year in startYear..endYear) {
                for (month in 1..12) {
                    add(PersianDateTime(year = year, month = month, day = 1))
                }
            }
        }
    }

    val listState = rememberLazyListState()

    // Calculate initial scroll position
    val initialIndex = remember(state.initDisplayedDate) {
        allMonths.indexOfFirst {
            it.year == state.initDisplayedDate.year &&
                    it.month == state.initDisplayedDate.month
        }.coerceAtLeast(0)
    }

    // Scroll to initial month on first composition
    LaunchedEffect(initialIndex) {
        listState.scrollToItem(initialIndex)
    }

    // Update initDisplayedDate when scrolling
    LaunchedEffect(listState) {
        var lastVisibleMonth: PersianDateTime? = null

        snapshotFlow { listState.firstVisibleItemIndex }
            .collect { index ->
                if (index in allMonths.indices) {
                    val month = allMonths[index]
                    if (lastVisibleMonth?.year != month.year ||
                        lastVisibleMonth?.month != month.month
                    ) {
                        lastVisibleMonth = month
                        state.initDisplayedDate = month
                    }
                }
            }
    }

    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxWidth(),
    ) {
        items(allMonths, key = { "${it.year}-${it.month}" }) { monthDate ->
            MonthSection(
                monthDate = monthDate,
                weekdays = weekdays,
                state = state,
                colors = colors,
                onDayClick = onDayClick
            )
        }
    }
}

@Composable
private fun MonthSection(
    monthDate: PersianDateTime,
    weekdays: List<String>,
    state: PersianDateRangePickerState,
    colors: PersianDatePickerColors,
    onDayClick: (PersianDateTime) -> Unit
) {
    val formattedMonth = remember(monthDate) {
        monthDate.format {
            monthName()
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        // Month header
        Text(
            text = formattedMonth,
            style = MaterialTheme.typography.titleMedium,
            color = colors.notSelectedDayColor,
            modifier = Modifier
                .padding(start = 16.dp)
                .padding(bottom = 12.dp)
                .semantics { contentDescription = "Month: $formattedMonth" }
        )

        MonthGrid(
            weekdays = weekdays,
            displayedDate = monthDate,
            state = state,
            colors = colors,
            onDayClick = onDayClick
        )
    }
}

@OptIn(ExperimentalTime::class)
@Composable
internal fun MonthGrid(
    weekdays: List<String>,
    displayedDate: PersianDateTime,
    state: PersianDateRangePickerState,
    colors: PersianDatePickerColors,
    onDayClick: (PersianDateTime) -> Unit,
) {
    val firstDayOfMonth = remember(displayedDate.year, displayedDate.month) {
        displayedDate.copy(day = 1)
    }

    val firstDayWeekday = remember(firstDayOfMonth) {
        firstDayOfMonth.persianDayOfWeek()
    }

    val monthDayCount = remember(displayedDate.year, displayedDate.month) {
        displayedDate.monthLength()
    }

    val emptyDaysBefore = remember(firstDayWeekday) {
        firstDayWeekday.number - 1
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(DAYS_IN_WEEK),
        modifier = Modifier
            .fillMaxWidth()
            .height(((weekdays.size + emptyDaysBefore + monthDayCount + DAYS_IN_WEEK - 1) / DAYS_IN_WEEK * 48).dp),
        verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterVertically),
        userScrollEnabled = false
    ) {
        items(weekdays) { weekday ->
            ProvideTextStyle(MaterialTheme.typography.labelMedium) {
                Text(
                    text = weekday,
                    modifier = Modifier.semantics {
                        contentDescription = "Weekday: $weekday"
                    },
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelMedium,
                    color = colors.weekdaysColor
                )
            }
        }

        items(emptyDaysBefore) {
            Spacer(Modifier.aspectRatio(1f))
        }

        items(monthDayCount) { dayIndex ->
            val dayNumber = dayIndex + 1
            val currentDate = remember(displayedDate, dayNumber) {
                displayedDate.copy(day = dayNumber)
            }

            val startDate = state.selectedStartDate
            val endDate = state.selectedEndDate

            val isStartDate =
                currentDate.toLocalDate().toEpochDays() == startDate?.toLocalDate()?.toEpochDays()
            val isEndDate =
                currentDate.toLocalDate().toEpochDays() == endDate?.toLocalDate()?.toEpochDays()
            val isBeforeEndDate =
                currentDate.toLocalDate().toEpochDays() == endDate?.minusDays(1)?.toLocalDate()
                    ?.toEpochDays()
            val isAfterStartDate =
                currentDate.toLocalDate().toEpochDays() == startDate?.plusDays(1)?.toLocalDate()
                    ?.toEpochDays()
            val isInRange = startDate != null && endDate != null &&
                    currentDate.toEpochMilliseconds() > startDate.toEpochMilliseconds()
                    && currentDate.toEpochMilliseconds() < endDate.toEpochMilliseconds()
            val isSelected = isStartDate || isEndDate

            MonthDayItem(
                date = currentDate,
                isStartDate = isStartDate,
                isEndDate = isEndDate,
                isInRange = isInRange,
                isSelected = isSelected,
                isToday = currentDate == Clock.System.nowPersianDate(TimeZone.currentSystemDefault()),
                colors = colors,
                onDayClick = onDayClick,
                isAfterStartDate = isAfterStartDate,
                isBeforeEndDate = isBeforeEndDate
            )
        }
    }
}

@Composable
private fun MonthDayItem(
    date: PersianDateTime,
    isStartDate: Boolean,
    isEndDate: Boolean,
    isInRange: Boolean,
    isSelected: Boolean,
    isToday: Boolean,
    colors: PersianDatePickerColors,
    onDayClick: (PersianDateTime) -> Unit,
    isAfterStartDate: Boolean,
    isBeforeEndDate: Boolean
) {
    val backgroundBrush = when {
        isSelected -> SolidColor(colors.selectedDayColor)

        isBeforeEndDate -> Brush.horizontalGradient(
            listOf(
                colors.selectedDayColor,
                colors.selectedDayColor.copy(alpha = 0.3f),

            )
            )

        isAfterStartDate -> Brush.horizontalGradient(
            listOf(
                colors.selectedDayColor.copy(alpha = 0.3f),
                colors.selectedDayColor
            )
            )
        isInRange -> SolidColor(colors.selectedDayColor.copy(alpha = 0.3f))
        else -> SolidColor(Color.Transparent)
    }

    val borderColor by animateColorAsState(
        targetValue = if (isToday) colors.selectedDayColor else Color.Unspecified,
        label = "dayBorderColor"
    )

    val textColor by animateColorAsState(
        targetValue = if (isSelected) colors.onSelectedDayColor else colors.notSelectedDayColor,
        label = "dayTextColor"
    )

    val shape = when {
        isStartDate && isEndDate -> CircleShape
        isStartDate -> RoundedCornerShape(topStart = 50.dp, bottomStart = 50.dp)
        isEndDate -> RoundedCornerShape(topEnd = 50.dp, bottomEnd = 50.dp)
        isBeforeEndDate -> RectangleShape
        isAfterStartDate -> RectangleShape
        isInRange -> RoundedCornerShape(0.dp)
        else -> CircleShape
    }
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .then(
                if (isInRange) {
                    Modifier.background(backgroundBrush, shape)
                } else {
                    Modifier
                }
            )
    ) {
        Box(
            modifier = Modifier
                .aspectRatio(1f)
                .clip(shape)
                .background(if (isSelected) backgroundBrush else SolidColor(Color.Unspecified))
                .border(
                    width = PersianDatePickerTokens.todayDateBorderWidth,
                    color = borderColor,
                    shape = CircleShape
                )
                .clickable { onDayClick(date) }
                .semantics {
                    contentDescription = buildString {
                        append("Day ${date.day}")
                        if (isToday) append(", Today")
                        if (isStartDate) append(", Start date")
                        if (isEndDate) append(", End date")
                        if (isInRange) append(", In selected range")
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            ProvideContentColorTextStyle(
                textStyle = MaterialTheme.typography.bodyMedium,
                contentColor = textColor
            ) {
                Text(
                    text = date.day.toString(),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@OptIn(ExperimentalTime::class)
@Composable
fun YearPicker(
    displayedDate: PersianDateTime,
    state: PersianDateRangePickerState,
    colors: PersianDatePickerColors,
    onDateChange: (PersianDateTime) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    val gridState = rememberLazyGridState()

    val formattedDate = remember(displayedDate) {
        displayedDate.format {
            year()
        }
    }

    LaunchedEffect(expanded, displayedDate.year) {
        if (expanded) {
            val yearsList = state.yearRange.toList()
            val selectedYearIndex = yearsList.indexOf(displayedDate.year)
            if (selectedYearIndex >= 0) {
                gridState.animateScrollToItem(selectedYearIndex)
            }
        }
    }

    ProvideTextStyle(PersianDatePickerTokens.SelectionYearLabelTextFont) {
        Column {
            Row(
                modifier = Modifier
                    .clickable { expanded = !expanded }
                    .padding(start = 16.dp)
                    .semantics {
                        contentDescription = "Select year and month: $formattedDate"
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = formattedDate)
                Spacer(Modifier.width(8.dp))
                Icon(
                    imageVector = if (expanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                    contentDescription = if (expanded) "Collapse" else "Expand"
                )
            }

            AnimatedVisibility(visible = expanded) {
                Card(
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .height(PersianDatePickerTokens.ContainerHeight - 16.dp),
                    elevation = CardDefaults.cardElevation(0.dp),
                    colors = CardDefaults.cardColors(containerColor = colors.containerColor)
                ) {
                    Column {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(YEARS_IN_ROW),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            state = gridState
                        ) {
                            items(state.yearRange.toList()) { year ->
                                val isSelectedYear = year == displayedDate.year
                                val isCurrentYear =
                                    Clock.System.nowPersianDate(TimeZone.currentSystemDefault()).year == year
                                val backgroundColor by animateColorAsState(
                                    targetValue = if (isSelectedYear) {
                                        colors.selectedDayColor
                                    } else {
                                        colors.containerColor
                                    }
                                )
                                val textColor by animateColorAsState(
                                    targetValue = if (isSelectedYear) {
                                        colors.onSelectedDayColor
                                    } else {
                                        colors.notSelectedDayColor
                                    }
                                )

                                val borderColor by animateColorAsState(
                                    targetValue = if (isCurrentYear) {
                                        colors.selectedDayColor
                                    } else {
                                        colors.containerColor
                                    }
                                )
                                Box(
                                    modifier = Modifier
                                        .background(
                                            backgroundColor,
                                            MaterialTheme.shapes.extraLarge
                                        )
                                        .border(
                                            width = 1.dp,
                                            borderColor,
                                            MaterialTheme.shapes.extraLarge
                                        )
                                        .clickable {
                                            onDateChange(displayedDate.copy(year = year))
                                            expanded = false
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    ProvideContentColorTextStyle(
                                        textStyle = MaterialTheme.typography.bodyLarge,
                                        contentColor = textColor
                                    ) {
                                        Text(
                                            year.toString(),
                                            modifier = Modifier.semantics {
                                                contentDescription = "Year $year"
                                            }
                                        )
                                    }
                                }
                            }
                        }
                        Spacer(Modifier.padding(top = 8.dp))
                        HorizontalDivider()
                    }
                }
            }
        }
    }
}