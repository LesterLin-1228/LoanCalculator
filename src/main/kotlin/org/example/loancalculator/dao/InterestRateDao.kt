package org.example.loancalculator.dao

import org.example.loancalculator.entity.InterestRate
import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDate

interface InterestRateDao : JpaRepository<InterestRate, LocalDate> {
    fun findFirstByOrderByDateDesc(): InterestRate?
    fun existsByDate(date: LocalDate): Boolean
}