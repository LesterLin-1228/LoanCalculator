package org.example.loancalculator.dao

import org.example.loancalculator.entity.InterestRate
import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDate

interface InterestRateDao : JpaRepository<InterestRate, LocalDate> {
    // 找出最新日期
    fun findFirstByOrderByDateDesc(): InterestRate?
    // 是否存在相同日期
    fun existsByDate(date: LocalDate): Boolean
}