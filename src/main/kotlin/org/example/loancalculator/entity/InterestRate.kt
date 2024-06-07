package org.example.loancalculator.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import org.hibernate.annotations.Comment
import java.time.LocalDate

@Entity
class InterestRate(
    @Id
    @Column(nullable = false)
    @Comment("利率日期")
    val date: LocalDate,

    @Column(nullable = false)
    @Comment("基礎利率(單位:%)")
    val baseRate: Double
)