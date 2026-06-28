package com.example.cyclealarm.domain

import com.example.cyclealarm.model.AnchorMode
import com.example.cyclealarm.model.CycleUnit
import com.example.cyclealarm.model.LoopAlarm
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

class CycleCalculatorTest {
    @Test
    fun calculateNextDueDate_addsDaysWeeksMonthsAndYears() {
        val anchor = LocalDate.parse("2026-06-28")

        assertEquals("2026-12-25", CycleCalculator.calculateNextDueDate(anchor, 180, CycleUnit.DAY).toString())
        assertEquals("2026-07-12", CycleCalculator.calculateNextDueDate(anchor, 2, CycleUnit.WEEK).toString())
        assertEquals("2026-09-28", CycleCalculator.calculateNextDueDate(anchor, 3, CycleUnit.MONTH).toString())
        assertEquals("2027-06-28", CycleCalculator.calculateNextDueDate(anchor, 1, CycleUnit.YEAR).toString())
    }

    @Test
    fun completeAlarm_afterCompletionUsesTodayAsAnchor() {
        val alarm = sampleAlarm(
            anchorMode = AnchorMode.AFTER_COMPLETION,
            nextDueDate = "2026-07-01"
        )

        val completed = CycleCalculator.completeAlarm(alarm, LocalDate.parse("2026-06-28"))

        assertEquals("2026-06-28", completed.lastCompletedDate)
        assertEquals("2026-12-25", completed.nextDueDate)
        assertEquals(1, completed.completionHistory.size)
    }

    @Test
    fun completeAlarm_fixedDateUsesPreviousDueDateAsAnchor() {
        val alarm = sampleAlarm(
            anchorMode = AnchorMode.FIXED_DATE,
            cycleValue = 1,
            cycleUnit = CycleUnit.YEAR,
            nextDueDate = "2027-06-01"
        )

        val completed = CycleCalculator.completeAlarm(alarm, LocalDate.parse("2027-05-20"))

        assertEquals("2027-05-20", completed.lastCompletedDate)
        assertEquals("2028-06-01", completed.nextDueDate)
    }

    @Test
    fun calculateRemainingDays_returnsNegativeForOverdue() {
        val days = CycleCalculator.calculateRemainingDays(
            nextDueDate = LocalDate.parse("2026-06-25"),
            today = LocalDate.parse("2026-06-28")
        )

        assertEquals(-3, days)
    }

    private fun sampleAlarm(
        anchorMode: AnchorMode,
        cycleValue: Int = 180,
        cycleUnit: CycleUnit = CycleUnit.DAY,
        nextDueDate: String
    ): LoopAlarm {
        return LoopAlarm(
            id = 1L,
            title = "SIM 卡保活",
            cycleValue = cycleValue,
            cycleUnit = cycleUnit,
            anchorMode = anchorMode,
            startDate = "2026-01-01",
            nextDueDate = nextDueDate,
            reminderTime = "09:00"
        )
    }
}
