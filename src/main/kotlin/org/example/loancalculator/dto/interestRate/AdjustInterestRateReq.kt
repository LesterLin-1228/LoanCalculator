package org.example.loancalculator.dto.interestRate

import jakarta.validation.constraints.NotNull

data class AdjustInterestRateReq(
    @field:NotNull(message = "調整利率不能為空值")
    val adjustmentRate: Double
)
