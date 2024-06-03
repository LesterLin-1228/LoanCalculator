package org.example.loancalculator.service

import org.example.loancalculator.dao.LoanInfoRepository
import org.example.loancalculator.entity.LoanInfo
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class LoanInfoService(@Autowired private val loanInfoRepository: LoanInfoRepository) {

    fun createLoanInfo(loanInfo: LoanInfo): String {
        val existingLoanInfo = loanInfoRepository.findByLoanAccount(loanInfo.loanAccount)
        if (existingLoanInfo != null) {
            return "帳號重複"
        }
        loanInfoRepository.save(loanInfo)
        return "成功建立"
    }
}