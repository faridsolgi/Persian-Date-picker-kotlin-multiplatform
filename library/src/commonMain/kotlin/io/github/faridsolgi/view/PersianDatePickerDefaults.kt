package io.github.faridsolgi.view

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import io.github.faridsolgi.domain.SelectableDates
import io.github.faridsolgi.domain.model.DisplayMode
import io.github.faridsolgi.domain.model.PersianDatePickerColors
import io.github.faridsolgi.library.generated.resources.DateInputHeadline
import io.github.faridsolgi.library.generated.resources.DatePickerHeadline
import io.github.faridsolgi.library.generated.resources.Res
import io.github.faridsolgi.library.generated.resources.dateInputTitle
import io.github.faridsolgi.library.generated.resources.datePickerTitle
import io.github.faridsolgi.persiandatetime.converter.format
import io.github.faridsolgi.persiandatetime.domain.PersianDateTime
import org.jetbrains.compose.resources.stringResource
import kotlin.time.ExperimentalTime

val LocalPersianDatePickerTypography = staticCompositionLocalOf {
    Typography() // Fallback default
}
object PersianDatePickerDefaults {
    val YearRange = IntRange(1300, 1499)

    val AllDatesSelectable = object : SelectableDates {}

    @Composable
    fun colors(): PersianDatePickerColors = MaterialTheme.colorScheme.DefaultPersianDatePickerColors

    @Composable
    fun colors(
        containerColor: Color = Color.Unspecified,
        titleColor: Color = Color.Unspecified,
        confirmButtonColor: Color = Color.Unspecified,
        dismissButtonColor: Color = Color.Unspecified,
        selectedDayColor: Color = Color.Unspecified,
        onSelectedDayColor: Color = Color.Unspecified,
        notSelectedDayColor: Color = Color.Unspecified,
        todayColor: Color = Color.Unspecified,

    ): PersianDatePickerColors = MaterialTheme.colorScheme.DefaultPersianDatePickerColors.copy(
        containerColor = containerColor,
        titleColor = titleColor,
        confirmButtonColor = confirmButtonColor,
        dismissButtonColor = dismissButtonColor,
        selectedDayColor = selectedDayColor,
        onSelectedDayColor = onSelectedDayColor,
        notSelectedDayColor = notSelectedDayColor,
        todayColor = todayColor
    )


    internal val ColorScheme.DefaultPersianDatePickerColors: PersianDatePickerColors
        @Composable
        get() {
            return PersianDatePickerColors(
                containerColor = this.surfaceContainerHighest,
                titleColor = this.onSurface,
                headerColor = this.onSurface,
                confirmButtonColor = this.primary,
                dismissButtonColor = this.primary,
                selectedDayColor = this.primary,
                onSelectedDayColor = this.onPrimary,
                notSelectedDayColor = this.onSurface,
                todayColor = this.primary
            )
        }

    @Composable
    fun DatePickerTitle(displayMode: DisplayMode, modifier: Modifier = Modifier) {
        when (displayMode) {
            DisplayMode.Companion.Picker ->
                Text(
                    text = stringResource(Res.string.datePickerTitle),
                    modifier = modifier
                )

            DisplayMode.Companion.Input ->
                Text(text = stringResource(Res.string.dateInputTitle), modifier = modifier)
        }
    }

    @OptIn(ExperimentalTime::class)
    @Composable
    fun DatePickerHeadline(
        @Suppress("AutoBoxing") selectedDate: PersianDateTime?,
        displayMode: DisplayMode,
        modifier: Modifier = Modifier,
    ) {

        val headlineText =
            selectedDate?.format {
                day()
                char(' ')
                monthName()
                char(' ')
                year()
            }
                ?: when (displayMode) {
                    DisplayMode.Companion.Picker -> stringResource(Res.string.DatePickerHeadline)
                    DisplayMode.Companion.Input -> stringResource(Res.string.DateInputHeadline)
                    else -> ""
                }


        Text(
            text = headlineText,
            modifier = modifier,
            maxLines = 1
        )
    }

    val Shape: Shape
        @Composable
        get() = MaterialTheme.shapes.extraLarge
    val TonalElevation
        get() = 0.dp
}