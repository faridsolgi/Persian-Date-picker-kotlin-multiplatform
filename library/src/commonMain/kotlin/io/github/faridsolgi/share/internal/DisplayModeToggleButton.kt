package io.github.faridsolgi.share.internal

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Edit
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
                imageVector = Icons.Outlined.Edit,
                contentDescription = stringResource(Res.string.datePickerSwitchToInputMode)
            )
        }
    } else {
        IconButton(onClick = { onDisplayModeChange(DisplayMode.Companion.Picker) }, modifier = modifier) {
            Icon(
                imageVector = Icons.Outlined.DateRange,
                contentDescription = stringResource(Res.string.datePickerSwitchToPickerMode)
            )
        }
    }
}