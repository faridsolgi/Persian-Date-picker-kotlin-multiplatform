package io.github.faridsolgi.view

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.github.faridsolgi.domain.model.DisplayMode
import io.github.faridsolgi.library.generated.resources.Res
import io.github.faridsolgi.library.generated.resources.datePickerSwitchToInputMode
import io.github.faridsolgi.library.generated.resources.datePickerSwitchToPickerMode
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun DisplayModeToggleButton(
    modifier: Modifier,
    displayMode: DisplayMode,
    onDisplayModeChange: (DisplayMode) -> Unit
) {
    if (displayMode == DisplayMode.Companion.Picker) {
        IconButton(onClick = { onDisplayModeChange(DisplayMode.Companion.Input) }, modifier = modifier) {
            Icon(
                imageVector = Icons.Filled.Edit,
                contentDescription = stringResource(Res.string.datePickerSwitchToInputMode)
            )
        }
    } else {
        IconButton(onClick = { onDisplayModeChange(DisplayMode.Companion.Picker) }, modifier = modifier) {
            Icon(
                imageVector = Icons.Filled.DateRange,
                contentDescription = stringResource(Res.string.datePickerSwitchToPickerMode)
            )
        }
    }
}