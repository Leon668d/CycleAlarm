package com.example.cyclealarm.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
                    Text(text = "周期闹钟", style = MaterialTheme.typography.bodyMedium)
                }
                Button(onClick = onAdd) {
                    Text("新增")
                }
            }

            Text(
                text = "本 App 不会长期在后台运行。长周期提醒建议添加到系统日历；当天或近期响铃可使用系统闹钟。",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

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
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        alarm.remainingText(),
                        style = MaterialTheme.typography.titleMedium,
                        color = if (alarm.enabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                TextButton(onClick = onToggleEnabled) {
                    Text(if (alarm.enabled) "停用" else "启用")
                }
            }
            Text("下次到期：${alarm.nextDueDate} ${alarm.reminderTime}")
            Text(alarm.cycleLabel(), color = MaterialTheme.colorScheme.onSurfaceVariant)
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
