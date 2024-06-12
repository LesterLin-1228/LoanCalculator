package org.example.loancalculator.service

import org.example.loancalculator.dao.LoanInfoDao
import org.example.loancalculator.dao.LoanInterestRateDao
import org.example.loancalculator.dto.LoanInfoDto
import org.example.loancalculator.entity.LoanInfo
import org.example.loancalculator.entity.LoanInterestRate
import org.example.loancalculator.model.LoanRequest
import org.example.loancalculator.model.LoanResponse
import org.example.loancalculator.response.LoanDetailsResponse
import org.example.loancalculator.response.Response
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class LoanInfoService(
    @Autowired private val loanInfoDao: LoanInfoDao,
    @Autowired private val loanInterestRateDao: LoanInterestRateDao,
    @Autowired private val interestRateService: InterestRateService,
    @Autowired private val loanCalculatorService: LoanCalculatorService,
) {

    fun createLoan(loanInfoDto: LoanInfoDto): Response<String> {
        val existingLoanInfo = loanInfoDao.findByLoanAccount(loanInfoDto.loanAccount)
        return if (existingLoanInfo != null) {
            Response("帳號重複", HttpStatus.CONFLICT)
        } else {
            val startDate = LocalDate.now()
            val endDate = startDate.plusMonths(loanInfoDto.loanTerm.toLong())
            // 儲存貸款帳號
            val loanInfo = LoanInfo(
                loanAccount = loanInfoDto.loanAccount,
                startDate = startDate,
                endDate = endDate,
                repaymentDueDay = startDate.dayOfMonth,
                principalBalance = loanInfoDto.loanAmount,
                loanAmount = loanInfoDto.loanAmount,
                loanTerm = loanInfoDto.loanTerm
            )
            val saveLoan = loanInfoDao.save(loanInfo)

            // 儲存貸款利率
            val loanInterestRate = LoanInterestRate(
                loanAccount = saveLoan.loanAccount,
                rateStartDate = startDate,
                rateDifference = loanInfoDto.rateDifference
            )
            loanInterestRateDao.save(loanInterestRate)

            Response("成功建立", HttpStatus.CREATED)
        }
    }

    fun getLoanDetails(loanAccount: String): Response<LoanDetailsResponse> {
        val loanInfo = loanInfoDao.findByLoanAccount(loanAccount) ?: throw Exception("貸款帳號不存在")

        val loanResponse = prepareLoanRequest(loanInfo)

        // 初始化剩餘本金和下期還款金額
        var principalBalance = loanInfo.loanAmount
        var nextRepayment: Int? = null

        // 遍歷所有期數的還款資訊
        for (payment in loanResponse.payments) {
            if (principalBalance == loanInfo.principalBalance) {
                nextRepayment = payment.monthlyPayment
                break
            }
            principalBalance -= payment.principalForPeriod
        }

        val loanDetailsResponse = LoanDetailsResponse(
            principalBalance = loanInfo.principalBalance,
            nextRepayment = nextRepayment ?: throw Exception("查無下期還款金額")
        )

        return Response("成功獲取貸款詳情", HttpStatus.OK, loanDetailsResponse)
    }

    private fun prepareLoanRequest(loanInfo: LoanInfo): LoanResponse {
        // 獲取最新的基礎利率
        val baseRate = interestRateService.getLatestInterestRate()?.baseRate ?: throw Exception("查無最新基礎利率")
        // 獲取利率差
        val rateDifference =
            loanInterestRateDao.findByLoanAccount(loanInfo.loanAccount)?.rateDifference ?: throw Exception("查無利率差")

        val loanRequest = LoanRequest(
            loanAmount = loanInfo.loanAmount / 10000,
            loanPeriod = loanInfo.loanTerm,
            isSingleRate = true,
            interestRate = baseRate + rateDifference,
            gracePeriod = 0,
            ratePeriods = null,
            relatedFees = 0
        )
        // 傳回計算後的貸款資訊
        return loanCalculatorService.calculateLoan(loanRequest)
    }
}