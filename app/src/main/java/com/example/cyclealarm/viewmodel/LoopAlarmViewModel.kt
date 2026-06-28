package com.example.cyclealarm.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import com.example.cyclealarm.data.LocalStorage
import com.example.cyclealarm.data.LoopAlarmRepository
import com.example.cyclealarm.model.AnchorMode
import com.example.cyclealarm.model.CycleUnit
import com.example.cyclealarm.model.LoopAlarm
import com.example.cyclealarm.model.LoopAlarmInput
import java.time.LocalDate

data class LoopAlarmUiState(
    val alarms: List<LoopAlarm> = emptyList(),
    val message: String? = null
)

class LoopAlarmViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = LoopAlarmRepository(LocalStorage(application))

    var uiState by mutableStateOf(LoopAlarmUiState())
        private set

    init {
        refresh()
    }

    fun refresh() {
        uiState = uiState.copy(alarms = repository.getAll())
    }

    fun getAlarm(id: Long): LoopAlarm? = repository.getById(id)

    fun create(input: LoopAlarmInput): Result<LoopAlarm> {
        return runCatching { repository.create(input) }
            .onSuccess {
                refresh()
                showMessage("已创建周期任务")
            }
    }

    fun createTemplate(template: DefaultTemplate): Result<LoopAlarm> {
        return create(template.toInput())
    }

    fun update(id: Long, input: LoopAlarmInput): Result<LoopAlarm> {
        return runCatching {
            repository.update(id, input) ?: error("未找到任务")
        }.onSuccess {
            refresh()
            showMessage("已保存。请手动检查旧的系统日历或系统闹钟提醒。")
        }
    }

    fun delete(id: Long): Boolean {
        val deleted = repository.delete(id)
        refresh()
        if (deleted) showMessage("已删除。若已添加系统提醒，请手动到系统日历或闹钟删除。")
        return deleted
    }

    fun complete(id: Long): LoopAlarm? {
        val completed = repository.complete(id)
        refresh()
        return completed
    }

    fun setEnabled(id: Long, enabled: Boolean) {
        repository.setEnabled(id, enabled)
        refresh()
        if (enabled) {
            showMessage("已启用任务")
        } else {
            showMessage("已停用 App 内任务。系统日历或闹钟提醒需要手动检查。")
        }
    }

    fun clearMessage() {
        uiState = uiState.copy(message = null)
    }

    private fun showMessage(message: String) {
        uiState = uiState.copy(message = message)
    }
}

enum class DefaultTemplate(
    val label: String,
    private val titleValue: String,
    private val cycleValueValue: Int,
    private val cycleUnitValue: CycleUnit,
    private val anchorModeValue: AnchorMode
) {
    SIM_KEEP_ALIVE("SIM 卡保活", "SIM 卡保活", 180, CycleUnit.DAY, AnchorMode.AFTER_COMPLETION),
    FILTER("滤芯更换", "滤芯更换", 90, CycleUnit.DAY, AnchorMode.AFTER_COMPLETION),
    DOMAIN("域名续费", "域名续费", 1, CycleUnit.YEAR, AnchorMode.FIXED_DATE);

    fun toInput(): LoopAlarmInput {
        return LoopAlarmInput(
            title = titleValue,
            note = null,
            cycleValue = cycleValueValue,
            cycleUnit = cycleUnitValue,
            anchorMode = anchorModeValue,
            startDate = LocalDate.now().toString(),
            reminderTime = "09:00",
            enabled = true
        )
    }
}
