package org.example.loancalculator.service

import org.example.loancalculator.dao.LoanDao
import org.example.loancalculator.entity.Loan
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class LoanService(@Autowired private val loanDao: LoanDao) {

    fun createLoan(loan: Loan): String {
        val existingLoan = loanDao.findByAccount(loan.account)
        if (existingLoan != null) {
            return "帳號重複"
        }
        loanDao.save(loan)
        return "成功建立"
    }
}