package io.github.faridsolgi.domain.model

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp

internal object PersianDatePickerTokens {
    val ContainerHeight = 568.0.dp
    val ContainerWidth = 360.0.dp
    val DateContainerHeight = 40.0.dp
    val HeadlineTextStyle : TextStyle
        @Composable
        get() =  MaterialTheme.typography.headlineMedium

    val titleTextStyle : TextStyle
        @Composable
        get() =  MaterialTheme.typography.bodyMedium
    val todayDateBorderWidth = 1.dp
    val SelectionYearLabelTextFont
        @Composable
        get() = MaterialTheme.typography.bodyLarge
}