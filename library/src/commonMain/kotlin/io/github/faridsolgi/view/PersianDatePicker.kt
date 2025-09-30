package io.github.faridsolgi.view


import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.material.icons.outlined.ChevronLeft
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import io.github.faridsolgi.domain.model.DisplayMode
import io.github.faridsolgi.domain.model.PersianDatePickerColors
import io.github.faridsolgi.domain.model.PersianDatePickerTokens
import io.github.faridsolgi.persiandatetime.converter.format
import io.github.faridsolgi.persiandatetime.converter.monthLength
import io.github.faridsolgi.persiandatetime.converter.nowPersianDate
import io.github.faridsolgi.persiandatetime.converter.persianDayOfWeek
import io.github.faridsolgi.persiandatetime.converter.toDateString
import io.github.faridsolgi.persiandatetime.domain.PersianDateTime
import io.github.faridsolgi.persiandatetime.domain.PersianWeekday
import kotlinx.datetime.TimeZone
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@Composable
fun PersianDatePicker(
    state: PersianDatePickerState,
    modifier: Modifier = Modifier,
    title: (@Composable () -> Unit)? = {
        PersianDatePickerDefaults.DatePickerTitle(
            displayMode = state.displayMode,
            modifier = Modifier.padding(DatePickerTitlePadding)
        )
    },
    headline: (@Composable () -> Unit)? = {
        PersianDatePickerDefaults.DatePickerHeadline(
            selectedDate = state.selectedDate,
            displayMode = state.displayMode,
            modifier = Modifier.padding(DatePickerHeadlinePadding)

        )
    },
    showModeToggle: Boolean = true,
    colors: PersianDatePickerColors = PersianDatePickerDefaults.colors(),
) {
    // DatePicker()
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Column(modifier) {
            PersianDatePickerHeadLine(
                state,
                title,
                headline,
                showModeToggle,
                colors
            )
            HorizontalDivider()
            SwitchablePersianDatePickerContents(
                state, colors,
                Modifier.padding(horizontal = 16.dp)
            )

        }
    }
}

@Composable
fun SwitchablePersianDatePickerContents(
    state: PersianDatePickerState,
    colors: PersianDatePickerColors,
    modifier: Modifier = Modifier,
) {
    Column(modifier) {
        when (state.displayMode) {
            DisplayMode.Companion.Picker -> {
                PersianDatePickerCalender(state, colors)
            }

            DisplayMode.Companion.Input -> {

            }

            else -> ""
        }

    }
}

@OptIn(ExperimentalTime::class)
@Composable
fun PersianDatePickerCalender(
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

    // روز هفته اول ماه رو بدست بیار
    val firstDayOfMonth = displayedDate.copy(day = 1)
    val firstDayWeekday = firstDayOfMonth.persianDayOfWeek()

    // تعداد روزهای ماه
    val daysInMonth = displayedDate.monthLength()

    // تعداد خانه‌های خالی قبل از شروع ماه
    val emptyDaysBefore = firstDayWeekday.number - 1
    NavigationMonthAndYearSelection(state)
    Spacer(Modifier.padding(vertical = 4.dp))
    LazyVerticalGrid(
        columns = GridCells.Fixed(7),
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterVertically),
        horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally)
    ) {

        // نام روزهای هفته
        items(weekdays) {
            Text(
                text = it,
                modifier = Modifier,
                textAlign = TextAlign.Center
            )
        }

        // خانه‌های خالی قبل از شروع ماه
        items(emptyDaysBefore) {
            Text("")
        }

        // روزهای ماه
        items(daysInMonth) { dayIndex ->
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
                Modifier.aspectRatio(1f).background(backgroundColor, shape = CircleShape)
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
                                state.selectedDate = displayedDate.copy(day = dayNumber)
                            },
                        textAlign = TextAlign.Center
                    )
                }

            }
        }
    }
}

