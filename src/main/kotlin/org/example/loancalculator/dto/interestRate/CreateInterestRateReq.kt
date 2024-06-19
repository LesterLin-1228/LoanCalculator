package org.example.loancalculator.dto.interestRate

import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import java.time.LocalDate

data class CreateInterestRateReq(
    @field:NotNull(message = "日期不能為空值")
    val date: LocalDate = LocalDate.now(),

    @field:Positive(message = "基礎利率必須為正數")
    val baseRate: Double
)
