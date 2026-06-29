package io.github.faridsolgi.util

import io.github.faridsolgi.date_picker.view.PersianDatePickerState
import io.github.faridsolgi.persiandatetime.domain.PersianDateTime


internal fun PersianDatePickerState.navigateToPreviousMonth() {
    val current = this.initDisplayedDate
    val newDate = if (current.month == 1) {
        // Go to previous year, month 12
        val newYear = current.year - 1
        if (newYear >= this.yearRange.first) {
            PersianDateTime(newYear, 12, 1, 0, 0, 0)
        } else {
            return // Can't go back further
        }
    } else {
        // Go to previous month
        PersianDateTime(current.year, current.month - 1, 1, 0, 0, 0)
    }
    this.initDisplayedDate = newDate
}


internal fun PersianDatePickerState.navigateToNextMonth() {
    val current = initDisplayedDate
    val newDate = if (current.month == 12) {
        // Go to next year, month 1
        val newYear = current.year + 1
        if (newYear <= yearRange.last) {
            PersianDateTime(newYear, 1, 1, 0, 0, 0)
        } else {
            return // Can't go forward further
        }
    } else {
        // Go to next month
        PersianDateTime(current.year, current.month + 1, 1, 0, 0, 0)
    }
    initDisplayedDate = newDate
}


internal val PersianDatePickerState.canNavigateToPreviousMonth: Boolean
    get() {
        initDisplayedDate.let {
            if (it.month != 1) return true
            if (it.year > yearRange.first) return true
            return false
        }
    }

internal val PersianDatePickerState.canNavigateToNextMonth: Boolean
    get() {
        initDisplayedDate.let {
            if (it.month != 12) return true
            if (it.year < yearRange.last) return true
            return false
        }
    }