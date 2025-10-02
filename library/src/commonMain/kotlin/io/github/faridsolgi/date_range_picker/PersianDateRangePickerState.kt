package io.github.faridsolgi.date_range_picker

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import io.github.faridsolgi.date_picker.view.BasePersianDatePickerStateImpl
import io.github.faridsolgi.date_picker.view.PersianDatePickerDefaults
import io.github.faridsolgi.date_range_picker.PersianDateRangePickerStateImpl.Companion.Saver
import io.github.faridsolgi.domain.SelectableDates
import io.github.faridsolgi.domain.model.DisplayMode
import io.github.faridsolgi.persiandatetime.domain.PersianDateTime
import io.github.faridsolgi.persiandatetime.extensions.toEpochMilliseconds


/**
 * A state object that can be hoisted to observe the date range picker state. See
 * [rememberDateRangePickerState].
 */
@Stable
interface PersianDateRangePickerState {

    /**
     * A timestamp that represents the selected start date _start_ of the day in _UTC_ milliseconds
     * from the epoch.
     *
     * @see [setSelection] for setting this value along with the [selectedEndDate].
     */
    @get:Suppress("AutoBoxing")
    val selectedStartDate: PersianDateTime?

    /**
     * A timestamp that represents the selected end date _start_ of the day in _UTC_ milliseconds
     * from the epoch.
     *
     * @see [setSelection] for setting this value along with the [selectedStartDate].
     */
    @get:Suppress("AutoBoxing")
    val selectedEndDate: PersianDateTime?

    /**
     * A timestamp that represents the currently displayed month _start_ date in _UTC_ milliseconds
     * from the epoch.
     *
     * @throws IllegalArgumentException in case the value is set with a timestamp that does not fall
     *   within the [yearRange].
     */
    var initDisplayedDate: PersianDateTime

    /** A [DisplayMode] that represents the current UI mode (i.e. picker or input). */
    var displayMode: DisplayMode

    /** An [IntRange] that holds the year range that the date picker will be limited to. */
    val yearRange: IntRange

    /**
     * A [SelectableDates] that is consulted to check if a date is allowed.
     *
     * In case a date is not allowed to be selected, it will appear disabled in the UI.
     */
    val selectableDates: SelectableDates

    /**
     * Sets a start and end selection dates.
     *
     * The function expects the dates to be within the state's year-range, and for the start date to
     * appear before, or be equal, the end date. Also, if an end date is provided (e.g. not `null`),
     * a start date is also expected to be provided. In any other case, an
     * [IllegalArgumentException] is thrown.
     *
     * @param startDateMillis timestamp in _UTC_ milliseconds from the epoch that represents the
     *   start date selection. Provide a `null` to indicate no selection.
     * @param endDateMillis timestamp in _UTC_ milliseconds from the epoch that represents the end
     *   date selection. Provide a `null` to indicate no selection.
     * @throws IllegalArgumentException in case the given timestamps do not comply with the expected
     *   values specified above.
     */
    fun setSelection(
        @Suppress("AutoBoxing") startDateMillis: PersianDateTime?,
        @Suppress("AutoBoxing") endDateMillis: PersianDateTime?,
    )
}


/**
 * Creates a [PersianDateRangePickerState] for a [DateRangePicker] that is remembered across compositions.
 *
 * To create a date range picker state outside composition, see the `DateRangePickerState` function.
 *
 * @param initialSelectedStartDateMillis timestamp in _UTC_ milliseconds from the epoch that
 *   represents an initial selection of a start date. Provide a `null` to indicate no selection.
 * @param initialSelectedEndDateMillis timestamp in _UTC_ milliseconds from the epoch that
 *   represents an initial selection of an end date. Provide a `null` to indicate no selection.
 * @param initialDisplayedMonthMillis timestamp in _UTC_ milliseconds from the epoch that represents
 *   an initial selection of a month to be displayed to the user. By default, in case an
 *   `initialSelectedStartDateMillis` is provided, the initial displayed month would be the month of
 *   the selected date. Otherwise, in case `null` is provided, the displayed month would be the
 *   current one.
 * @param yearRange an [IntRange] that holds the year range that the date range picker will be
 *   limited to
 * @param initialDisplayMode an initial [DisplayMode] that this state will hold
 * @param selectableDates a [SelectableDates] that is consulted to check if a date is allowed. In
 *   case a date is not allowed to be selected, it will appear disabled in the UI.
 */
