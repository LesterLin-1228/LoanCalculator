package org.example.loancalculator.service

import org.example.loancalculator.dao.LoanInfoDao
import org.example.loancalculator.dao.LoanInterestRateDao
import org.example.loancalculator.dao.RepaymentRecordDao
import org.example.loancalculator.dto.RepaymentDto
import org.example.loancalculator.entity.RepaymentRecord
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate

@Service
class RepaymentService(
    @Autowired private val loanInfoDao: LoanInfoDao,
    @Autowired private val interestRateService: InterestRateService,
    @Autowired private val loanInterestRateDao: LoanInterestRateDao,
    @Autowired private val repaymentRecordDao: RepaymentRecordDao
) {

    fun repay(repaymentDto: RepaymentDto): ResponseEntity<String> {
        val loanInfo = loanInfoDao.findByLoanAccount(repaymentDto.loanAccount) ?: return ResponseEntity(
            "貸款帳號不存在",
            HttpStatus.BAD_REQUEST
        )

        // 計算當期利息
        val baseRate = interestRateService.getLatestInterestRate()?.baseRate ?: throw Exception("查無最新基礎利率")
        val rateDifference =
            loanInterestRateDao.findByLoanAccount(loanInfo.loanAccount)?.rateDifference ?: throw Exception("查無利率差")
        val monthlyRate = (baseRate + rateDifference) / 100 / 12
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
            currentInterestRate = baseRate + rateDifference
        )
        repaymentRecordDao.save(repaymentRecord)

        return ResponseEntity("還款操作成功", HttpStatus.OK)
    }

    private fun roundToInteger(value: Double): Int {
        return BigDecimal(value).setScale(0, RoundingMode.HALF_UP).toInt()
    }
}