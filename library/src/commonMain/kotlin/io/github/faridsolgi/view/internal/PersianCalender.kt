package io.github.faridsolgi.view.internal

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateColorAsState
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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.outlined.ChevronLeft
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import kotlinx.datetime.TimeZone
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class, ExperimentalAnimationApi::class)
@Composable
internal fun PersianDatePickerCalender(
    state: PersianDatePickerState,
    colors: PersianDatePickerColors,
) {
    val displayedDate by remember(state.initDisplayedDate) {
        mutableStateOf(state.initDisplayedDate)
    }

    val weekdays = PersianWeekday.entries
        .sortedBy { it.number }
        .filterNot { it == PersianWeekday.UNKNOWN }
        .map { it.displayName.take(1) }


    val firstDayOfMonth = displayedDate.copy(day = 1)
    val firstDayWeekday = firstDayOfMonth.persianDayOfWeek()


    val monthDayCount = displayedDate.monthLength()


    val emptyDaysBefore = firstDayWeekday.number - 1
    NavigationMonthAndYearSelection(
        displayedDate,
        state,
        navigateToPreviousMonth = { state.navigateToPreviousMonth() },
        navigateToNextMonth = { state.navigateToNextMonth() },
    )
    Spacer(Modifier.padding(vertical = 4.dp))
    MonthGrid(
        weekdays,
        emptyDaysBefore, monthDayCount,
        displayedDate, state, colors,
        onItemClick = {
            state.selectedDate = it
        })
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
    onItemClick: (item: PersianDateTime) -> Unit,
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(7),
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterVertically),
        horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally)
    ) {

        // day weeks name
        items(weekdays) {
            Text(
                text = it,
                modifier = Modifier,
                textAlign = TextAlign.Center
            )
        }

        // empty cell before the first day of the month
        items(emptyDaysBefore) {
            Text("")
        }

        // days of the month
        items(daysInMonth) { dayIndex ->
            AnimatedContent(
                targetState = dayIndex,
                transitionSpec = {
                    slideInHorizontally { width -> width } + fadeIn() togetherWith
                            slideOutHorizontally { width -> -width } + fadeOut()
                }
            ) {
                MonthDayItem(dayIndex, state, displayedDate, colors, onItemClick)

            }
        }
    }
}

@OptIn(ExperimentalTime::class)
@Composable
private fun MonthDayItem(
    dayIndex: Int,
    state: PersianDatePickerState,
    displayedDate: PersianDateTime,
    colors: PersianDatePickerColors,
    onItemClick: (PersianDateTime) -> Unit,
) {
    val dayNumber = dayIndex + 1
    val isSelectedDay = state.selectedDate == displayedDate.copy(day = dayNumber)
    val isToday =
        displayedDate.copy(day = dayNumber) == Clock.System.nowPersianDate(TimeZone.currentSystemDefault())
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelectedDay) {
            colors.selectedDayColor
        } else {
            colors.containerColor
        }
    )
    val borderColor by animateColorAsState(
        targetValue = if (isToday) {
            colors.selectedDayColor
        } else {
            colors.containerColor
        }
    )
    Box(
        Modifier
            .aspectRatio(1f)
            .background(backgroundColor, shape = CircleShape)
            .border(
                width = PersianDatePickerTokens.todayDateBorderWidth,
                borderColor,
                CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        val dateTextColor by animateColorAsState(
            targetValue = if (isSelectedDay) {
                colors.onSelectedDayColor
            } else {
                colors.notSelectedDayColor
            }
        )
        ProvideContentColorTextStyle(
            textStyle = MaterialTheme.typography.labelLarge,
            contentColor = dateTextColor
        ) {
            Text(
                text = dayNumber.toString(),
                modifier = Modifier
                    .clickable {
                        onItemClick(displayedDate.copy(day = dayNumber))
                    },
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
internal fun NavigationMonthAndYearSelection(
    displayedDate: PersianDateTime,
    state: PersianDatePickerState,
    navigateToPreviousMonth: () -> Unit,
    navigateToNextMonth: () -> Unit,
) {
    Row(
        Modifier.fillMaxWidth().padding(start = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        YearSelector(displayedDate, state) {
            state.initDisplayedDate = it
        }
        Spacer(Modifier.weight(1f))
        Row {
            //NavigateToNextMonth
            IconButton(onClick = {
                state.canNavigateToNextMonth {
                    navigateToNextMonth()
                }
            }) {
                Icon(Icons.Outlined.ChevronRight, contentDescription = null)
            }
            //NavigateToPreviousMonth
            IconButton(onClick = {
                state.canNavigateToPreviousMonth {
                    navigateToPreviousMonth()
                }

            }) {
                Icon(Icons.Outlined.ChevronLeft, contentDescription = null)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun YearSelector(
    displayedDate: PersianDateTime,
    state: PersianDatePickerState,
    onYearSelect: (PersianDateTime) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        Row(
            modifier = Modifier
                .clickable { expanded = !expanded }
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = displayedDate.format {
                monthName()
                char(' ')
                year()
            })
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = "Dropdown"
            )
        }

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            // Display years in a grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(3), // 3 columns
                modifier = Modifier
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(state.yearRange.toList()) { item ->
                    Text(
                        text = item.toString(),
                        modifier = Modifier
                            .clickable {
                                onYearSelect(displayedDate.copy(item))
                                expanded = false
                            }
                            .padding(8.dp)
                    )
                }
            }
        }
    }
}

