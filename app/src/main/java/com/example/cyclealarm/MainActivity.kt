package com.example.cyclealarm

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cyclealarm.model.LoopAlarm
import com.example.cyclealarm.system.SystemAlarmIntentHelper
import com.example.cyclealarm.system.SystemCalendarIntentHelper
import com.example.cyclealarm.ui.AlarmDetailScreen
import com.example.cyclealarm.ui.AlarmEditScreen
import com.example.cyclealarm.ui.CompleteResultDialog
import com.example.cyclealarm.ui.HomeScreen
import com.example.cyclealarm.viewmodel.LoopAlarmViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(
                colorScheme = lightColorScheme(
                    primary = Color(0xFF2F5D50),
                    secondary = Color(0xFFD7634D),
                    background = Color(0xFFF7F8F4),
                    surface = Color(0xFFFFFFFF),
                    onPrimary = Color.White
                )
            ) {
                val context = LocalContext.current
                val viewModel: LoopAlarmViewModel = viewModel()
                val state = viewModel.uiState
                var route by rememberSaveable { mutableStateOf("home") }
                var selectedAlarmId by rememberSaveable { mutableStateOf<Long?>(null) }
                var completedAlarm by remember { mutableStateOf<LoopAlarm?>(null) }

                LaunchedEffect(state.message) {
                    state.message?.let {
                        Toast.makeText(context, it, Toast.LENGTH_LONG).show()
                        viewModel.clearMessage()
                    }
                }

                completedAlarm?.let { alarm ->
                    CompleteResultDialog(
                        alarm = alarm,
                        onCalendar = { SystemCalendarIntentHelper.openCalendarInsert(context, alarm) },
                        onAlarm = { SystemAlarmIntentHelper.openSystemAlarm(context, alarm) },
                        onDismiss = { completedAlarm = null }
                    )
                }

                when (route) {
                    "edit" -> AlarmEditScreen(
                        alarm = selectedAlarmId?.let(viewModel::getAlarm),
                        onCancel = {
                            route = if (selectedAlarmId == null) "home" else "detail"
                        },
                        onSave = { input ->
                            val result = selectedAlarmId?.let { viewModel.update(it, input) }
                                ?: viewModel.create(input)
                            result.onSuccess { saved ->
                                selectedAlarmId = saved.id
                                route = "detail"
                            }
                            result
                        }
                    )

                    "detail" -> AlarmDetailScreen(
                        alarm = selectedAlarmId?.let(viewModel::getAlarm),
                        onBack = {
                            selectedAlarmId = null
                            route = "home"
                        },
                        onEdit = { route = "edit" },
                        onDelete = {
                            viewModel.delete(it)
                            selectedAlarmId = null
                            route = "home"
                        },
                        onComplete = {
                            completedAlarm = viewModel.complete(it)
                        },
                        onToggleEnabled = { id, enabled -> viewModel.setEnabled(id, enabled) },
                        onCalendar = { SystemCalendarIntentHelper.openCalendarInsert(context, it) },
                        onAlarm = { SystemAlarmIntentHelper.openSystemAlarm(context, it) }
                    )

                    else -> HomeScreen(
                        alarms = state.alarms,
                        onAdd = {
                            selectedAlarmId = null
                            route = "edit"
                        },
                        onOpenDetail = {
                            selectedAlarmId = it
                            route = "detail"
                        },
                        onComplete = {
                            completedAlarm = viewModel.complete(it)
                        },
                        onToggleEnabled = { id, enabled -> viewModel.setEnabled(id, enabled) },
                        onCalendar = { SystemCalendarIntentHelper.openCalendarInsert(context, it) },
                        onAlarm = { SystemAlarmIntentHelper.openSystemAlarm(context, it) },
                        onCreateTemplate = { viewModel.createTemplate(it) }
                    )
                }
            }
        }
    }
}
