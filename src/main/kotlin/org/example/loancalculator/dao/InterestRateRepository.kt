package org.example.loancalculator.dao

import org.example.loancalculator.entity.InterestRate
import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDate

interface InterestRateRepository : JpaRepository<InterestRate, LocalDate> {
}