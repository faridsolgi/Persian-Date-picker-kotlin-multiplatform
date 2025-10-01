package io.github.faridsolgi.view.internal

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.outlined.ChevronLeft
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.github.faridsolgi.domain.model.PersianDatePickerColors
import io.github.faridsolgi.domain.model.PersianDatePickerTokens
import io.github.faridsolgi.persiandatetime.converter.format
import io.github.faridsolgi.persiandatetime.converter.monthLength
import io.github.faridsolgi.persiandatetime.converter.nowPersianDate
import io.github.faridsolgi.persiandatetime.converter.persianDayOfWeek
import io.github.faridsolgi.persiandatetime.domain.PersianDateTime
import io.github.faridsolgi.persiandatetime.domain.PersianWeekday
import io.github.faridsolgi.util.canNavigateToNextMonth
import io.github.faridsolgi.util.canNavigateToPreviousMonth
import io.github.faridsolgi.util.navigateToNextMonth
import io.github.faridsolgi.util.navigateToPreviousMonth
import io.github.faridsolgi.view.PersianDatePickerState
import kotlinx.datetime.TimeZone
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

private const val DAYS_IN_WEEK = 7
private const val YEARS_IN_ROW = 3
private const val MAX_CALENDAR_ROWS = 6

@OptIn(ExperimentalTime::class, ExperimentalAnimationApi::class)
@Composable
internal fun PersianDatePickerCalendar(
    state: PersianDatePickerState,
    colors: PersianDatePickerColors,
    modifier: Modifier = Modifier,
) {
    val displayedDate = state.initDisplayedDate

    val weekdays = remember {
        PersianWeekday.entries
            .asSequence()
            .filterNot { it == PersianWeekday.UNKNOWN }
            .sortedBy { it.number }
            .map { it.displayName.take(1) }
            .toList()
    }

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

    Column(modifier = modifier) {
        NavigationMonthAndYearSelection(
            displayedDate = displayedDate,
            state = state,
            colors = colors,
            navigateToPreviousMonth = { state.navigateToPreviousMonth() },
            navigateToNextMonth = { state.navigateToNextMonth() },
        )
        MonthGrid(
            weekdays = weekdays,
            emptyDaysBefore = emptyDaysBefore,
            daysInMonth = monthDayCount,
            displayedDate = displayedDate,
            state = state,
            colors = colors,
            onDayClick = { state.selectedDate = it }
        )
    }
}

@OptIn(ExperimentalTime::class)
@Composable
internal fun MonthGrid(
    weekdays: List<String>,
    emptyDaysBefore: Int,
    daysInMonth: Int,
    displayedDate: PersianDateTime,
    state: PersianDatePickerState,
    colors: PersianDatePickerColors,
    onDayClick: (PersianDateTime) -> Unit,
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(DAYS_IN_WEEK),
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterVertically),
        horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally)
    ) {
        // Weekday headers
        items(weekdays) { weekday ->
            ProvideTextStyle(MaterialTheme.typography.labelMedium){
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

        // Empty cells before the first day
        items(emptyDaysBefore) {
            Spacer(Modifier.aspectRatio(1f))
        }

        // Days of the month
        items(daysInMonth) { dayIndex ->
            val dayNumber = dayIndex + 1
            val currentDate = remember(displayedDate, dayNumber) {
                displayedDate.copy(day = dayNumber)
            }

            MonthDayItem(
                date = currentDate,
                isSelected = state.selectedDate == currentDate,
                isToday = currentDate == Clock.System.nowPersianDate(TimeZone.currentSystemDefault()),
                colors = colors,
                onDayClick = onDayClick
            )
        }
    }
}

@Composable
private fun MonthDayItem(
    date: PersianDateTime,
    isSelected: Boolean,
    isToday: Boolean,
    colors: PersianDatePickerColors,
    onDayClick: (PersianDateTime) -> Unit,
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) colors.selectedDayColor else Color.Unspecified,
        label = "dayBackgroundColor"
    )

    val borderColor by animateColorAsState(
        targetValue = if (isToday) colors.selectedDayColor else Color.Unspecified,
        label = "dayBorderColor"
    )

    val textColor by animateColorAsState(
        targetValue = if (isSelected) colors.onSelectedDayColor else colors.notSelectedDayColor,
        label = "dayTextColor"
    )
    AnimatedContent(
        targetState = date.day,
        transitionSpec = {
            slideInHorizontally(animationSpec = tween(500)) { height -> height } + fadeIn() togetherWith
                    slideOutHorizontally(animationSpec = tween(500)) { height -> -height } + fadeOut()
        }
    ) {

    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .background(backgroundColor, CircleShape)
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
                    if (isSelected) append(", Selected")
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

@Composable
internal fun NavigationMonthAndYearSelection(
    displayedDate: PersianDateTime,
    state: PersianDatePickerState,
    colors: PersianDatePickerColors,
    navigateToPreviousMonth: () -> Unit,
    navigateToNextMonth: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        YearPicker(
            displayedDate = displayedDate,
            state = state,
            colors = colors,
            onYearSelect = { state.initDisplayedDate = it }
        )

        Spacer(Modifier.weight(1f))

        Row {
            IconButton(
                onClick = {
                    state.canNavigateToNextMonth {
                        navigateToNextMonth()
                    }
                },
                modifier = Modifier.semantics {
                    contentDescription = "Next month"
                }
            ) {
                Icon(Icons.Outlined.ChevronRight, contentDescription = null)
            }

            IconButton(
                onClick = {
                    state.canNavigateToPreviousMonth {
                        navigateToPreviousMonth()
                    }
                },
                modifier = Modifier.semantics {
                    contentDescription = "Previous month"
                }
            ) {
                Icon(Icons.Outlined.ChevronLeft, contentDescription = null)
            }
        }
    }
}

@OptIn(ExperimentalTime::class)
@Composable
fun YearPicker(
    displayedDate: PersianDateTime,
    state: PersianDatePickerState,
    colors: PersianDatePickerColors,
    onYearSelect: (PersianDateTime) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    val gridState = rememberLazyGridState()

    val formattedDate = remember(displayedDate) {
        displayedDate.format {
            monthName()
            char(' ')
            year()
        }
    }

    // Scroll to selected year when expanded
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
            if (expanded) {
                Spacer(Modifier.height(16.dp))
            }

            // Clickable header
            Row(
                modifier = Modifier
                    .clickable { expanded = !expanded }
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

            // Year dropdown
            AnimatedVisibility(visible = expanded) {
                Card(
                    modifier = Modifier.padding(top = 8.dp)
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
                                        .border(width = 1.dp, borderColor, MaterialTheme.shapes.extraLarge)
                                        .clickable {
                                            onYearSelect(displayedDate.copy(year = year))
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