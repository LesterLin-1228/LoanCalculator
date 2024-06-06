package org.example.loancalculator.dto

import java.time.LocalDate

data class InterestRateDto (
    val date: LocalDate? = null,
    val baseRate: Double? = null
)
