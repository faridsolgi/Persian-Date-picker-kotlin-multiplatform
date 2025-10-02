package io.github.faridsolgi.util

import io.github.faridsolgi.persiandatetime.domain.PersianDateTime
import io.github.faridsolgi.persiandatetime.extensions.isLeapYear

internal object PersianCalendarUtils {

    /**
     * Get number of days in a Persian month
     */
    fun getDaysInMonth(year: Int, month: Int): Int {
        return when (month) {
            in 1..6 -> 31
            in 7..11 -> 30
            12 -> if (isLeapYear(year)) 30 else 29
            else -> throw IllegalArgumentException("Invalid month: $month")
        }
    }

    /**
     * Check if a Persian year is leap year
     */
    fun isLeapYear(year: Int): Boolean = PersianDateTime(year, 1, 1, 0, 0, 0).isLeapYear()

    /**
     * Check if two dates are the same day
     */
    fun isSameDay(date1: PersianDateTime, date2: PersianDateTime): Boolean {
        return date1.year == date2.year &&
                date1.month == date2.month &&
                date1.day == date2.day
    }



}