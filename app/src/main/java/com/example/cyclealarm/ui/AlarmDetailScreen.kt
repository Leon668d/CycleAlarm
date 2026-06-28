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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AlarmDetailScreen(
    alarm: LoopAlarm?,
    onBack: () -> Unit,
    onEdit: () -> Unit,
    onDelete: (Long) -> Unit,
    onComplete: (Long) -> Unit,
    onToggleEnabled: (Long, Boolean) -> Unit,
    onCalendar: (LoopAlarm) -> Unit,
    onAlarm: (LoopAlarm) -> Unit
) {
    if (alarm == null) {
        MissingAlarm(onBack)
        return
    }

    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("确定删除这个周期任务吗？") },
            text = {
                Text(
                    "此操作会删除 App 内的任务记录和完成历史。\n\n如果你之前已经添加过系统日历或系统闹钟提醒，请手动前往系统日历或系统闹钟删除对应提醒。"
                )
            },
            confirmButton = {
                Button(onClick = { onDelete(alarm.id) }) {
                    Text("删除")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("取消")
                }
            }
        )
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
                TextButton(onClick = onBack) { Text("返回") }
                TextButton(onClick = onEdit) { Text("编辑") }
            }

            Text(alarm.title, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Text(alarm.remainingText(), style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)

            InfoCard {
                DetailRow("备注", alarm.note ?: "无")
                DetailRow("周期", alarm.cycleLabel())
                DetailRow("开始日期", alarm.startDate)
                DetailRow("上次完成", alarm.lastCompletedDate ?: "尚未完成")
                DetailRow("下次到期", "${alarm.nextDueDate} ${alarm.reminderTime}")
                DetailRow("状态", if (alarm.enabled) "启用" else "停用")
            }

            Text("系统提醒", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Text(
                "长周期提醒建议添加到系统日历。系统闹钟通常更适合当天或近期响铃。",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { onCalendar(alarm) }) { Text("添加到系统日历") }
                OutlinedButton(onClick = { onAlarm(alarm) }) { Text("设置系统闹钟") }
            }

            Text("操作", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { onComplete(alarm.id) }) { Text("我已完成") }
                OutlinedButton(onClick = { onToggleEnabled(alarm.id, !alarm.enabled) }) {
                    Text(if (alarm.enabled) "停用" else "启用")
                }
                OutlinedButton(onClick = { showDeleteDialog = true }) { Text("删除") }
            }

            Text("完成历史", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            if (alarm.completionHistory.isEmpty()) {
                Text("暂无完成记录", color = MaterialTheme.colorScheme.onSurfaceVariant)
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    alarm.completionHistory.forEach { record ->
                        Text("${record.completedDate} 完成")
                    }
                }
            }
        }
    }
}

@Composable
fun CompleteResultDialog(
    alarm: LoopAlarm,
    onCalendar: () -> Unit,
    onAlarm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("已完成本轮任务") },
        text = {
            Text("下一轮到期时间为：${alarm.nextDueDate} ${alarm.reminderTime}\n\n你可以将下一轮提醒添加到系统日历，或设置系统闹钟。")
        },
        confirmButton = {
            Button(onClick = {
                onCalendar()
                onDismiss()
            }) {
                Text("添加到系统日历")
            }
        },
        dismissButton = {
            Row {
                TextButton(onClick = {
                    onAlarm()
                    onDismiss()
                }) {
                    Text("设置系统闹钟")
                }
                TextButton(onClick = onDismiss) {
                    Text("稍后再说")
                }
            }
        }
    )
}

@Composable
private fun MissingAlarm(onBack: () -> Unit) {
    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("未找到任务")
            Button(onClick = onBack) { Text("返回首页") }
        }
    }
}

@Composable
private fun InfoCard(content: @Composable Column.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            content = content
        )
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.bodyLarge)
    }
}