@Composable
fun rememberDateRangePickerState(
    @Suppress("AutoBoxing") initialSelectedStartDate: PersianDateTime? = null,
    @Suppress("AutoBoxing") initialSelectedEndDate: PersianDateTime? = null,
    @Suppress("AutoBoxing") initialDisplayedMonth: PersianDateTime? = initialSelectedStartDate,
    yearRange: IntRange = PersianDatePickerDefaults.YearRange,
    initialDisplayMode: DisplayMode = DisplayMode.Picker,
    selectableDates: SelectableDates = PersianDatePickerDefaults.AllDatesSelectable,
): PersianDateRangePickerState {
    return rememberSaveable(saver = Saver(selectableDates)) {
        PersianDateRangePickerStateImpl(
            initialSelectedStartDate = initialSelectedStartDate,
            initialSelectedEndDate = initialSelectedEndDate,
            initialDisplayedMonth = initialDisplayedMonth,
            yearRange = yearRange,
            initialDisplayMode = initialDisplayMode,
            selectableDates = selectableDates,
        )
    }
        .apply {
            // Update the state's selectable dates if they were changed.
            this.selectableDates = selectableDates
        }
}
/*


/**
 * Creates a [DateRangePickerState].
 *
 * For most cases, you are advised to use the [rememberDateRangePickerState] when in a composition.
 *
 * Note that in case you provide a [locale] that is different than the default platform locale, you
 * may need to ensure that the picker's title and headline are localized correctly. The following
 * sample shows one possible way of doing so by applying a local composition of a `LocalContext` and
 * `LocaleConfiguration`.
 *
 * @sample androidx.compose.material3.samples.DatePickerCustomLocaleSample
 * @param locale the [CalendarLocale] that will be used when formatting dates, determining the input
 *   format, displaying the week-day, determining the first day of the week, and more. Note that in
 *   case the provided [CalendarLocale] differs from the platform's default Locale, you may need to
 *   ensure that the picker's title and headline are localized correctly, and in some cases, you may
 *   need to apply an RTL layout.
 * @param initialSelectedStartDateMillis timestamp in _UTC_ milliseconds from the epoch that
 *   represents an initial selection of a start date. Provide a `null` to indicate no selection.
 * @param initialSelectedEndDateMillis timestamp in _UTC_ milliseconds from the epoch that
 *   represents an initial selection of an end date. Provide a `null` to indicate no selection.
 * @param initialDisplayedMonthMillis timestamp in _UTC_ milliseconds from the epoch that represents
 *   an initial selection of a month to be displayed to the user. By default, in case an
 *   `initialSelectedStartDateMillis` is provided, the initial displayed month would be the month of
 *   the selected date. Otherwise, in case `null` is provided, the displayed month would be the
 *   current one.
 * @param yearRange an [IntRange] that holds the year range that the date picker will be limited to
 * @param initialDisplayMode an initial [DisplayMode] that this state will hold
 * @param selectableDates a [SelectableDates] that is consulted to check if a date is allowed. In
 *   case a date is not allowed to be selected, it will appear disabled in the UI
 * @throws IllegalArgumentException if the initial timestamps do not fall within the year range this
 *   state is created with, or the end date precedes the start date, or when an end date is provided
 *   without a start date (e.g. the start date was null, while the end date was not).
 * @see rememberDateRangePickerState
 *//*

fun DateRangePickerState(
    @Suppress("AutoBoxing") initialSelectedStartDate: PersianDateTime? = null,
    @Suppress("AutoBoxing") initialSelectedEndDate: PersianDateTime? = null,
    @Suppress("AutoBoxing") initialDisplayedMonth: PersianDateTime? = initialSelectedStartDate,
    yearRange: IntRange = PersianDatePickerDefaults.YearRange,
    initialDisplayMode: DisplayMode = DisplayMode.Picker,
    selectableDates: SelectableDates = PersianDatePickerDefaults.AllDatesSelectable,
): PersianDateRangePickerState =
    PersianDateRangePickerStateImpl(
        initialSelectedStartDate = initialSelectedStartDate,
        initialSelectedEndDate= initialSelectedEndDate,
        initialDisplayedMonth = initialDisplayedMonth,
        yearRange = yearRange,
        initialDisplayMode = initialDisplayMode,
        selectableDates = selectableDates
    )



*/*/




/**
 * A default implementation of the [PersianDateRangePickerState]. See [rememberDateRangePickerState].
 *
 * The state's [selectedStartDate] and [selectedEndDate] will provide timestamps for the
 * _beginning_ of the selected days (i.e. midnight in _UTC_ milliseconds from the epoch).
 *
 * @param initialSelectedStartDateMillis timestamp in _UTC_ milliseconds from the epoch that
 *   represents an initial selection of a start date. Provide a `null` to indicate no selection.
 * @param initialSelectedEndDateMillis timestamp in _UTC_ milliseconds from the epoch that
 *   represents an initial selection of an end date. Provide a `null` to indicate no selection.
 * @param initialDisplayedMonthMillis timestamp in _UTC_ milliseconds from the epoch that represents
 *   an initial selection of a month to be displayed to the user. By default, in case an
 *   `initialSelectedStartDateMillis` is provided, the initial displayed month would be the month of
 *   the selected date. Otherwise, in case `null` is provided, the displayed month would be the
 *   current one.
 * @param yearRange an [IntRange] that holds the year range that the date picker will be limited to
 * @param initialDisplayMode an initial [DisplayMode] that this state will hold
 * @param selectableDates a [SelectableDates] that is consulted to check if a date is allowed. In
 *   case a date is not allowed to be selected, it will appear disabled in the UI
 * @param locale a [CalendarLocale] to be used when formatting dates, determining the input format,
 *   and more
 * @throws IllegalArgumentException if the initial timestamps do not fall within the year range this
 *   state is created with, or the end date precedes the start date, or when an end date is provided
 *   without a start date (e.g. the start date was null, while the end date was not).
 * @see rememberDateRangePickerState
 */
