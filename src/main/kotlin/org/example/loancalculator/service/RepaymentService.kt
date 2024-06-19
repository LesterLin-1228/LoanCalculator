package org.example.loancalculator.service

import org.example.loancalculator.dto.repayment.EarlyPrincipalRepayDto
import org.example.loancalculator.dto.repayment.EarlyPrincipalRepayReq
import org.example.loancalculator.dto.repayment.RepaymentDto
import org.example.loancalculator.dto.repayment.RepaymentReq

interface RepaymentService {
    fun repay(repaymentReq: RepaymentReq): RepaymentDto
    fun calculateEarlyPrincipalRepay(earlyPrincipalRepayReq: EarlyPrincipalRepayReq): EarlyPrincipalRepayDto
    fun earlyPrincipalRepay(earlyPrincipalRepayReq: EarlyPrincipalRepayReq): EarlyPrincipalRepayDto
}