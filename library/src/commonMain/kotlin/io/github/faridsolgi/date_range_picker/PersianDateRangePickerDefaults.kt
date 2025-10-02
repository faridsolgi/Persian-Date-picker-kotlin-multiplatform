package io.github.faridsolgi.date_range_picker



import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.DatePickerFormatter
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.unit.dp
import io.github.faridsolgi.date_picker.view.PersianDatePickerDefaults
import io.github.faridsolgi.domain.model.DisplayMode
import io.github.faridsolgi.library.generated.resources.datePickerHeadline
import io.github.faridsolgi.library.generated.resources.Res
import io.github.faridsolgi.library.generated.resources.dateInputTitle
import io.github.faridsolgi.library.generated.resources.datePickerTitle
import io.github.faridsolgi.library.generated.resources.dateRangeEndHeadline
import io.github.faridsolgi.library.generated.resources.dateRangeStartHeadline
import io.github.faridsolgi.persiandatetime.domain.PersianDateTime
import io.github.faridsolgi.persiandatetime.extensions.format
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource

@Stable
object PersianDateRangePickerDefaults {
    /**
     * A default date range picker title composable.
     *
     * @param displayMode the current [DisplayMode]
     * @param modifier a [Modifier] to be applied for the title
     * @param contentColor the content color of this title
     */
    @Composable
    fun DateRangePickerTitle(
        displayMode: DisplayMode,
        modifier: Modifier = Modifier,
        contentColor: Color = PersianDatePickerDefaults.colors().titleColor,
    ) {
        when (displayMode) {
            DisplayMode.Picker ->
                Text(
                    stringResource(Res.string.datePickerTitle),
                    modifier = modifier,
                    color = contentColor,
                )
            DisplayMode.Input ->
                Text(
                    stringResource(Res.string.dateInputTitle),
                    modifier = modifier,
                    color = contentColor,
                )
        }
    }

    /**
     * A default date picker headline composable lambda that displays a default headline text when
     * there is no date selection, and an actual date string when there is.
     *
     * @param selectedStartDateMillis a timestamp that represents the selected start date _start_ of
     *   the day in _UTC_ milliseconds from the epoch
     * @param selectedEndDateMillis a timestamp that represents the selected end date _start_ of the
     *   day in _UTC_ milliseconds from the epoch
     * @param displayMode the current [DisplayMode]
     * @param dateFormatter a [DatePickerFormatter]
     * @param modifier a [Modifier] to be applied for the headline
     * @param contentColor the content color of this headline
     */
    @Composable
    fun DateRangePickerHeadline(
        @Suppress("AutoBoxing") selectedStartDate: PersianDateTime?,
        @Suppress("AutoBoxing") selectedEndDate: PersianDateTime?,
        displayMode: DisplayMode,
        modifier: Modifier = Modifier,
        contentColor: Color = PersianDatePickerDefaults.colors().headerColor,
    ) {
        val startDateText = stringResource(Res.string.dateRangeStartHeadline)
        val endDateText = stringResource(Res.string.dateRangeEndHeadline)
        DateRangePickerHeadline(
            selectedStartDate= selectedStartDate,
            selectedEndDate = selectedEndDate,
            displayMode = displayMode,

            modifier = modifier,
            contentColor = contentColor,
            startDateText = startDateText,
            endDateText = endDateText,
            startDatePlaceholder = { Text(text = startDateText, color = contentColor) },
            endDatePlaceholder = { Text(text = endDateText, color = contentColor) },
            datesDelimiter = { Text(text = "-", color = contentColor) },

        )
    }

    @Composable
    private fun DateRangePickerHeadline(
        selectedStartDate: PersianDateTime?,
        selectedEndDate: PersianDateTime?,
        displayMode: DisplayMode,
        modifier: Modifier,
        contentColor: Color,
        startDateText: String,
        endDateText: String,
        startDatePlaceholder: @Composable () -> Unit,
        endDatePlaceholder: @Composable () -> Unit,
        datesDelimiter: @Composable () -> Unit,
    ) {


        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            if (selectedStartDate != null) {
                Text(text = selectedStartDate.format {
                    day()
                    char(' ')
                    monthName()
                    char(' ')
                    year()
                }, color = contentColor)
            } else {
                startDatePlaceholder()
            }
            datesDelimiter()
            if (selectedEndDate != null) {
                Text(text = selectedEndDate.format {
                    day()
                    char(' ')
                    monthName()
                    char(' ')
                    year()
                }, color = contentColor)
            } else {
                endDatePlaceholder()
            }
        }
}

}