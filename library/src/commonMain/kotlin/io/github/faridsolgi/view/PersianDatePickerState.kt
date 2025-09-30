package io.github.faridsolgi.view


import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import io.github.faridsolgi.domain.model.DisplayMode
import io.github.faridsolgi.domain.SelectableDates
import io.github.faridsolgi.view.PersianDatePickerStateImpl.Companion.Saver
import io.github.faridsolgi.persiandatetime.converter.nowPersianDate
import io.github.faridsolgi.persiandatetime.converter.toLocalDate
import io.github.faridsolgi.persiandatetime.converter.toPersianDateTime
import io.github.faridsolgi.persiandatetime.domain.PersianDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@Stable
interface PersianDatePickerState {

    /**
     * A timestamp that represents the selected date _start_ of the day in _UTC_ milliseconds from
     * the epoch.
     *
     * @throws IllegalArgumentException in case the value is set with a timestamp that does not fall
     *   within the [yearRange].
     */
    @get:Suppress("AutoBoxing")
    var selectedDate: PersianDateTime?

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
}

@OptIn(ExperimentalMaterial3Api::class)
private class PersianDatePickerStateImpl(
    @Suppress("AutoBoxing") initialSelectedDate: PersianDateTime? = null,
    @Suppress("AutoBoxing") initDisplayedDate: PersianDateTime? = initialSelectedDate,
    yearRange: IntRange = PersianDatePickerDefaults.YearRange,
    initialDisplayMode: DisplayMode = DisplayMode.Companion.Picker,
    selectableDates: SelectableDates = PersianDatePickerDefaults.AllDatesSelectable,
) : BasePersianDatePickerStateImpl(initDisplayedDate, yearRange, selectableDates),
    PersianDatePickerState {
    private var _selectedDate =
        mutableStateOf(
            if (initialSelectedDate != null) {
                val date = initialSelectedDate
                require(yearRange.contains(date.year)) {
                    "The provided initial date's year (${date.year}) is out of the years range " +
                            "of $yearRange."
                }
                date
            } else {
                null
            }
        )

    override var selectedDate: PersianDateTime?
        get() = _selectedDate.value
        set(value) {
            _selectedDate.value = value

        }

    private var _displayMode = mutableStateOf(initialDisplayMode)
    override var displayMode: DisplayMode
        get() = _displayMode.value
        set(value) {
            _displayMode.value = value
            selectedDate?.let {
                initDisplayedDate = it
            }
        }

    companion object {
        /**
         * The default [Saver] implementation for [DatePickerStateImpl].
         *
         * @param selectableDates a [SelectableDates] instance that is consulted to check if a date
         *   is allowed
         */
        @OptIn(ExperimentalTime::class)
        fun Saver(
            selectableDates: SelectableDates,
        ): Saver<PersianDatePickerStateImpl, Any> =
            listSaver(
                save = {
                    listOf(
                        it.selectedDate?.toLocalDate()
                            ?.atStartOfDayIn(TimeZone.currentSystemDefault())
                            ?.toEpochMilliseconds(),
                        it.initDisplayedDate.toLocalDate()
                            .atStartOfDayIn(TimeZone.currentSystemDefault()).toEpochMilliseconds(),
                        it.yearRange.first,
                        it.yearRange.last,
                        it.displayMode.value
                    )
                },
                restore = { value ->
                    PersianDatePickerStateImpl(
                        initialSelectedDate = (value[0] as Long?)?.let {
                            Instant.fromEpochMilliseconds(it)
                                .toPersianDateTime(
                                    TimeZone.currentSystemDefault()
                                )
                        },
                        initDisplayedDate = (value[1] as Long?)?.let {
                            Instant.fromEpochMilliseconds(it)
                                .toPersianDateTime(TimeZone.currentSystemDefault())
                        },
                        yearRange = IntRange(value[2] as Int, value[3] as Int),
                        initialDisplayMode = DisplayMode(value[4] as Int),
                        selectableDates = selectableDates
                    )
                }
            )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Stable
internal abstract class BasePersianDatePickerStateImpl(
    @Suppress("AutoBoxing") initDisplayedDate: PersianDateTime?,
    val yearRange: IntRange,
    selectableDates: SelectableDates,
) {

    var selectableDates by mutableStateOf(selectableDates)

    @OptIn(ExperimentalTime::class)
    private var _displayedDate =
        mutableStateOf(
            if (initDisplayedDate != null) {
                val date = initDisplayedDate
                require(yearRange.contains(date.year)) {
                    "The initial display month's year (${date.year}) is out of the years range of " +
                            "$yearRange."
                }
                date
            } else {
                // Set the displayed month to the current one.
                Clock.System.nowPersianDate(TimeZone.currentSystemDefault())
            }
        )

    var initDisplayedDate: PersianDateTime
        get() = _displayedDate.value
        set(value) {
            val date = value
            require(yearRange.contains(date.year)) {
                "The display month's year (${date.year}) is out of the years range of $yearRange."
            }
            _displayedDate.value = date
        }
}


@Composable
@ExperimentalMaterial3Api
fun rememberPersianDatePickerState(
    @Suppress("AutoBoxing") initialSelectedDate: PersianDateTime? = null,
    @Suppress("AutoBoxing") initialDisplayedDate: PersianDateTime? = initialSelectedDate,
    yearRange: IntRange = PersianDatePickerDefaults.YearRange,
    initialDisplayMode: DisplayMode = DisplayMode.Companion.Picker,
    selectableDates: SelectableDates = PersianDatePickerDefaults.AllDatesSelectable,
): PersianDatePickerState {
    return rememberSaveable(saver = Saver(selectableDates)) {
        PersianDatePickerStateImpl(
            initialSelectedDate = initialSelectedDate,
            initDisplayedDate = initialDisplayedDate,
            yearRange = yearRange,
            initialDisplayMode = initialDisplayMode,
            selectableDates = selectableDates
        )
    }.apply {
        // Update the state's selectable dates if they were changed.
        this.selectableDates = selectableDates
    }
}