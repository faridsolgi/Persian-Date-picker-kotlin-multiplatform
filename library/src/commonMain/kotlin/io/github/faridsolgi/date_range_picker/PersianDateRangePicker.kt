package io.github.faridsolgi.date_range_picker

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import io.github.faridsolgi.date_picker.view.PersianDateEnterSection
import io.github.faridsolgi.date_picker.view.PersianDatePickerDefaults
import io.github.faridsolgi.date_picker.view.internal.PersianDatePickerCalendar
import io.github.faridsolgi.date_range_picker.internal.PersianDateRangePickerCalender
import io.github.faridsolgi.domain.model.DisplayMode
import io.github.faridsolgi.domain.model.PersianDatePickerColors
import io.github.faridsolgi.domain.model.PersianDatePickerTokens
import io.github.faridsolgi.share.PersianDatePickerDialog
import io.github.faridsolgi.share.internal.DisplayModeToggleButton
import io.github.faridsolgi.share.internal.ProvideContentColorTextStyle
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
 fun PersianDateRangePicker(
    state: PersianDateRangePickerState,
    modifier: Modifier = Modifier,
    colors: PersianDatePickerColors = PersianDatePickerDefaults.colors(),
    title: (@Composable () -> Unit)? = {
        PersianDateRangePickerDefaults.DateRangePickerTitle(
            displayMode = state.displayMode,
            modifier = Modifier.padding(DatePickerTitlePadding)
        )
    },
    headline: (@Composable () -> Unit)? = {
        PersianDateRangePickerDefaults.DateRangePickerHeadline(
            selectedStartDate = state.selectedStartDate,
            selectedEndDate = state.selectedEndDate,
            displayMode = state.displayMode,
            modifier = Modifier.padding(DatePickerHeadlinePadding)
        )
    },
    showModeToggle: Boolean = true,
    focusRequester: FocusRequester? = remember { FocusRequester() },
) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Column(modifier) {
            PersianDatePickerHeadLine(
                state,
                title, headline,
                showModeToggle,
                colors
            )
            HorizontalDivider()
            SwitchablePersianDateRangePickerContents(state,colors, Modifier.padding(16.dp))
        }
    }

}
@Composable
private fun SwitchablePersianDateRangePickerContents(
    state: PersianDateRangePickerState,
    colors: PersianDatePickerColors,
    modifier: Modifier = Modifier,
) {

    AnimatedContent(
        targetState = state.displayMode ,
        transitionSpec = {
            slideInHorizontally(animationSpec = tween(500)) { height -> height } + fadeIn() togetherWith slideOutHorizontally(
                animationSpec = tween(500)
            ) { height -> -height } + fadeOut()
        },
        label = "display mode transition"
    ) { displayMode ->
        Column(modifier) {
            when (displayMode) {
                DisplayMode.Companion.Picker -> {
                    PersianDateRangePickerCalender(state, colors, Modifier)
                }

                DisplayMode.Companion.Input -> {
                  //  PersianDateEnterSection(state, colors)
                }
            }
        }
    }
}


@Composable
internal fun PersianDatePickerHeadLine(
    state: PersianDateRangePickerState,
    title: (@Composable () -> Unit)?,
    headline: (@Composable () -> Unit)?,
    showModeToggle: Boolean,
    colors: PersianDatePickerColors,
) {
    Column(
        modifier = Modifier
            .sizeIn(minWidth = PersianDatePickerTokens.ContainerWidth),
    ) {
        ProvideContentColorTextStyle(
            colors.titleColor,
            PersianDatePickerTokens.titleTextStyle
        ) {
            title?.invoke()
        }
        Spacer(Modifier.padding(vertical = 8.dp))
        Row(
            Modifier.fillMaxWidth().padding(start = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ProvideContentColorTextStyle(
                colors.headerColor,
                PersianDatePickerTokens.HeadlineRangeTextStyle
            ) {

                headline?.invoke()
            }

            if (showModeToggle) {
                Spacer(Modifier.weight(1f))
                DisplayModeToggleButton(
                    Modifier.padding(end = 16.dp),
                    displayMode = state.displayMode,
                    onDisplayModeChange = { state.displayMode = it }
                )
            }
        }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
private fun PersianDatePickerPreview() {
    MaterialTheme {
        val state = rememberPersianDateRangePickerState(yearRange = 1400..1500)

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
            PersianDateRangePicker(
                state = state
            )
        }

    }
}
private val DatePickerTitlePadding = PaddingValues(start = 24.dp, end = 12.dp, top = 16.dp)
private val DatePickerHeadlinePadding = PaddingValues(start = 24.dp, end = 12.dp, bottom = 12.dp)

