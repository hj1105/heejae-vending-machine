package org.example.model.sales

import org.example.model.beverage.Beverage
import org.example.model.payment.PaymentMethod
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class SaleHistory(
    val id: String = generateId(),
    val beverage: Beverage,
    val paymentMethod: PaymentMethod,
    val amount: Int,
    val timestamp: LocalDateTime = LocalDateTime.now()
) {
    companion object {
        private var counter = 0

        private fun generateId(): String {
            return "SALE-${++counter}"
        }
    }

    /**
     * 영수증 형식으로 판매 정보 출력
     */
    fun generateReceipt(): String {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val formattedDate = timestamp.format(formatter)

        return """
        |===== 영수증 =====
        |거래번호: $id
        |날짜: $formattedDate
        |상품: ${beverage.name}
        |가격: ${beverage.price}원
        |결제 방법: ${paymentMethod.description}
        |=================
        """.trimMargin()
    }
}