package io.github.faridsolgi.view


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import io.github.faridsolgi.domain.model.DisplayMode
import io.github.faridsolgi.domain.model.PersianDatePickerColors
import io.github.faridsolgi.domain.model.PersianDatePickerTokens
import io.github.faridsolgi.persiandatetime.converter.toDateString
import io.github.faridsolgi.view.internal.DisplayModeToggleButton
import io.github.faridsolgi.view.internal.PersianDatePickerCalender
import io.github.faridsolgi.view.internal.PersianDatePickerState
import io.github.faridsolgi.view.internal.ProvideContentColorTextStyle
import io.github.faridsolgi.view.internal.rememberPersianDatePickerState
import org.jetbrains.compose.ui.tooling.preview.Preview

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


@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
private fun PersianDatePickerPreview() {
    MaterialTheme {
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
                    Modifier.padding(end = 16.dp),
                    displayMode = state.displayMode,
                    onDisplayModeChange = { state.displayMode = it }
                )
            }
        }
    }
}


private val DatePickerTitlePadding = PaddingValues(start = 24.dp, end = 12.dp, top = 16.dp)
private val DatePickerHeadlinePadding = PaddingValues(start = 24.dp, end = 12.dp, bottom = 12.dp)