@Stable
private class PersianDateRangePickerStateImpl(
    @Suppress("AutoBoxing") initialSelectedStartDate: PersianDateTime?,
    @Suppress("AutoBoxing") initialSelectedEndDate: PersianDateTime?,
    @Suppress("AutoBoxing") initialDisplayedMonth: PersianDateTime?,
    yearRange: IntRange,
    initialDisplayMode: DisplayMode,
    selectableDates: SelectableDates,
) :
    BasePersianDatePickerStateImpl(initialDisplayedMonth, yearRange, selectableDates),
    PersianDateRangePickerState {

    /** A mutable state of [CalendarDate] that represents a selected start date. */
    private var _selectedStartDate = mutableStateOf<PersianDateTime?>(null)

    /** A mutable state of [CalendarDate] that represents a selected end date. */
    private var _selectedEndDate = mutableStateOf<PersianDateTime?>(null)

    /** Initialize the state with the provided initial selections. */
    init {
        setSelection(
            startDate = initialSelectedStartDate,
            endDate = initialSelectedEndDate,
        )
    }

    /**
     * A timestamp that represents the _start_ of the day of the selected start date in _UTC_
     * milliseconds from the epoch.
     *
     * In case no date was selected or provided, the state will hold a `null` value.
     *
     * @throws IllegalArgumentException in case a set timestamp does not fall within the year range
     *   this state was created with.
     */
    override val selectedStartDate: PersianDateTime?
        @Suppress("AutoBoxing") get() = _selectedStartDate.value

    /**
     * A timestamp that represents the _start_ of the day of the selected end date in _UTC_
     * milliseconds from the epoch.
     *
     * In case no date was selected or provided, the state will hold a `null` value.
     *
     * @throws IllegalArgumentException in case a set timestamp does not fall within the year range
     *   this state was created with.
     */
    override val selectedEndDate: PersianDateTime?
        @Suppress("AutoBoxing") get() = _selectedEndDate.value

    /**
     * A mutable state of [DisplayMode] that represents the current display mode of the UI (i.e.
     * picker or input).
     */
    private var _displayMode = mutableStateOf(initialDisplayMode)

    override var displayMode
        get() = _displayMode.value
        set(displayMode) {
            selectedStartDate?.let {
                initDisplayedDate = it
            }
            _displayMode.value = displayMode
        }

    override fun setSelection(
        @Suppress("AutoBoxing") startDate: PersianDateTime?,
        @Suppress("AutoBoxing") endDate: PersianDateTime?,
    ) {

        // Validate that an end date cannot be set without a start date and that the end date
        // appears on or after the start date.
        if (
            startDate != null &&
            (endDate == null || startDate.toEpochMilliseconds() <= endDate.toEpochMilliseconds())
        ) {
            _selectedStartDate.value = startDate
            _selectedEndDate.value = endDate
        } else {
            _selectedStartDate.value = null
            _selectedEndDate.value = null
        }
    }

    private fun getDate(date: PersianDateTime?) =
        if (date != null) {
            // Validate that the date is within the valid years range.
            if (yearRange.contains(date.year)) {
                date
            } else {
                null
            }
        } else {
            null
        }

    companion object {
        /**
         * The default [Saver] implementation for [PersianDateRangePickerStateImpl].
         *
         * @param selectableDates a [SelectableDates] instance that is consulted to check if a date
         *   is allowed
         */
        fun Saver(
            selectableDates: SelectableDates,
        ): Saver<PersianDateRangePickerStateImpl, Any> =
            listSaver(
                save = {
                    listOf(
                        it.selectedStartDate?.toEpochMilliseconds(),
                        it.selectedEndDate?.toEpochMilliseconds(),
                        it.initDisplayedDate.toEpochMilliseconds(),
                        it.yearRange.first,
                        it.yearRange.last,
                        it.displayMode.value,
                    )
                },
                restore = { value ->
                    PersianDateRangePickerStateImpl(
                        initialSelectedStartDate = (value[0] as Long?)?.let {
                            PersianDateTime.parse(it)
                        },
                        initialSelectedEndDate = (value[1] as Long?)?.let {
                            PersianDateTime.parse(it)
                        },
                        initialDisplayedMonth = (value[2] as Long?)?.let {
                            PersianDateTime.parse(it)
                        },
                        yearRange = IntRange(value[3] as Int, value[4] as Int),
                        initialDisplayMode = DisplayMode(value[5] as Int),
                        selectableDates = selectableDates,
                    )
                },
            )
    }
}
