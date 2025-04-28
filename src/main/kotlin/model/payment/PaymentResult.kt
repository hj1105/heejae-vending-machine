package org.example.model.payment

import java.time.LocalDateTime

data class PaymentResult(
    val success: Boolean,
    val message: String,
    val timestamp: LocalDateTime = LocalDateTime.now(),
    val change: Map<Money, Int> = emptyMap()
)