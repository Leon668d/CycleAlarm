package com.example.cyclealarm.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.FilterChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.cyclealarm.model.AnchorMode
import com.example.cyclealarm.model.CycleUnit
import com.example.cyclealarm.model.LoopAlarm
import com.example.cyclealarm.model.LoopAlarmInput
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneOffset

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun AlarmEditScreen(
    alarm: LoopAlarm?,
    onCancel: () -> Unit,
    onSave: (LoopAlarmInput) -> Result<LoopAlarm>
) {
    var title by remember(alarm?.id) { mutableStateOf(alarm?.title ?: "") }
    var note by remember(alarm?.id) { mutableStateOf(alarm?.note ?: "") }
    var cycleValue by remember(alarm?.id) { mutableStateOf(alarm?.cycleValue?.toString() ?: "180") }
    var cycleUnit by remember(alarm?.id) { mutableStateOf(alarm?.cycleUnit ?: CycleUnit.DAY) }
    var anchorMode by remember(alarm?.id) { mutableStateOf(alarm?.anchorMode ?: AnchorMode.AFTER_COMPLETION) }
    var startDate by remember(alarm?.id) { mutableStateOf(alarm?.startDate ?: LocalDate.now().toString()) }
    var reminderTime by remember(alarm?.id) { mutableStateOf(alarm?.reminderTime ?: "09:00") }
    var enabled by remember(alarm?.id) { mutableStateOf(alarm?.enabled ?: true) }
    var error by remember { mutableStateOf<String?>(null) }
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = startDate.toEpochMillisOrToday()
    )

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                Button(
                    onClick = {
                        datePickerState.selectedDateMillis?.let {
                            startDate = millisToLocalDate(it).toString()
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("确定")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("取消")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (alarm == null) "新增周期任务" else "编辑周期任务",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                TextButton(onClick = onCancel) { Text("取消") }
            }

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("标题") },
                singleLine = true
            )
            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("备注") },
                minLines = 2
            )
            OutlinedTextField(
                value = cycleValue,
                onValueChange = { cycleValue = it.filter(Char::isDigit) },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("周期数值") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )

            Text("周期单位", style = MaterialTheme.typography.titleSmall)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                CycleUnit.entries.forEach { unit ->
                    FilterChip(
                        selected = cycleUnit == unit,
                        onClick = { cycleUnit = unit },
                        label = { Text(unit.label()) }
                    )
                }
            }

            Text("循环模式", style = MaterialTheme.typography.titleSmall)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                AnchorMode.entries.forEach { mode ->
                    FilterChip(
                        selected = anchorMode == mode,
                        onClick = { anchorMode = mode },
                        label = { Text(mode.label()) }
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = startDate,
                    onValueChange = { startDate = it },
                    modifier = Modifier.weight(1f),
                    label = { Text("开始日期") },
                    singleLine = true
                )
                Button(onClick = { showDatePicker = true }) {
                    Text("选择")
                }
            }
            OutlinedTextField(
                value = reminderTime,
                onValueChange = { reminderTime = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("提醒时间，格式 09:00") },
                singleLine = true
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("启用任务", style = MaterialTheme.typography.titleSmall)
                    Text("停用后不会显示完成和提醒快捷操作", style = MaterialTheme.typography.bodySmall)
                }
                Switch(checked = enabled, onCheckedChange = { enabled = it })
            }

            if (alarm != null) {
                Text(
                    "如果你之前已经添加过系统日历或系统闹钟提醒，请手动检查是否需要删除或修改旧提醒。",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            error?.let {
                Text(it, color = MaterialTheme.colorScheme.error)
            }

            Button(
                onClick = {
                    val input = validateInput(
                        title = title,
                        note = note,
                        cycleValue = cycleValue,
                        cycleUnit = cycleUnit,
                        anchorMode = anchorMode,
                        startDate = startDate,
                        reminderTime = reminderTime,
                        enabled = enabled
                    )

                    if (input == null) {
                        error = "请检查标题、周期、日期和时间格式。"
                    } else {
                        onSave(input).onFailure { error = it.message ?: "保存失败" }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("保存")
            }
        }
    }
}

private fun validateInput(
    title: String,
    note: String,
    cycleValue: String,
    cycleUnit: CycleUnit,
    anchorMode: AnchorMode,
    startDate: String,
    reminderTime: String,
    enabled: Boolean
): LoopAlarmInput? {
    val parsedCycle = cycleValue.toIntOrNull() ?: return null
    if (title.isBlank() || parsedCycle <= 0) return null

    return runCatching {
        LocalDate.parse(startDate)
        LocalTime.parse(reminderTime)
        LoopAlarmInput(
            title = title,
            note = note,
            cycleValue = parsedCycle,
            cycleUnit = cycleUnit,
            anchorMode = anchorMode,
            startDate = startDate,
            reminderTime = reminderTime,
            enabled = enabled
        )
    }.getOrNull()
}

private fun String.toEpochMillisOrToday(): Long {
    return runCatching { LocalDate.parse(this).toEpochMillis() }
        .getOrElse { LocalDate.now().toEpochMillis() }
}

private fun LocalDate.toEpochMillis(): Long {
    return atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
}

private fun millisToLocalDate(millis: Long): LocalDate {
    return Instant.ofEpochMilli(millis).atZone(ZoneOffset.UTC).toLocalDate()
}
