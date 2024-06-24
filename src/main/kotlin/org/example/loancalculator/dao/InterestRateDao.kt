package org.example.loancalculator.dao

import org.example.loancalculator.entity.InterestRate
import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDate

interface InterestRateDao : JpaRepository<InterestRate, LocalDate> {
    // 找出指定日期的最新基礎利率
    fun findByDate(date: LocalDate): InterestRate?
    // 找出指定日期之前的最新基礎利率
    fun findFirstByDateBeforeOrderByDateDesc(date: LocalDate): InterestRate?
    // 是否存在相同日期
    fun existsByDate(date: LocalDate): Boolean
}