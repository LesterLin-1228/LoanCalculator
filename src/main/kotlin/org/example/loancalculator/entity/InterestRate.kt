package org.example.loancalculator.entity

import jakarta.persistence.Entity
import jakarta.persistence.Id
import java.time.LocalDate

@Entity
data class InterestRate(
    @Id
    val date: LocalDate = LocalDate.now(),
    val baseRate: Double = 2.0
)