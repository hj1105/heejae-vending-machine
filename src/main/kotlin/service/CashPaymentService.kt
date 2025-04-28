package org.example.service

import org.example.model.payment.Money
import org.example.model.payment.PaymentResult
import org.example.util.MoneyUtil

class CashPaymentService {
    private var insertedAmount = 0

    private val cashInventory = mutableMapOf<Money, Int>().apply {
        Money.getAllDenominations().forEach { money ->
            this[money] = when(money) {
                Money.COIN_100 -> 50
                Money.COIN_500 -> 30
                Money.BILL_1000 -> 20
                Money.BILL_5000 -> 10
                Money.BILL_10000 -> 5
            }
        }
    }

    fun insertMoney(money: Money): Int {
        insertedAmount += money.value
        cashInventory[money] = cashInventory.getOrDefault(money, 0) + 1
        return insertedAmount
    }

    fun getInsertedAmount(): Int = insertedAmount

    fun processPayment(amount: Int): PaymentResult {
        if (insertedAmount < amount) {
            return PaymentResult(
                success = false,
                message = "금액이 부족합니다. ${amount - insertedAmount}원이 더 필요합니다."
            )
        }

        val change = insertedAmount - amount
        val changeResult = returnChange(change)

        insertedAmount = 0

        return changeResult
    }

    private fun returnChange(amount: Int): PaymentResult {
        if (amount == 0) {
            return PaymentResult(
                success = true,
                message = "정확한 금액이 투입되었습니다."
            )
        }

        val changeMap = MoneyUtil.calculateChange(amount)
        val changeBreakdown = mutableMapOf<Money, Int>()
        var remainingAmount = amount

        // 각 화폐 단위별로 필요한 개수와 보유량 확인
        for ((money, required) in changeMap) {
            val available = cashInventory.getOrDefault(money, 0)
            val actualCount = minOf(required, available)

            if (actualCount > 0) {
                changeBreakdown[money] = actualCount
                remainingAmount -= money.value * actualCount
                cashInventory[money] = cashInventory.getOrDefault(money, 0) - actualCount
            }
        }

        return if (remainingAmount > 0) {
            for ((money, count) in changeBreakdown) {
                cashInventory[money] = cashInventory.getOrDefault(money, 0) + count
            }

            PaymentResult(
                success = false,
                message = "거스름돈이 부족합니다. 관리자에게 문의해주세요.",
                change = changeBreakdown
            )
        } else {
            PaymentResult(
                success = true,
                message = "결제가 완료되었습니다. 거스름돈 ${amount}원이 반환됩니다.",
                change = changeBreakdown
            )
        }
    }

    fun cancelAndReturnMoney(): PaymentResult {
        val returnedAmount = insertedAmount
        insertedAmount = 0

        return PaymentResult(
            success = true,
            message = "${returnedAmount}원이 반환되었습니다."
        )
    }

    fun refillCash(money: Money, quantity: Int): Boolean {
        cashInventory[money] = cashInventory.getOrDefault(money, 0) + quantity
        return true
    }

    fun getCashInventory(): Map<Money, Int> = cashInventory.toMap()
}