package org.example.loancalculator.service.impl

import org.example.loancalculator.dao.LoanInfoDao
import org.example.loancalculator.dao.RepaymentRecordDao
import org.example.loancalculator.dto.repayment.EarlyPrincipalRepayDto
import org.example.loancalculator.dto.repayment.EarlyPrincipalRepayReq
import org.example.loancalculator.dto.repayment.RepaymentDto
import org.example.loancalculator.dto.repayment.RepaymentReq
import org.example.loancalculator.entity.LoanInfo
import org.example.loancalculator.entity.RepaymentRecord
import org.example.loancalculator.service.LoanCalculatorService
import org.example.loancalculator.service.RepaymentService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.time.LocalDate

@Service
class RepaymentServiceImpl(
    @Autowired private val loanInfoDao: LoanInfoDao,
    @Autowired private val repaymentRecordDao: RepaymentRecordDao,
    @Autowired private val loanCalculatorService: LoanCalculatorService,
) : RepaymentService {

    override fun repay(repaymentReq: RepaymentReq): RepaymentDto {
        val loanInfo = loanInfoDao.findByLoanAccount(repaymentReq.loanAccount) ?: throw ResponseStatusException(
            HttpStatus.NOT_FOUND,
            "貸款帳號不存在"
        )
        // 計算當期利息
        val currentInterestRate = loanCalculatorService.getCurrentInterestRate(loanInfo.loanAccount)
        val monthlyRate = currentInterestRate / 100 / 12
        val interestForPeriod = loanCalculatorService.roundToInteger(loanInfo.principalBalance * monthlyRate)
        val monthlyPayment =
            loanCalculatorService.calculateMonthlyPayment(loanInfo.loanAmount, monthlyRate, loanInfo.loanTerm)
        val principalForPeriod = monthlyPayment - interestForPeriod

        if (repaymentReq.repaymentAmount != monthlyPayment) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "還款金額必須等於每月應還金額")
        }

        // 更新本金餘額
        val principalBalance = loanInfo.principalBalance - principalForPeriod

        // 更新貸款資訊
        loanInfo.principalBalance = principalBalance
        loanInfo.totalAmountRepayment += repaymentReq.repaymentAmount
        loanInfo.totalInterestRepayment += interestForPeriod
        loanInfo.totalPrincipalRepayment += principalForPeriod
        loanInfoDao.save(loanInfo)

        // 記錄還款
        val repaymentRecord = RepaymentRecord(
            loanInfo = loanInfo,
            repaymentAmount = repaymentReq.repaymentAmount,
            repaymentDate = repaymentReq.repaymentDate,
            principalRepaid = principalForPeriod,
            interestRepaid = interestForPeriod,
            currentInterestRate = currentInterestRate
        )
        repaymentRecordDao.save(repaymentRecord)

        // 記錄還款返回Dto
        val repaymentDto = RepaymentDto(
            loanAccount = repaymentReq.loanAccount,
            repaymentAmount = repaymentReq.repaymentAmount,
            repaymentDate = repaymentReq.repaymentDate,
            principalRepaid = principalForPeriod,
            interestRepaid = interestForPeriod,
            currentInterestRate = currentInterestRate
        )

        return repaymentDto
    }

    override fun calculateEarlyPrincipalRepay(
        earlyPrincipalRepayReq: EarlyPrincipalRepayReq
    ): EarlyPrincipalRepayDto {
        val loanInfo =
            loanInfoDao.findByLoanAccount(earlyPrincipalRepayReq.loanAccount) ?: throw ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "貸款帳號不存在"
            )

        // 計算本金餘額
        val newPrincipalBalance = loanInfo.principalBalance - earlyPrincipalRepayReq.earlyPrincipalRepayment

        // 當提前還本金超過本金餘額
        if (newPrincipalBalance < 0) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "提前還本金超過本金餘額")
        }

        // 當提前還本金等於本金餘額
        if (newPrincipalBalance == 0) {
            return EarlyPrincipalRepayDto(
                principalBalance = 0,
                nextRepaymentAmount = 0
            )
        }

        // 創建一個新的 LoanInfo 物件，用於試算，避免影響資料庫
        val tempLoanInfo = LoanInfo(
            loanAccount = loanInfo.loanAccount,
            startDate = loanInfo.startDate,
            endDate = loanInfo.endDate,
            repaymentDueDay = loanInfo.repaymentDueDay,
            principalBalance = newPrincipalBalance,
            loanAmount = loanInfo.loanAmount,
            loanTerm = loanInfo.loanTerm,
            totalAmountRepayment = loanInfo.totalAmountRepayment,
            totalPrincipalRepayment = loanInfo.totalPrincipalRepayment,
            totalInterestRepayment = loanInfo.totalInterestRepayment
        )

        val loanResponse = loanCalculatorService.prepareLoanRequest(tempLoanInfo)

        val nextRepaymentInfo = loanResponse.payments.firstOrNull { it.principalBalance < newPrincipalBalance }
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "查無下期還款資訊")
        println(nextRepaymentInfo)

        val earlyPrincipalRepayDto = EarlyPrincipalRepayDto(
            principalBalance = newPrincipalBalance,
            nextRepaymentAmount = nextRepaymentInfo.monthlyPayment,
        )
        return earlyPrincipalRepayDto
    }

    override fun earlyPrincipalRepay(
        earlyPrincipalRepayReq: EarlyPrincipalRepayReq
    ): EarlyPrincipalRepayDto {
        val loanInfo =
            loanInfoDao.findByLoanAccount(earlyPrincipalRepayReq.loanAccount) ?: throw ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "貸款帳號不存在"
            )

        val newPrincipalBalance = loanInfo.principalBalance - earlyPrincipalRepayReq.earlyPrincipalRepayment

        if (newPrincipalBalance < 0) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "提前還本金超過本金餘額")
        }

        if (newPrincipalBalance == 0) {
            return EarlyPrincipalRepayDto(
                principalBalance = 0,
                nextRepaymentAmount = 0
            )
        }

        // 更新 LoanInfo
        loanInfo.principalBalance = newPrincipalBalance
        loanInfo.totalPrincipalRepayment += earlyPrincipalRepayReq.earlyPrincipalRepayment
        loanInfo.totalAmountRepayment += earlyPrincipalRepayReq.earlyPrincipalRepayment
        loanInfoDao.save(loanInfo)

        // 記錄提前還款資訊
        val currentInterestRate = loanCalculatorService.getCurrentInterestRate(loanInfo.loanAccount)
        val repaymentRecord = RepaymentRecord(
            loanInfo = loanInfo,
            repaymentAmount = earlyPrincipalRepayReq.earlyPrincipalRepayment,
            principalRepaid = earlyPrincipalRepayReq.earlyPrincipalRepayment,
            interestRepaid = 0,
            currentInterestRate = currentInterestRate,
            repaymentDate = LocalDate.now()
        )
        repaymentRecordDao.save(repaymentRecord)

        val loanResponse = loanCalculatorService.prepareLoanRequest(loanInfo)

        val nextRepaymentInfo = loanResponse.payments.firstOrNull { it.principalBalance < loanInfo.principalBalance }
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "查無下期還款資訊")

        val earlyPrincipalRepayDto = EarlyPrincipalRepayDto(
            principalBalance = loanInfo.principalBalance,
            nextRepaymentAmount = nextRepaymentInfo.monthlyPayment
        )

        return earlyPrincipalRepayDto
    }
}