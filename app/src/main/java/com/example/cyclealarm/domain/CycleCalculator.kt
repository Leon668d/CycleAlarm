package com.example.cyclealarm.domain

import com.example.cyclealarm.model.AnchorMode
import com.example.cyclealarm.model.CompletionRecord
import com.example.cyclealarm.model.CycleUnit
import com.example.cyclealarm.model.LoopAlarm
import java.time.LocalDate
import java.time.temporal.ChronoUnit

object CycleCalculator {
    fun calculateNextDueDate(
        anchorDate: LocalDate,
        cycleValue: Int,
        cycleUnit: CycleUnit
    ): LocalDate {
        require(cycleValue > 0) { "cycleValue must be greater than 0" }
        return when (cycleUnit) {
            CycleUnit.DAY -> anchorDate.plusDays(cycleValue.toLong())
            CycleUnit.WEEK -> anchorDate.plusWeeks(cycleValue.toLong())
            CycleUnit.MONTH -> anchorDate.plusMonths(cycleValue.toLong())
            CycleUnit.YEAR -> anchorDate.plusYears(cycleValue.toLong())
        }
    }

    fun calculateRemainingDays(nextDueDate: LocalDate, today: LocalDate = LocalDate.now()): Long {
        return ChronoUnit.DAYS.between(today, nextDueDate)
    }

    fun completeAlarm(alarm: LoopAlarm, today: LocalDate = LocalDate.now()): LoopAlarm {
        val nextDueDate = when (alarm.anchorMode) {
            AnchorMode.AFTER_COMPLETION -> calculateNextDueDate(
                anchorDate = today,
                cycleValue = alarm.cycleValue,
                cycleUnit = alarm.cycleUnit
            )

            AnchorMode.FIXED_DATE -> calculateNextDueDate(
                anchorDate = LocalDate.parse(alarm.nextDueDate),
                cycleValue = alarm.cycleValue,
                cycleUnit = alarm.cycleUnit
            )
        }

        val completionRecord = CompletionRecord(
            completedDate = today.toString(),
            completedAt = System.currentTimeMillis()
        )

        return alarm.copy(
            lastCompletedDate = today.toString(),
            nextDueDate = nextDueDate.toString(),
            completionHistory = listOf(completionRecord) + alarm.completionHistory,
            updatedAt = System.currentTimeMillis()
        )
    }
}
