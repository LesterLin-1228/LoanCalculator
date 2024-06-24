package org.example.loancalculator.dto.interestRate

import jakarta.validation.constraints.NotNull
import java.time.LocalDate

data class AdjustInterestRateReq(
    @field:NotNull(message = "調整利率不能為空值")
    val adjustmentRate: Double,
    @field:NotNull(message = "生效日不能為空")
    val effectiveDate: LocalDate
)
