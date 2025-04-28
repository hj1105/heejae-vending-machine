package org.example.service

import org.example.model.payment.PaymentResult

class CardPaymentService {
    fun processPayment(cardNumber: String, amount: Int): PaymentResult {
        if (!validateCard(cardNumber)) {
            return PaymentResult(
                success = false,
                message = "유효하지 않은 카드입니다."
            )
        }

        val isApproved = requestCardApproval(cardNumber, amount)

        return if (isApproved) {
            PaymentResult(
                success = true,
                message = "${amount}원 카드 결제가 승인되었습니다."
            )
        } else {
            PaymentResult(
                success = false,
                message = "카드 결제가 거절되었습니다."
            )
        }
    }

    private fun validateCard(cardNumber: String): Boolean {
        return cardNumber.length == 16 && cardNumber.all { it.isDigit() }
    }

    /**
     * 카드 승인 요청 (외부 시스템 호출 시뮬레이션)
     */
    private fun requestCardApproval(cardNumber: String, amount: Int): Boolean {
        val lastDigit = cardNumber.last().digitToInt()

        return lastDigit % 2 == 0
    }
}