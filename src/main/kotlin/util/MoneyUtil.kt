package org.example.util

import org.example.model.payment.Money

object MoneyUtil {
    fun calculateChange(amount: Int): Map<Money, Int> {
        var remaining = amount
        val result = mutableMapOf<Money, Int>()
        val denominations = Money.values().sortedByDescending { it.value }

        for (money in denominations) {
            val count = remaining / money.value
            if (count > 0) {
                result[money] = count
                remaining -= count * money.value
            }
        }

        return result
    }
}