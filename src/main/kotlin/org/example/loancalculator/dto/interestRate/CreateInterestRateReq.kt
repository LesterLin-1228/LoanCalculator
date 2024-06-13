package org.example.loancalculator.dto.interestRate

import java.time.LocalDate

data class CreateInterestRateReq(
    val date: LocalDate? = null,
    val baseRate: Double? = null
)
