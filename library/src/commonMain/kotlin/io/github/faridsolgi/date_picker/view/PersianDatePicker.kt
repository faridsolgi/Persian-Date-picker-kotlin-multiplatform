package io.github.faridsolgi.date_picker.view


import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import io.github.faridsolgi.domain.model.DisplayMode
import io.github.faridsolgi.domain.model.PersianDatePickerColors
import io.github.faridsolgi.domain.model.PersianDatePickerTokens
import io.github.faridsolgi.library.generated.resources.Res
import io.github.faridsolgi.library.generated.resources.date
import io.github.faridsolgi.library.generated.resources.dateHint
import io.github.faridsolgi.library.generated.resources.error_pattern_not_valid
import io.github.faridsolgi.library.generated.resources.error_year_not_valid_range
import io.github.faridsolgi.persiandatetime.domain.PersianDateTime
import io.github.faridsolgi.persiandatetime.extensions.format
import io.github.faridsolgi.persiandatetime.extensions.toDateString
import io.github.faridsolgi.util.DateVisualTransformation
import io.github.faridsolgi.share.internal.DisplayModeToggleButton
import io.github.faridsolgi.date_picker.view.internal.PersianDatePickerCalendar
import io.github.faridsolgi.share.internal.ProvideContentColorTextStyle
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersianDatePicker(
    state: PersianDatePickerState,
    modifier: Modifier = Modifier,
    title: (@Composable () -> Unit)? = {
        PersianDatePickerDefaults.DatePickerTitle(
            displayMode = state.displayMode,
            modifier = Modifier.padding(DatePickerTitlePadding)
        )
    },
    headline: (@Composable () -> Unit)? = {
        PersianDatePickerDefaults.DatePickerHeadline(
            selectedDate = state.selectedDate,
            displayMode = state.displayMode,
            modifier = Modifier.padding(DatePickerHeadlinePadding)

        )
    },
    showModeToggle: Boolean = true,
    colors: PersianDatePickerColors = PersianDatePickerDefaults.colors(),
) {

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Column(modifier) {
            PersianDatePickerHeadLine(
                state,
                title,
                headline,
                showModeToggle,
                colors
            )
            HorizontalDivider()
            SwitchablePersianDatePickerContents(
                state, colors,
                Modifier.padding(horizontal = 16.dp)
            )

        }
    }
}

@Composable
fun SwitchablePersianDatePickerContents(
    state: PersianDatePickerState,
    colors: PersianDatePickerColors,
    modifier: Modifier = Modifier,
) {

        AnimatedContent(
            targetState = state.displayMode ,
            transitionSpec = {
                slideInHorizontally(animationSpec = tween(500)) { height -> height } + fadeIn() togetherWith slideOutHorizontally(
                    animationSpec = tween(500)
                ) { height -> -height } + fadeOut()
            },
            label = "display mode transition"
        ) { displayMode ->
            Column(modifier) {
            when (displayMode) {
                DisplayMode.Companion.Picker -> {
                    PersianDatePickerCalendar(state, colors)
                }

                DisplayMode.Companion.Input -> {
                    PersianDateEnterSection(state, colors)
                }
            }
        }
    }
}

