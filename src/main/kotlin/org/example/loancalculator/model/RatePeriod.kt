package org.example.loancalculator.model

data class RatePeriod(
    val rate: Double, // 利率 (單位%)
    val months: Int // 利率適用的期間 (單位月)
)