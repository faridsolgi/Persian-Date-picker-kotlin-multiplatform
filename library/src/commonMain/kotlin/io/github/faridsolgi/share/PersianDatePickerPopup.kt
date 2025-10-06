package io.github.faridsolgi.share

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.requiredWidthIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupPositionProvider
import io.github.faridsolgi.domain.model.PersianDatePickerColors
import io.github.faridsolgi.domain.model.PersianDatePickerTokens
import io.github.faridsolgi.date_picker.view.PersianDatePickerDefaults

@Composable
fun PersianDatePickerPopup(
    expanded: Boolean,
    anchor: @Composable () -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = PersianDatePickerDefaults.Shape,
    colors: PersianDatePickerColors = PersianDatePickerDefaults.colors(),
    offsetY: Dp = PersianDatePickerDefaults.PopupOffsetY,
    alignment: Alignment = Alignment.TopEnd,
    content: @Composable () -> Unit,
) {
    val dropdownPopupPositioner = remember(offsetY, alignment) {
        object : PopupPositionProvider {
            override fun calculatePosition(
                anchorBounds: IntRect,
                windowSize: IntSize,
                layoutDirection: LayoutDirection,
                popupContentSize: IntSize,
            ): IntOffset {
                // Calculate x position based on alignment and layoutDirection
                val x = when (alignment) {
                    Alignment.TopStart, Alignment.CenterStart, Alignment.BottomStart -> {
                        if (layoutDirection == LayoutDirection.Ltr) {
                            // Start = left
                            anchorBounds.left
                        } else {
                            // Start = right
                            anchorBounds.right - popupContentSize.width
                        }
                    }

                    Alignment.TopCenter, Alignment.Center, Alignment.BottomCenter -> {
                        anchorBounds.left + (anchorBounds.width - popupContentSize.width) / 2
                    }

                    Alignment.TopEnd, Alignment.CenterEnd, Alignment.BottomEnd -> {
                        if (layoutDirection == LayoutDirection.Ltr) {
                            // End = right
                            anchorBounds.right - popupContentSize.width
                        } else {
                            // End = left
                            anchorBounds.left
                        }
                    }

                    else -> anchorBounds.left // Default fallback
                }

                // Calculate y position based on alignment
                val y = when (alignment) {
                    Alignment.TopStart, Alignment.TopCenter, Alignment.TopEnd -> {
                        anchorBounds.top - popupContentSize.height - offsetY.value.toInt()
                    }

                    Alignment.CenterStart, Alignment.Center, Alignment.CenterEnd -> {
                        anchorBounds.top + (anchorBounds.height - popupContentSize.height) / 2
                    }

                    Alignment.BottomStart, Alignment.BottomCenter, Alignment.BottomEnd -> {
                        anchorBounds.bottom + offsetY.value.toInt()
                    }

                    else -> anchorBounds.bottom + offsetY.value.toInt() // Default to bottom
                }

                // Adjust x if popup goes off screen
                val adjustedX = when {
                    x + popupContentSize.width > windowSize.width -> windowSize.width - popupContentSize.width
                    x < 0 -> 0
                    else -> x
                }

                // Adjust y if popup goes off screen
                val adjustedY = when {
                    y + popupContentSize.height > windowSize.height -> {
                        if (y > anchorBounds.top) {
                            anchorBounds.top - popupContentSize.height - offsetY.value.toInt()
                        } else {
                            y
                        }
                    }

                    y < 0 -> {
                        anchorBounds.bottom + offsetY.value.toInt()
                    }

                    else -> y
                }

                return IntOffset(adjustedX.coerceAtLeast(0), adjustedY.coerceAtLeast(0))
            }
        }
    }

    Box {
        anchor()

        if (expanded) {
            Popup(
                onDismissRequest = onDismissRequest, popupPositionProvider = dropdownPopupPositioner
            ) {
                Box(
                    modifier = modifier.requiredWidth( PersianDatePickerTokens.ContainerWidth)
                        .heightIn(min= PersianDatePickerTokens.ContainerHeight,max = PersianDatePickerTokens.ContainerHeightMax)
                        .background(colors.containerColor, shape = shape)
                        .padding(top = 8.dp)
                ) {
                    CompositionLocalProvider(
                        LocalLayoutDirection provides LayoutDirection.Rtl
                    ) {
                        content()
                    }
                }
            }
        }
    }
}