@Composable
fun NavigationMonthAndYearSelection(state: PersianDatePickerState) {
    val date = state.initDisplayedDate
    Row(
        Modifier.fillMaxWidth().padding(start = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            date.format { monthName(); char(' '); year() },
            style = MaterialTheme.typography.labelLarge
        )
        IconButton(onClick = {
            navigateToPreviousMonth(state)
        }) {
            Icon(Icons.Outlined.ArrowDropDown, contentDescription = null)
        }
        Spacer(Modifier.weight(1f))
        Row {
            IconButton(onClick = {
                if (canNavigateToNextMonth(state)) {
                    navigateToNextMonth(state)
                }

            }) {
                Icon(Icons.Outlined.ChevronRight, contentDescription = null)
            }
            IconButton(onClick = {
                if (canNavigateToPreviousMonth(state)) {
                    navigateToPreviousMonth(state)
                }

            }) {
                Icon(Icons.Outlined.ChevronLeft, contentDescription = null)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
private fun PersianDatePickerPreview() {
    MaterialTheme(typography = PersianDatePickerDefaults.defaultTypography()) {
        val state = rememberPersianDatePickerState()

        PersianDatePickerDialog(
            dismissButton = {
                TextButton({}) {
                    Text("لغو")
                }
            },
            confirmButton = {
                TextButton({}) {
                    Text("تایید")
                }
            },
            onDismissRequest = {

            },
        ) {
            PersianDatePicker(
                state = state
            )
        }
        Spacer(Modifier.padding(16.dp))
        Text(state.selectedDate?.toDateString() ?: "")


    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview(widthDp = 288, heightDp = 498, showBackground = true)
private fun SwitchablePersianDatePickerContentsPreview() {
    MaterialTheme {
        SwitchablePersianDatePickerContents(
            rememberPersianDatePickerState(),
            colors = PersianDatePickerDefaults.colors().copy(
                containerColor = Color.Red,
                titleColor = Color.Red,
                confirmButtonColor = Color.Red,
                dismissButtonColor = Color.Red
            )
        )
    }
}

@Composable
internal fun PersianDatePickerHeadLine(
    state: PersianDatePickerState,
    title: (@Composable () -> Unit)?,
    headline: (@Composable () -> Unit)?,
    showModeToggle: Boolean,
    colors: PersianDatePickerColors,
) {
    Column(
        modifier = Modifier
            .sizeIn(minWidth = PersianDatePickerTokens.ContainerWidth)
            .background(colors.containerColor)
    ) {
        ProvideContentColorTextStyle(
            colors.titleColor,
            PersianDatePickerTokens.titleTextStyle
        ) {
            title?.invoke()
        }
        Spacer(Modifier.padding(vertical = 16.dp))
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ProvideContentColorTextStyle(
                colors.headerColor,
                PersianDatePickerTokens.HeadlineTextStyle
            ) {
                headline?.invoke()
            }

            if (showModeToggle) {
                DisplayModeToggleButton(
                    Modifier,
                    displayMode = state.displayMode,
                    onDisplayModeChange = { state.displayMode = it }
                )
            }
        }
    }
}

private val DatePickerTitlePadding = PaddingValues(start = 24.dp, end = 12.dp, top = 16.dp)
private val DatePickerHeadlinePadding = PaddingValues(start = 24.dp, end = 12.dp, bottom = 12.dp)

@OptIn(ExperimentalMaterial3Api::class)
private fun navigateToPreviousMonth(state: PersianDatePickerState) {
    val current = state.initDisplayedDate
    val newDate = if (current.month == 1) {
        // Go to previous year, month 12
        val newYear = current.year - 1
        if (newYear >= state.yearRange.first) {
            PersianDateTime(newYear, 12, 1, 0, 0, 0)
        } else {
            return // Can't go back further
        }
    } else {
        // Go to previous month
        PersianDateTime(current.year, current.month - 1, 1, 0, 0, 0)
    }
    state.initDisplayedDate = newDate
}

@OptIn(ExperimentalMaterial3Api::class)
private fun navigateToNextMonth(state: PersianDatePickerState) {
    val current = state.initDisplayedDate
    val newDate = if (current.month == 12) {
        // Go to next year, month 1
        val newYear = current.year + 1
        if (newYear <= state.yearRange.last) {
            PersianDateTime(newYear, 1, 1, 0, 0, 0)
        } else {
            return // Can't go forward further
        }
    } else {
        // Go to next month
        PersianDateTime(current.year, current.month + 1, 1, 0, 0, 0)
    }
    state.initDisplayedDate = newDate
}

@OptIn(ExperimentalMaterial3Api::class)
private fun canNavigateToPreviousMonth(state: PersianDatePickerState): Boolean {
    val current = state.initDisplayedDate
    return if (current.month == 1) {
        current.year > state.yearRange.first
    } else {
        true
    }
}

@OptIn(ExperimentalMaterial3Api::class)
private fun canNavigateToNextMonth(state: PersianDatePickerState): Boolean {
    val current = state.initDisplayedDate
    return if (current.month == 12) {
        current.year < state.yearRange.last
    } else {
        true
    }
}