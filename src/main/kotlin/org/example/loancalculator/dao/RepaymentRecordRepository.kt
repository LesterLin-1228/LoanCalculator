package org.example.loancalculator.dao

import org.example.loancalculator.entity.RepaymentRecord
import org.springframework.data.jpa.repository.JpaRepository

interface RepaymentRecordRepository : JpaRepository<RepaymentRecord, Long>{
}