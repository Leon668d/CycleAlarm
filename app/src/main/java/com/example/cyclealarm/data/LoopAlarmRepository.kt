package com.example.cyclealarm.data

import com.example.cyclealarm.domain.CycleCalculator
import com.example.cyclealarm.model.LoopAlarm
import com.example.cyclealarm.model.LoopAlarmInput
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.time.LocalDate

class LoopAlarmRepository(
    private val storage: LocalStorage
) {
    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
        prettyPrint = true
    }

    fun getAll(): List<LoopAlarm> {
        return decode(storage.readAlarmsJson()).sortedWith(
            compareByDescending<LoopAlarm> { it.enabled }
                .thenBy { it.nextDueDate }
                .thenBy { it.createdAt }
        )
    }

    fun getById(id: Long): LoopAlarm? = getAll().firstOrNull { it.id == id }

    fun create(input: LoopAlarmInput): LoopAlarm {
        validate(input)
        val now = System.currentTimeMillis()
        val nextDueDate = CycleCalculator.calculateNextDueDate(
            anchorDate = LocalDate.parse(input.startDate),
            cycleValue = input.cycleValue,
            cycleUnit = input.cycleUnit
        )

        val alarm = LoopAlarm(
            id = generateId(now),
            title = input.title.trim(),
            note = input.note?.trim()?.ifBlank { null },
            cycleValue = input.cycleValue,
            cycleUnit = input.cycleUnit,
            anchorMode = input.anchorMode,
            startDate = input.startDate,
            nextDueDate = nextDueDate.toString(),
            reminderTime = input.reminderTime,
            enabled = input.enabled,
            createdAt = now,
            updatedAt = now
        )

        persist(getAll() + alarm)
        return alarm
    }

    fun update(id: Long, input: LoopAlarmInput): LoopAlarm? {
        validate(input)
        var updatedAlarm: LoopAlarm? = null
        val updatedList = getAll().map { alarm ->
            if (alarm.id != id) return@map alarm

            val shouldRecalculateDueDate =
                alarm.cycleValue != input.cycleValue ||
                    alarm.cycleUnit != input.cycleUnit ||
                    alarm.startDate != input.startDate ||
                    alarm.anchorMode != input.anchorMode

            val nextDueDate = if (shouldRecalculateDueDate) {
                CycleCalculator.calculateNextDueDate(
                    anchorDate = LocalDate.parse(input.startDate),
                    cycleValue = input.cycleValue,
                    cycleUnit = input.cycleUnit
                ).toString()
            } else {
                alarm.nextDueDate
            }

            alarm.copy(
                title = input.title.trim(),
                note = input.note?.trim()?.ifBlank { null },
                cycleValue = input.cycleValue,
                cycleUnit = input.cycleUnit,
                anchorMode = input.anchorMode,
                startDate = input.startDate,
                nextDueDate = nextDueDate,
                reminderTime = input.reminderTime,
                enabled = input.enabled,
                updatedAt = System.currentTimeMillis()
            ).also { updatedAlarm = it }
        }

        if (updatedAlarm != null) persist(updatedList)
        return updatedAlarm
    }

    fun delete(id: Long): Boolean {
        val current = getAll()
        val updated = current.filterNot { it.id == id }
        if (updated.size == current.size) return false
        persist(updated)
        return true
    }

    fun complete(id: Long): LoopAlarm? {
        var completed: LoopAlarm? = null
        val updated = getAll().map { alarm ->
            if (alarm.id == id) {
                CycleCalculator.completeAlarm(alarm).also { completed = it }
            } else {
                alarm
            }
        }

        if (completed != null) persist(updated)
        return completed
    }

    fun setEnabled(id: Long, enabled: Boolean): LoopAlarm? {
        var changed: LoopAlarm? = null
        val updated = getAll().map { alarm ->
            if (alarm.id == id) {
                alarm.copy(enabled = enabled, updatedAt = System.currentTimeMillis()).also { changed = it }
            } else {
                alarm
            }
        }
        if (changed != null) persist(updated)
        return changed
    }

    fun exportJson(): String = json.encodeToString(getAll())

    fun importJson(rawJson: String): Boolean {
        val imported = decode(rawJson)
        persist(imported)
        return true
    }

    private fun validate(input: LoopAlarmInput) {
        require(input.title.isNotBlank()) { "标题不能为空" }
        require(input.cycleValue > 0) { "周期数值必须大于 0" }
        LocalDate.parse(input.startDate)
        java.time.LocalTime.parse(input.reminderTime)
    }

    private fun decode(rawJson: String): List<LoopAlarm> {
        return runCatching { json.decodeFromString<List<LoopAlarm>>(rawJson) }.getOrDefault(emptyList())
    }

    private fun persist(alarms: List<LoopAlarm>) {
        storage.writeAlarmsJson(json.encodeToString(alarms))
    }

    private fun generateId(now: Long): Long {
        val existingIds = getAll().map { it.id }.toSet()
        var candidate = now
        while (candidate in existingIds) candidate += 1
        return candidate
    }
}
