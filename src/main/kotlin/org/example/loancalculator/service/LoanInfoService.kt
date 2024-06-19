package org.example.loancalculator.service

import org.example.loancalculator.dto.loanInfo.LoanDetailsDto
import org.example.loancalculator.dto.loanInfo.LoanInfoDto
import org.example.loancalculator.dto.loanInfo.LoanInfoReq
import org.example.loancalculator.dto.loanInfo.LoanStatisticsDto

interface LoanInfoService {
    // 建立貸款
    fun createLoan(loanInfoReq: LoanInfoReq): LoanInfoDto
    // 取的貸款詳情
    fun getLoanDetails(loanAccount: String): LoanDetailsDto
    // 取得貸款統計
    fun getLoanStatistics(): LoanStatisticsDto
}