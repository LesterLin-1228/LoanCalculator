package org.example.loancalculator.service

import org.example.loancalculator.dao.LoanInfoDao
import org.example.loancalculator.dao.RepaymentRecordDao
import org.example.loancalculator.dto.EarlyPrincipalRepaymentDto
import org.example.loancalculator.dto.RepaymentDto
import org.example.loancalculator.entity.LoanInfo
import org.example.loancalculator.entity.RepaymentRecord
import org.example.loancalculator.response.EarlyPrincipalRepaymentResponse
import org.example.loancalculator.response.Response
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate

@Service
class RepaymentService(
    @Autowired private val loanInfoDao: LoanInfoDao,
    @Autowired private val repaymentRecordDao: RepaymentRecordDao,
    @Autowired private val loanInfoService: LoanInfoService,
) {

    fun repay(repaymentDto: RepaymentDto): Response<String> {
        val loanInfo = loanInfoDao.findByLoanAccount(repaymentDto.loanAccount) ?: return Response(
            "貸款帳號不存在",
            HttpStatus.BAD_REQUEST
        )

        // 計算當期利息
        val currentInterestRate = loanInfoService.getCurrentInterestRate(loanInfo.loanAccount)
        val monthlyRate = currentInterestRate / 100 / 12
        val interestForPeriod = roundToInteger(loanInfo.principalBalance * monthlyRate)

        // 當期還款金額
        val repaymentAmount = repaymentDto.repaymentAmount

        // 計算當期還款的本金
        val principalForPeriod = repaymentAmount - interestForPeriod

        // 更新本金餘額
        val principalBalance = loanInfo.principalBalance - principalForPeriod

        // 更新貸款資訊
        loanInfo.principalBalance = principalBalance
        loanInfo.totalAmountRepaid += repaymentAmount
        loanInfo.totalInterestRepaid += interestForPeriod
        loanInfo.totalPrincipalRepaid += principalForPeriod
        loanInfoDao.save(loanInfo)

        // 記錄還款
        val repaymentRecord = RepaymentRecord(
            loanAccount = repaymentDto.loanAccount,
            repaymentAmount = repaymentAmount,
            repaymentDate = repaymentDto.repaymentDate ?: LocalDate.now(),
            principalRepaid = principalForPeriod,
            interestRepaid = interestForPeriod,
            currentInterestRate = currentInterestRate
        )
        repaymentRecordDao.save(repaymentRecord)

        return Response("還款操作成功", HttpStatus.OK)
    }

    fun calculateEarlyPrincipalRepayment(
        earlyPrincipalRepaymentDto: EarlyPrincipalRepaymentDto
    ): Response<EarlyPrincipalRepaymentResponse> {
        val loanInfo =
            loanInfoDao.findByLoanAccount(earlyPrincipalRepaymentDto.loanAccount) ?: throw Exception("貸款帳號不存在")

        // 計算本金餘額
        val principalBalance = loanInfo.principalBalance - earlyPrincipalRepaymentDto.earlyPrincipalRepayment
        if (principalBalance < 0) {
            throw Exception("提前還本金超過本金餘額")
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
            ?: throw Exception("查無下期還款資訊")

        val nextRepaymentDate = loanInfoService.calculateNextRepaymentDate(
            loanInfo.startDate,
            nextRepaymentInfo.period,
            loanInfo.repaymentDueDay
        )

        val response = EarlyPrincipalRepaymentResponse(
            principalBalance = principalBalance,
            nextRepaymentAmount = nextRepaymentInfo.monthlyPayment,
            nextRepaymentDate = nextRepaymentDate
        )
        return Response("試算成功", HttpStatus.OK, response)
    }

    private fun roundToInteger(value: Double): Int {
        return BigDecimal(value).setScale(0, RoundingMode.HALF_UP).toInt()
    }
}