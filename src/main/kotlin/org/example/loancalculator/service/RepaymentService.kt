package org.example.loancalculator.service

import org.example.loancalculator.dao.LoanInfoDao
import org.example.loancalculator.dao.RepaymentRecordDao
import org.example.loancalculator.dto.repayment.EarlyPrincipalRepayDto
import org.example.loancalculator.dto.repayment.EarlyPrincipalRepayReq
import org.example.loancalculator.dto.repayment.RepaymentDto
import org.example.loancalculator.dto.repayment.RepaymentReq
import org.example.loancalculator.entity.LoanInfo
import org.example.loancalculator.entity.RepaymentRecord
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate

@Service
class RepaymentService(
    @Autowired private val loanInfoDao: LoanInfoDao,
    @Autowired private val repaymentRecordDao: RepaymentRecordDao,
    @Autowired private val loanInfoService: LoanInfoService,
) {

    fun repay(repaymentReq: RepaymentReq): RepaymentDto {
        val loanInfo = loanInfoDao.findByLoanAccount(repaymentReq.loanAccount) ?: throw ResponseStatusException(
            HttpStatus.NOT_FOUND,
            "貸款帳號不存在"
        )
        // 計算當期利息
        val currentInterestRate = loanInfoService.getCurrentInterestRate(loanInfo.loanAccount)
        val monthlyRate = currentInterestRate / 100 / 12
        val interestForPeriod = roundToInteger(loanInfo.principalBalance * monthlyRate)
        // 當期還款金額
        val repaymentAmount = repaymentReq.repaymentAmount
        // 計算當期還款的本金
        val principalForPeriod = repaymentAmount - interestForPeriod
        // 更新本金餘額
        val principalBalance = loanInfo.principalBalance - principalForPeriod

        // 更新貸款資訊
        loanInfo.principalBalance = principalBalance
        loanInfo.totalAmountRepayment += repaymentAmount
        loanInfo.totalInterestRepayment += interestForPeriod
        loanInfo.totalPrincipalRepayment += principalForPeriod
        loanInfoDao.save(loanInfo)

        // 記錄還款
        val repaymentRecord = RepaymentRecord(
            loanInfo = loanInfo,
            repaymentAmount = repaymentAmount,
            repaymentDate = repaymentReq.repaymentDate ?: LocalDate.now(),
            principalRepaid = principalForPeriod,
            interestRepaid = interestForPeriod,
            currentInterestRate = currentInterestRate
        )
        repaymentRecordDao.save(repaymentRecord)

        // 記錄還款返回Dto
        val repaymentDto = RepaymentDto(
            loanAccount = repaymentReq.loanAccount,
            repaymentAmount = repaymentAmount,
            repaymentDate = repaymentReq.repaymentDate ?: LocalDate.now(),
            principalRepaid = principalForPeriod,
            interestRepaid = interestForPeriod,
            currentInterestRate = currentInterestRate
        )

        return repaymentDto
    }

    fun calculateEarlyPrincipalRepay(
        earlyPrincipalRepayReq: EarlyPrincipalRepayReq
    ): EarlyPrincipalRepayDto {
        val loanInfo =
            loanInfoDao.findByLoanAccount(earlyPrincipalRepayReq.loanAccount) ?: throw ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "貸款帳號不存在"
            )

        // 計算本金餘額
        val principalBalance = loanInfo.principalBalance - earlyPrincipalRepayReq.earlyPrincipalRepayment
        if (principalBalance < 0) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "提前還本金超過本金餘額")
        }
        // 創建一個新的 LoanInfo 物件，用於試算，避免影響資料庫
        val tempLoanInfo = LoanInfo(
            loanAccount = loanInfo.loanAccount,
            startDate = loanInfo.startDate,
            endDate = loanInfo.endDate,
            repaymentDueDay = loanInfo.repaymentDueDay,
            principalBalance = principalBalance,
            loanAmount = loanInfo.loanAmount,
            loanTerm = loanInfo.loanTerm
        )

        val loanResponse = loanInfoService.prepareLoanRequest(tempLoanInfo)

        val nextRepaymentInfo = loanResponse.payments.firstOrNull { it.principalBalance < principalBalance }
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "查無下期還款資訊")
        println(nextRepaymentInfo)

        val earlyPrincipalRepayDto = EarlyPrincipalRepayDto(
            principalBalance = principalBalance,
            nextRepaymentAmount = nextRepaymentInfo.monthlyPayment,
        )
        return earlyPrincipalRepayDto
    }

    fun earlyPrincipalRepay(
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

        // 更新 LoanInfo
        loanInfo.principalBalance = newPrincipalBalance
        loanInfo.totalPrincipalRepayment += earlyPrincipalRepayReq.earlyPrincipalRepayment
        loanInfo.totalAmountRepayment += earlyPrincipalRepayReq.earlyPrincipalRepayment
        loanInfoDao.save(loanInfo)

        // 記錄提前還款資訊
        val currentInterestRate = loanInfoService.getCurrentInterestRate(loanInfo.loanAccount)
        val repaymentRecord = RepaymentRecord(
            loanInfo = loanInfo,
            repaymentAmount = earlyPrincipalRepayReq.earlyPrincipalRepayment,
            principalRepaid = earlyPrincipalRepayReq.earlyPrincipalRepayment,
            interestRepaid = 0,
            currentInterestRate = currentInterestRate,
            repaymentDate = LocalDate.now()
        )
        repaymentRecordDao.save(repaymentRecord)

        val loanResponse = loanInfoService.prepareLoanRequest(loanInfo)

        val nextRepaymentInfo = loanResponse.payments.firstOrNull { it.principalBalance < loanInfo.principalBalance }
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "查無下期還款資訊")

        val earlyPrincipalRepayDto = EarlyPrincipalRepayDto(
            principalBalance = loanInfo.principalBalance,
            nextRepaymentAmount = nextRepaymentInfo.monthlyPayment
        )

        return earlyPrincipalRepayDto
    }

    private fun roundToInteger(value: Double): Int {
        return BigDecimal(value).setScale(0, RoundingMode.HALF_UP).toInt()
    }
}