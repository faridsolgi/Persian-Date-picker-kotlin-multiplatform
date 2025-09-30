package io.github.faridsolgi

import androidx.compose.runtime.Stable

/** Represents the different modes that a date picker can be at. */


/** An interface that controls the selectable dates and years in the date pickers UI. */

@Stable
interface SelectableDates {

    /**
     * Returns true if the date item representing the [utcTimeMillis] should be enabled for
     * selection in the UI.
     */
    fun isSelectableDate(utcTimeMillis: Long) = true

    /**
     * Returns true if a given [year] should be enabled for selection in the UI. When a year is
     * defined as non selectable, all the dates in that year will also be non selectable.
     */
    fun isSelectableYear(year: Int) = true
}

