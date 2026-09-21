package io.github.faridsolgi.share

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import io.github.faridsolgi.domain.model.PersianDatePickerColors
import io.github.faridsolgi.domain.model.PersianDatePickerTokens
import io.github.faridsolgi.date_picker.view.PersianDatePickerDefaults
import io.github.faridsolgi.date_picker.view.PersianDatePickerTypography

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersianDatePickerDialog(
    onDismissRequest: () -> Unit,
    confirmButton: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    dismissButton: @Composable (() -> Unit)? = null,
    shape: Shape = PersianDatePickerDefaults.Shape,
    tonalElevation: Dp = PersianDatePickerDefaults.TonalElevation,
    colors: PersianDatePickerColors = PersianDatePickerDefaults.colors(),
    properties: DialogProperties = DialogProperties(usePlatformDefaultWidth = false),
    typography: PersianDatePickerTypography = PersianDatePickerDefaults.typography(),
    content: @Composable ColumnScope.() -> Unit,
) {
    BasicAlertDialog(
        onDismissRequest = onDismissRequest,
        modifier = modifier.wrapContentHeight(),
        properties = properties
    ) {
        MaterialTheme(
            typography = MaterialTheme.typography.copy(
                titleLarge = typography.title,
                bodyLarge = typography.headline,
                bodyMedium = typography.day,
                labelLarge = typography.button,
                labelMedium = typography.weekday,
                labelSmall = typography.weekday
            )
        ) {
            Surface(
                modifier = Modifier
                    .requiredWidth(PersianDatePickerTokens.ContainerWidth)
                    .heightIn(max = PersianDatePickerTokens.ContainerHeight),
                shape = shape,
                color = colors.containerColor,
                tonalElevation = tonalElevation,
            ) {
                Column(verticalArrangement = Arrangement.SpaceBetween) {
                    // Keep the content collapsible when switching to input mode.
                    Box(Modifier.weight(1f, fill = false)) {
                        this@Column.content()
                    }

                    Box(
                        modifier = Modifier
                            .padding(DialogButtonsPadding)
                            .padding(top = 8.dp)
                    ) {
                        CompositionLocalProvider(
                            LocalContentColor provides colors.confirmButtonColor,
                            LocalTextStyle provides typography.button,
                            LocalLayoutDirection provides LayoutDirection.Rtl
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = DialogButtonsCrossAxisSpacing),
                                horizontalArrangement = Arrangement.End,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                dismissButton?.invoke()
                                confirmButton()
                            }
                        }
                    }
                }
            }
        }
    }
}

private val DialogButtonsPadding = PaddingValues(bottom = 8.dp, end = 6.dp)
private val DialogButtonsMainAxisSpacing = 8.dp
private val DialogButtonsCrossAxisSpacing = 12.dp
