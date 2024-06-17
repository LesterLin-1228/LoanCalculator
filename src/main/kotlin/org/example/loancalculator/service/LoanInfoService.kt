package org.example.loancalculator.service

import org.example.loancalculator.dao.LoanInfoDao
import org.example.loancalculator.dao.LoanInterestRateDao
import org.example.loancalculator.dto.loanInfo.LoanDetailsDto
import org.example.loancalculator.dto.loanInfo.LoanInfoDto
import org.example.loancalculator.dto.loanInfo.LoanInfoReq
import org.example.loancalculator.entity.LoanInfo
import org.example.loancalculator.entity.LoanInterestRate
import org.example.loancalculator.model.LoanRequest
import org.example.loancalculator.model.LoanResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.time.LocalDate

@Service
class LoanInfoService(
    @Autowired private val loanInfoDao: LoanInfoDao,
    @Autowired private val loanInterestRateDao: LoanInterestRateDao,
    @Autowired private val interestRateService: InterestRateService,
    @Autowired private val loanCalculatorService: LoanCalculatorService,
) {

    fun createLoan(loanInfoReq: LoanInfoReq): LoanInfoDto {
        val existingLoanInfo = loanInfoDao.findByLoanAccount(loanInfoReq.loanAccount)
        if (existingLoanInfo != null) {
            throw ResponseStatusException(HttpStatus.CONFLICT, "帳號已存在")
        } else {
            val startDate = LocalDate.now()
            val endDate = startDate.plusMonths(loanInfoReq.loanTerm.toLong())
            // 儲存貸款帳號
            val loanInfo = LoanInfo(
                loanAccount = loanInfoReq.loanAccount,
                startDate = startDate,
                endDate = endDate,
                repaymentDueDay = startDate.dayOfMonth,
                principalBalance = loanInfoReq.loanAmount,
                loanAmount = loanInfoReq.loanAmount,
                loanTerm = loanInfoReq.loanTerm
            )
            val saveLoan = loanInfoDao.save(loanInfo)

            // 儲存貸款利率
            val loanInterestRate = LoanInterestRate(
                loanAccount = saveLoan.loanAccount,
                rateStartDate = startDate,
                rateDifference = loanInfoReq.rateDifference
            )
            loanInterestRateDao.save(loanInterestRate)

            // 返回
            val loanInfoDto = LoanInfoDto(
                loanAccount = loanInfoReq.loanAccount,
                loanAmount = loanInfoReq.loanAmount,
                loanTerm = loanInfoReq.loanTerm,
                rateDifference = loanInfoReq.rateDifference,
                startDate = startDate,
                endDate = endDate,
                repaymentDueDay = startDate.dayOfMonth
            )

            return loanInfoDto
        }
    }

    fun getLoanDetails(loanAccount: String): LoanDetailsDto {
        val loanInfo = loanInfoDao.findByLoanAccount(loanAccount) ?: throw ResponseStatusException(
            HttpStatus.NOT_FOUND,
            "貸款帳號不存在"
        )

        val loanResponse = prepareLoanRequest(loanInfo)

        // 初始化剩餘本金
        val principalBalance = loanInfo.principalBalance

        // 查詢下期還款資訊
        val nextRepaymentInfo =
            loanResponse.payments.firstOrNull { it.principalBalance < principalBalance }
                ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "查無下期還款資訊")

        val nextRepaymentDate =
            calculateNextRepaymentDate(loanInfo.startDate, nextRepaymentInfo.period, loanInfo.repaymentDueDay)

        val loanDetailsDto = LoanDetailsDto(
            principalBalance = principalBalance,
            nextRepayment = nextRepaymentInfo.monthlyPayment,
            nextRepaymentDate = nextRepaymentDate
        )

        return loanDetailsDto
    }

    fun prepareLoanRequest(loanInfo: LoanInfo): LoanResponse {

        val currentInterestRate = getCurrentInterestRate(loanInfo.loanAccount)

        val loanRequest = LoanRequest(
            loanAmount = loanInfo.loanAmount / 10000,
            loanPeriod = loanInfo.loanTerm,
            isSingleRate = true,
            interestRate = currentInterestRate,
            gracePeriod = 0,
            ratePeriods = null,
            relatedFees = 0
        )
        // 傳回計算後的貸款資訊
        return loanCalculatorService.calculateLoan(loanRequest)
    }

    // 計算下次還款日
    fun calculateNextRepaymentDate(startDate: LocalDate, period: Int, repaymentDueDay: Int): LocalDate {
        val repaymentMonth = startDate.plusMonths(period.toLong())
        return LocalDate.of(repaymentMonth.year, repaymentMonth.month, repaymentDueDay)
    }

    // 根據貸款帳號計算當前利率
    fun getCurrentInterestRate(loanAccount: String): Double {
        // 獲取最新的基礎利率
        val baseRate = interestRateService.getLatestInterestRate().baseRate ?: throw Exception("查無最新基礎利率")
        // 獲取利率差
        val rateDifference =
            loanInterestRateDao.findByLoanAccount(loanAccount)?.rateDifference ?: throw Exception("查無利率差")
        return baseRate + rateDifference
    }
}