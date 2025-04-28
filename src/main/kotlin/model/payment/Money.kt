package org.example.model.payment

enum class Money(val value: Int, val description: String) {
    COIN_100(100, "100원"),
    COIN_500(500, "500원"),
    BILL_1000(1000, "1,000원"),
    BILL_5000(5000, "5,000원"),
    BILL_10000(10000, "10,000원");

    override fun toString(): String = description

    companion object {
        fun getAllDenominations(): List<Money> {
            return values().toList()
        }

        fun fromValue(value: Int): Money? {
            return values().find { it.value == value }
        }
    }
}