@Composable
internal fun PersianDateEnterSection(
    state: PersianDatePickerState,
    colors: PersianDatePickerColors,
) {
    var enteredDate by remember { mutableStateOf(state.selectedDate?.format { year();month();day(); }?:"") }
    var isError by remember { mutableStateOf(false) }
    var errorText by remember { mutableStateOf("") }
    val errorPatternNotValid = stringResource(Res.string.error_pattern_not_valid)
    val errorYearNotValidRange = stringResource(Res.string.error_year_not_valid_range,state.yearRange.first,state.yearRange.last)
    LaunchedEffect(enteredDate) {
        if (enteredDate.length == 8) {
            val year = enteredDate.substring(0, 4).toIntOrNull()
            val month = enteredDate.substring(4, 6).toIntOrNull()
            val day = enteredDate.substring(6, 8).toIntOrNull()
            if (year == null || year !in 1300..1500) {
                    isError = true
                    errorText = errorYearNotValidRange
                state.selectedDate =null
                return@LaunchedEffect
            }
            try {
               val validDate = PersianDateTime(year=year,month=month!!,day=day!!)
                state.selectedDate = validDate
            }catch (e: IllegalArgumentException){
                isError = true
                errorText = e.message.toString()
                state.selectedDate =null
            }catch (e: Exception){
                isError = true
                errorText = errorPatternNotValid
                state.selectedDate =null
            }
        } else {
            isError = false
            errorText = ""
            state.selectedDate =null
        }
    }

    OutlinedTextField(
        modifier = Modifier.fillMaxWidth()
            .padding(horizontal = 24.dp)
            .padding(top = 16.dp),
        value = enteredDate,
        onValueChange = { newValue ->
            // Keep only digits
            val digits = newValue.filter { it.isDigit() }
            enteredDate = digits.take(10)
        },
        isError = isError,
        supportingText = {
            Text(errorText,
                style = LocalTextStyle.current.copy(textAlign = TextAlign.Start)
            )
        },
        label = { Text(stringResource(Res.string.date)) },
        placeholder = { Text(stringResource(Res.string.dateHint)) },
        singleLine = true,
        visualTransformation = DateVisualTransformation(),
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview(heightDp = 480, widthDp = 720)
private fun PersianDatePickerPreview() {
    MaterialTheme {
        /*    val state = rememberPersianDatePickerState(yearRange = 1400..1500)

            PersianDatePickerDialog(
                dismissButton = {
                    TextButton({}) {
                        Text("لغو")
                    }
                },
                confirmButton = {
                    TextButton({}) {
                        Text("تایید")
                    }
                },
                onDismissRequest = {

                },
            ) {
                PersianDatePicker(
                    state = state
                )
            }*/

        var showDatePicker by remember { mutableStateOf(false) }
        val datePickerState = rememberPersianDatePickerState()
        val selectedDate = datePickerState.selectedDate?.let {
            it.toDateString()
        } ?: ""

        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = selectedDate,
                onValueChange = { },
                label = { Text("DOB") },
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = { showDatePicker = !showDatePicker }) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = "Select date"
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth().height(64.dp)
            )

            if (showDatePicker) {
                Popup(
                    onDismissRequest = { showDatePicker = false }, alignment = Alignment.TopStart
                ) {
                    Box(
                        modifier = Modifier.fillMaxWidth().offset(y = 64.dp)
                            .shadow(elevation = 4.dp).background(MaterialTheme.colorScheme.surface)
                            .padding(16.dp)
                    ) {
                        PersianDatePicker(
                            state = datePickerState, showModeToggle = false
                        )
                    }
                }
            }
        }
    }
}


@Composable
internal fun PersianDatePickerHeadLine(
    state: PersianDatePickerState,
    title: (@Composable () -> Unit)?,
    headline: (@Composable () -> Unit)?,
    showModeToggle: Boolean,
    colors: PersianDatePickerColors,
) {
    Column(
        modifier = Modifier
            .sizeIn(minWidth = PersianDatePickerTokens.ContainerWidth)
    ) {
        ProvideContentColorTextStyle(
            colors.titleColor,
            PersianDatePickerTokens.titleTextStyle
        ) {
            title?.invoke()
        }
        Spacer(Modifier.padding(vertical = 16.dp))
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ProvideContentColorTextStyle(
                colors.headerColor,
                PersianDatePickerTokens.HeadlineTextStyle
            ) {
                headline?.invoke()
            }

            if (showModeToggle) {
                DisplayModeToggleButton(
                    Modifier.padding(end = 16.dp),
                    displayMode = state.displayMode,
                    onDisplayModeChange = { state.displayMode = it }
                )
            }
        }
    }
}


private val DatePickerTitlePadding = PaddingValues(start = 24.dp, end = 12.dp, top = 16.dp)
private val DatePickerHeadlinePadding = PaddingValues(start = 24.dp, end = 12.dp, bottom = 12.dp)

