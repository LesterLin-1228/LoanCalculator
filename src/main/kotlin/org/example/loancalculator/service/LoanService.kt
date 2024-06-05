package org.example.loancalculator.service

import org.example.loancalculator.dao.LoanDao
import org.example.loancalculator.dto.LoanDetailsDto
import org.example.loancalculator.dto.loanDto
import org.example.loancalculator.entity.Loan
import org.example.loancalculator.result.Result
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service

@Service
class LoanService(
    @Autowired private val loanDao: LoanDao,
    @Autowired private val loanCalculatorService: LoanCalculatorService
) {

    fun createLoan(loanDto: loanDto): Result {
        val existingLoan = loanDao.findByLoanAccount(loanDto.account)
        return if (existingLoan != null) {
            Result("帳號重複", HttpStatus.CONFLICT)
        } else {
            val loan = Loan(
                account = loanDto.account,
                startDate = loanDto.startDate,
                endDate = loanDto.endDate,
                paymentDueDay = loanDto.paymentDueDay,
                remainingPrincipal = loanDto.remainingPrincipal,
                totalRepayment = loanDto.totalRepayment,
                totalPrincipalRepaid = loanDto.totalPrincipalRepaid,
                totalInterestRepaid = loanDto.totalInterestRepaid,
                loanAmount = loanDto.loanAmount,
                loanTerm = loanDto.loanTerm
            )
            loanDao.save(loan)
            Result("成功建立", HttpStatus.CREATED)
        }
    }

//    fun getLoanDetails(loanAccount: String): LoanDetailsDto? {
//        val loan = loanDao.findByLoanAccount(loanAccount)
//        return loan?.let {
//            LoanDetailsDto(
//                remainingPrincipal = loanCalculatorService.calculateLoan(it).payments[0].remainingPrincipal,
//                repayment = loanCalculatorService.calculateLoan(it).payments[0].monthlyPayment
//            )
//        }
//    }
}