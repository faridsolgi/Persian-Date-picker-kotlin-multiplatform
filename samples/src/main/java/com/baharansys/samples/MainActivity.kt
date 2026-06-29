@file:OptIn(ExperimentalMaterial3Api::class)

package com.baharansys.samples

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.baharansys.samples.components.MaterialDatePickersList
import com.baharansys.samples.components.PersianDatePickersList
import com.baharansys.samples.ui.theme.PersiandatepickerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PersiandatepickerTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Row(
                        Modifier
                            .padding(innerPadding)
                            .fillMaxSize(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        MaterialDatePickersList()
                        PersianDatePickersList()
                    }
                }
            }
        }
    }
}
