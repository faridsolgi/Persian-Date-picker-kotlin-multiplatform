package io.github.faridsolgi

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.takeOrElse

@Immutable
class PersianDatePickerColors(
    val containerColor: Color,
    val selectedDayColor: Color,
    val onSelectedDayColor: Color,
    val notSelectedDayColor: Color,
    val todayColor: Color,
    val titleColor: Color,
    val headerColor: Color,
    val confirmButtonColor: Color,
    val dismissButtonColor: Color,
) {
    fun copy(
        containerColor: Color = this.containerColor,
        titleColor: Color = this.titleColor,
        headerColor: Color = this.headerColor,
        confirmButtonColor: Color = this.confirmButtonColor,
        dismissButtonColor: Color = this.dismissButtonColor,
        todayColor: Color = this.todayColor,
        selectedDayColor: Color = this.selectedDayColor,
        onSelectedDayColor: Color = this.onSelectedDayColor,
        notSelectedDayColor: Color = this.notSelectedDayColor,
        ) = PersianDatePickerColors(
        containerColor = containerColor.takeOrElse { this.containerColor },

        titleColor = titleColor.takeOrElse { this.titleColor },
        headerColor = headerColor.takeOrElse { this.headerColor },
        confirmButtonColor = confirmButtonColor.takeOrElse { this.confirmButtonColor },
        dismissButtonColor = dismissButtonColor.takeOrElse { this.dismissButtonColor },
        selectedDayColor = selectedDayColor.takeOrElse { this.selectedDayColor },
        onSelectedDayColor = onSelectedDayColor.takeOrElse { this.onSelectedDayColor },
        notSelectedDayColor = notSelectedDayColor.takeOrElse { this.notSelectedDayColor },
        todayColor = todayColor.takeOrElse { this.todayColor }
        )
}

