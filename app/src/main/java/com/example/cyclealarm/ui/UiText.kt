package com.example.cyclealarm.ui

import com.example.cyclealarm.domain.CycleCalculator
import com.example.cyclealarm.model.AnchorMode
import com.example.cyclealarm.model.CycleUnit
import com.example.cyclealarm.model.LoopAlarm
import java.time.LocalDate

fun CycleUnit.label(): String = when (this) {
    CycleUnit.DAY -> "天"
    CycleUnit.WEEK -> "周"
    CycleUnit.MONTH -> "月"
    CycleUnit.YEAR -> "年"
}

fun AnchorMode.label(): String = when (this) {
    AnchorMode.AFTER_COMPLETION -> "完成后重新计时"
    AnchorMode.FIXED_DATE -> "固定周期循环"
}

fun LoopAlarm.cycleLabel(): String = "每 $cycleValue ${cycleUnit.label()} · ${anchorMode.label()}"

fun LoopAlarm.remainingText(today: LocalDate = LocalDate.now()): String {
    val days = CycleCalculator.calculateRemainingDays(LocalDate.parse(nextDueDate), today)
    return when {
        !enabled -> "已停用"
        days > 0 -> "剩余 $days 天"
        days == 0L -> "今天到期"
        else -> "已逾期 ${-days} 天"
    }
}
