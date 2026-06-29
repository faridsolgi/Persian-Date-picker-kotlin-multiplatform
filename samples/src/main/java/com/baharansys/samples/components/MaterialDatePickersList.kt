@file:OptIn(ExperimentalMaterial3Api::class)

package com.baharansys.samples.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun MaterialDatePickersList(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("Material Date Pickers", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))
        MaterialDatePicker()
        Spacer(modifier = Modifier.height(8.dp))
        MaterialDateRangePicker()
    }
}

@Composable
fun MaterialDatePicker() {
    val state = rememberDatePickerState()
    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        DatePickerDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                TextButton(onClick = {
                    println("Selected date: ${state.selectedDateMillis}")
                    showDialog = false
                }) {
                    Text("Confirm")
                }
            }
        ) {
            DatePicker(state = state)
        }
    }

    Button(onClick = { showDialog = true }) {
        Text("Select A Date")
    }

}

@Composable
fun MaterialDateRangePicker() {
    val state = rememberDateRangePickerState()
    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        DatePickerDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                TextButton(onClick = {
                    println("Selected date: ${state.selectedStartDateMillis}-${state.selectedEndDateMillis}")
                    showDialog = false
                }) {
                    Text("Confirm")
                }
            }
        ) {
            DateRangePicker(state = state)
        }
    }

    Button(onClick = { showDialog = true }) {
        Text("Select A Date Rage")
    }

}