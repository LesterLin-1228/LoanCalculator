package org.example.loancalculator.service

import org.example.loancalculator.dao.LoanInfoDao
import org.example.loancalculator.dao.LoanInterestRateDao
import org.example.loancalculator.dto.LoanInfoDto
import org.example.loancalculator.entity.LoanInfo
import org.example.loancalculator.entity.LoanInterestRate
import org.example.loancalculator.model.LoanRequest
import org.example.loancalculator.result.LoanDetailsResult
import org.example.loancalculator.result.Result
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class LoanInfoService(
    @Autowired private val loanInfoDao: LoanInfoDao,
    @Autowired private val loanInterestRateDao: LoanInterestRateDao,
    @Autowired private val interestRateService: InterestRateService,
    private val loanCalculatorService: LoanCalculatorService,
) {

    fun createLoan(loanInfoDto: LoanInfoDto): Result<Any?> {
        val existingLoanInfo = loanInfoDao.findByLoanAccount(loanInfoDto.loanAccount)
        return if (existingLoanInfo != null) {
            Result("帳號重複", HttpStatus.CONFLICT)
        } else {
            val startDate = LocalDate.now()
            val endDate = startDate.plusMonths(loanInfoDto.loanTerm.toLong())
            // 儲存貸款帳號
            val loanInfo = LoanInfo(
                loanAccount = loanInfoDto.loanAccount,
                startDate = startDate,
                endDate = endDate,
                paymentDueDay = startDate.dayOfMonth,
                remainingPrincipal = loanInfoDto.loanAmount,
                loanAmount = loanInfoDto.loanAmount,
                loanTerm = loanInfoDto.loanTerm
            )
            val saveLoan = loanInfoDao.save(loanInfo)

            // 儲存貸款利率
            val loanInterestRate = LoanInterestRate(
                loanAccount = saveLoan.loanAccount,
                rateStartDate = loanInfoDto.rateStartDate,
                rateDifference = loanInfoDto.rateDifference
            )
            loanInterestRateDao.save(loanInterestRate)

            Result("成功建立", HttpStatus.CREATED)
        }
    }

    fun getLoanDetails(loanAccount: String): Result<LoanDetailsResult> {
        val loanInfo = loanInfoDao.findByLoanAccount(loanAccount) ?: throw Exception("貸款帳號不存在")

        // 獲取最新的基礎利率
        val baseRate = interestRateService.getLatestInterestRate()?.baseRate ?: throw Exception("查無最新基礎利率")
        // 獲取利率差
        val rateDifference =
            loanInterestRateDao.findByLoanAccount(loanAccount)?.rateDifference ?: throw Exception("查無利率差")

        val loanRequest = LoanRequest(
            loanAmount = loanInfo.loanAmount,
            loanPeriod = loanInfo.loanTerm,
            isSingleRate = true,
            interestRate = baseRate + rateDifference,
            gracePeriod = 0,
            ratePeriods = null,
            relatedFees = 0
        )

        // 計算還款資訊
        val loanResponse = loanCalculatorService.calculateLoan(loanRequest)

        // 初始化剩餘本金和下期還款金額
        var remainingPrincipal = loanInfo.loanAmount
        var nextRepayment: Int? = null

        // 遍歷所有期數的還款資訊
        for (payment in loanResponse.payments) {
            if (remainingPrincipal == loanInfo.remainingPrincipal) {
                nextRepayment = payment.monthlyPayment
                break
            }
            remainingPrincipal -= payment.principalForPeriod
        }

        val loanDetailsResult =  LoanDetailsResult(
            remainingPrincipal = loanInfo.remainingPrincipal,
            nextRepayment = nextRepayment ?: throw Exception("查無下期還款金額")
        )

        return Result("成功獲取貸款詳情", HttpStatus.OK, loanDetailsResult)
    }

}