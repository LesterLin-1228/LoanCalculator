package org.example.loancalculator.dto.interestRate

import java.time.LocalDate

data class InterestRateDto (
    val date: LocalDate,
    val baseRate: Double
)
