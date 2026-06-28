package com.example.cyclealarm.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.cyclealarm.model.LoopAlarm
import com.example.cyclealarm.viewmodel.DefaultTemplate

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HomeScreen(
    alarms: List<LoopAlarm>,
    onAdd: () -> Unit,
    onOpenDetail: (Long) -> Unit,
    onComplete: (Long) -> Unit,
    onToggleEnabled: (Long, Boolean) -> Unit,
    onCalendar: (LoopAlarm) -> Unit,
    onAlarm: (LoopAlarm) -> Unit,
    onCreateTemplate: (DefaultTemplate) -> Unit
) {
    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Cycle Alarm",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(text = "${alarms.count { it.enabled }} 个启用任务", style = MaterialTheme.typography.bodyMedium)
                }
                Button(onClick = onAdd) {
                    Text("新增")
                }
            }

            if (alarms.isEmpty()) {
                EmptyState(onAdd = onAdd, onCreateTemplate = onCreateTemplate)
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(alarms, key = { it.id }) { alarm ->
                        AlarmCard(
                            alarm = alarm,
                            onOpenDetail = { onOpenDetail(alarm.id) },
                            onComplete = { onComplete(alarm.id) },
                            onToggleEnabled = { onToggleEnabled(alarm.id, !alarm.enabled) },
                            onCalendar = { onCalendar(alarm) },
                            onAlarm = { onAlarm(alarm) }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun EmptyState(
    onAdd: () -> Unit,
    onCreateTemplate: (DefaultTemplate) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("还没有周期任务", style = MaterialTheme.typography.titleMedium)
            Text(
                "可以新增一个长周期任务，或用模板快速创建测试数据。",
                style = MaterialTheme.typography.bodyMedium
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = onAdd) { Text("新增任务") }
            }
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                DefaultTemplate.entries.forEach { template ->
                    FilterChip(
                        selected = false,
                        onClick = { onCreateTemplate(template) },
                        label = { Text(template.label) }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun AlarmCard(
    alarm: LoopAlarm,
    onOpenDetail: () -> Unit,
    onComplete: () -> Unit,
    onToggleEnabled: () -> Unit,
    onCalendar: () -> Unit,
    onAlarm: () -> Unit
) {
    var showDisableDialog by remember { mutableStateOf(false) }

    if (showDisableDialog) {
        AlertDialog(
            onDismissRequest = { showDisableDialog = false },
            title = { Text("停用这个任务？") },
            text = {
                Text("停用只会影响 App 内的任务状态，不会删除或修改你已经添加到系统日历、系统闹钟里的提醒。")
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDisableDialog = false
                        onToggleEnabled()
                    }
                ) {
                    Text("停用")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDisableDialog = false }) {
                    Text("取消")
                }
            }
        )
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(alarm.title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
                    Text(
                        alarm.remainingText(),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (alarm.enabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                TextButton(onClick = { if (alarm.enabled) showDisableDialog = true else onToggleEnabled() }) {
                    Text(if (alarm.enabled) "停用" else "启用")
                }
            }
            Text(
                text = "${alarm.nextDueDate} ${alarm.reminderTime} · 每 ${alarm.cycleValue} ${alarm.cycleUnit.label()}",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (alarm.enabled) {
                    Button(onClick = onComplete) { Text("完成") }
                    OutlinedButton(onClick = onCalendar) { Text("日历") }
                    OutlinedButton(onClick = onAlarm) { Text("闹钟") }
                }
                OutlinedButton(onClick = onOpenDetail) { Text("详情") }
            }
        }
    }
}
