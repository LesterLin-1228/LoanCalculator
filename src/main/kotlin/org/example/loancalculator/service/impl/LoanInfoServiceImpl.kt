package org.example.loancalculator.service.impl

import org.example.loancalculator.dao.LoanInfoDao
import org.example.loancalculator.dao.LoanInterestRateDao
import org.example.loancalculator.dao.RepaymentRecordDao
import org.example.loancalculator.dto.loanInfo.LoanDetailsDto
import org.example.loancalculator.dto.loanInfo.LoanInfoDto
import org.example.loancalculator.dto.loanInfo.LoanInfoReq
import org.example.loancalculator.dto.loanInfo.LoanStatisticsDto
import org.example.loancalculator.entity.LoanInfo
import org.example.loancalculator.entity.LoanInterestRate
import org.example.loancalculator.service.LoanCalculatorService
import org.example.loancalculator.service.LoanInfoService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class LoanInfoServiceImpl(
    @Autowired private val loanInfoDao: LoanInfoDao,
    @Autowired private val loanInterestRateDao: LoanInterestRateDao,
    @Autowired private val loanCalculatorService: LoanCalculatorService,
    @Autowired private val repaymentRecordDao: RepaymentRecordDao,
) : LoanInfoService {

    override fun createLoan(loanInfoReq: LoanInfoReq): LoanInfoDto {
        val existingLoanInfo = loanInfoDao.findByLoanAccount(loanInfoReq.loanAccount)
        if (existingLoanInfo != null) {
            throw ResponseStatusException(HttpStatus.CONFLICT, "帳號已存在")
        } else {
            val endDate = loanInfoReq.startDate.plusMonths(loanInfoReq.loanTerm.toLong())

            // 儲存貸款帳號
            val loanInfo = LoanInfo(
                loanAccount = loanInfoReq.loanAccount,
                startDate = loanInfoReq.startDate,
                endDate = endDate,
                repaymentDueDay = loanInfoReq.startDate.dayOfMonth,
                principalBalance = loanInfoReq.loanAmount,
                loanAmount = loanInfoReq.loanAmount,
                loanTerm = loanInfoReq.loanTerm,
                totalAmountRepayment = 0,
                totalPrincipalRepayment = 0,
                totalInterestRepayment = 0
            )
            val saveLoan = loanInfoDao.save(loanInfo)

            // 儲存貸款利率
            val loanInterestRate = LoanInterestRate(
                loanAccount = saveLoan.loanAccount,
                rateStartDate = loanInfoReq.startDate,
                rateDifference = loanInfoReq.rateDifference
            )
            loanInterestRateDao.save(loanInterestRate)

            // 返回
            val loanInfoDto = LoanInfoDto(
                loanAccount = loanInfoReq.loanAccount,
                loanAmount = loanInfoReq.loanAmount,
                loanTerm = loanInfoReq.loanTerm,
                rateDifference = loanInfoReq.rateDifference,
                startDate = loanInfoReq.startDate,
                endDate = endDate,
                repaymentDueDay = loanInfoReq.startDate.dayOfMonth
            )

            return loanInfoDto
        }
    }

    override fun getLoanDetails(loanAccount: String): LoanDetailsDto {
        val loanInfo = loanInfoDao.findByLoanAccount(loanAccount) ?: throw ResponseStatusException(
            HttpStatus.NOT_FOUND,
            "貸款帳號不存在"
        )

        val loanResponse = loanCalculatorService.prepareLoanRequest(loanInfo)

        // 初始化剩餘本金
        val principalBalance = loanInfo.principalBalance

        // 查詢下期還款資訊
        val nextRepaymentInfo =
            loanResponse.payments.firstOrNull { it.principalBalance < principalBalance }
                ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "查無下期還款資訊")

        val lastRepaymentDate = repaymentRecordDao.findLatestRepaymentDateByLoanAccount(loanAccount)

        val nextRepaymentDate =
            loanCalculatorService.calculateNextRepaymentDate(
                loanInfo.startDate,
                lastRepaymentDate,
                loanInfo.repaymentDueDay
            )

        val loanDetailsDto = LoanDetailsDto(
            principalBalance = principalBalance,
            nextRepayment = nextRepaymentInfo.monthlyPayment,
            nextRepaymentDate = nextRepaymentDate
        )

        return loanDetailsDto
    }

    override fun getLoanStatistics(): LoanStatisticsDto {
        val totalLoanAmount = loanInfoDao.getTotalLoanAmount()
        val totalRepaidAmount = loanInfoDao.getTotalRepaidAmount()
        val totalInterestReceived = loanInfoDao.getTotalInterestReceived()

        return LoanStatisticsDto(
            totalLoanAmount = totalLoanAmount,
            totalRepaidAmount = totalRepaidAmount,
            totalInterestReceived = totalInterestReceived
        )
    }
}