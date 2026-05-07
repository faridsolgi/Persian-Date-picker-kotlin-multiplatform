@file:OptIn(ExperimentalMaterial3Api::class)

package com.baharansys.samples.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.faridsolgi.date_picker.view.PersianDatePicker
import io.github.faridsolgi.date_picker.view.rememberPersianDatePickerState
import io.github.faridsolgi.date_range_picker.PersianDateRangePicker
import io.github.faridsolgi.date_range_picker.rememberPersianDateRangePickerState
import io.github.faridsolgi.share.PersianDatePickerDialog
import io.github.faridsolgi.share.PersianDatePickerPopup

@Composable
fun PersianDatePickersList(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("انتخاب تاریخ فارسی", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))
        PersianDatePickerDialog()
        Spacer(modifier = Modifier.height(8.dp))
        PersianDateRangePickerDialog()
        Spacer(modifier = Modifier.height(8.dp))
        PersianDatePickerPopUp()
        Spacer(modifier = Modifier.height(8.dp))
        PersianRangeDatePickerPopUp()
        Spacer(modifier = Modifier.height(8.dp))
        CustomTitleAndHeadline()
    }
}

@Composable
fun PersianDatePickerDialog() {
    val state = rememberPersianDatePickerState()
    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        PersianDatePickerDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                TextButton(onClick = {
                    println("Selected date: ${state.selectedDate}")
                    showDialog = false
                }) {
                    Text("تایید")
                }
            }
        ) {
            PersianDatePicker(state = state)
        }
    }

    Button(onClick = { showDialog = true }) {
        Text("انتخاب تاریخ")
    }
}

@Composable
fun PersianDateRangePickerDialog() {
    val rangeState = rememberPersianDateRangePickerState()
    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        PersianDatePickerDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                TextButton(onClick = {
                    println("Start: ${rangeState.selectedStartDate}")
                    println("End: ${rangeState.selectedEndDate}")
                    showDialog = false
                }) {
                    Text("تایید")
                }
            }
        ) {
            PersianDateRangePicker(state = rangeState)
        }
    }

    Button(onClick = { showDialog = true }) {
        Text("انتخاب بازه تاریخ")
    }
}

@Composable
fun PersianDatePickerPopUp() {
    val state = rememberPersianDatePickerState()
    var expanded by remember { mutableStateOf(false) }

    PersianDatePickerPopup(
        expanded = expanded,
        onDismissRequest = { expanded = false },
        anchor = {
            Button(onClick = { expanded = true }) {
                Text("انتخاب تاریخ")
            }
        }
    ) {
        PersianDatePicker(state = state)
    }
}

@Composable
fun PersianRangeDatePickerPopUp() {
    val rangeState = rememberPersianDateRangePickerState()
    var expanded by remember { mutableStateOf(false) }

    PersianDatePickerPopup(
        expanded = expanded,
        onDismissRequest = { expanded = false },
        anchor = {
            Button(onClick = { expanded = true }) {
                Text("انتخاب بازه تاریخ")
            }
        }
    ) {
        PersianDateRangePicker(state = rangeState)
    }
}

@Composable
fun CustomTitleAndHeadline() {
    val state = rememberPersianDatePickerState()
    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {

        PersianDatePickerDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = { TextButton(onClick = {}) { Text("تایید") } }
        ) {
            PersianDatePicker(
                state = state,
                title = {
                    Text(
                        text = "📅 انتخاب تاریخ",
                        style = MaterialTheme.typography.headlineMedium,
                        modifier = Modifier.padding(16.dp)
                    )
                },
                headline = {
                    Text(
                        text = state.selectedDate?.toString() ?: "هیچ تاریخی انتخاب نشده",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            )
        }
    }

    Button(onClick = { showDialog = true }) {
        Text("📅 انتخاب تاریخ")
    }

}
