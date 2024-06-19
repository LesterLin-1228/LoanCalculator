package org.example.loancalculator.service

import org.example.loancalculator.dto.interestRate.AdjustInterestRateReq
import org.example.loancalculator.dto.interestRate.CreateInterestRateReq
import org.example.loancalculator.dto.interestRate.InterestRateDto

interface InterestRateService {
    // 建立基礎利率表
    fun createInterestRate(createInterestRateReq: CreateInterestRateReq): InterestRateDto
    // 取得最新基礎利率
    fun getLatestInterestRate(): InterestRateDto
    // 調整基礎利率
    fun adjustInterestRate(adjustInterestRateReq: AdjustInterestRateReq): InterestRateDto
}