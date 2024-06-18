package org.example.loancalculator.dto.interestRate

import java.time.LocalDate

data class AdjustInterestRateReq(
    val adjustmentDate: LocalDate? = null,
    val adjustmentRate: Double
)
