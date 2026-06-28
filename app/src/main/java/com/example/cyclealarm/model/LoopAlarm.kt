package com.example.cyclealarm.model

import kotlinx.serialization.Serializable

@Serializable
data class LoopAlarm(
    val id: Long,
    val title: String,
    val note: String? = null,
    val cycleValue: Int,
    val cycleUnit: CycleUnit,
    val anchorMode: AnchorMode,
    val startDate: String,
    val lastCompletedDate: String? = null,
    val nextDueDate: String,
    val reminderTime: String,
    val remindBeforeDays: List<Int> = listOf(30, 7, 1, 0),
    val enabled: Boolean = true,
    val completionHistory: List<CompletionRecord> = emptyList(),
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Serializable
data class CompletionRecord(
    val completedDate: String,
    val completedAt: Long = System.currentTimeMillis()
)

@Serializable
enum class CycleUnit {
    DAY,
    WEEK,
    MONTH,
    YEAR
}

@Serializable
enum class AnchorMode {
    FIXED_DATE,
    AFTER_COMPLETION
}

data class LoopAlarmInput(
    val title: String,
    val note: String?,
    val cycleValue: Int,
    val cycleUnit: CycleUnit,
    val anchorMode: AnchorMode,
    val startDate: String,
    val reminderTime: String,
    val enabled: Boolean
)
