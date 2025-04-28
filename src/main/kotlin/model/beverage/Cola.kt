package org.example.model.beverage

class Cola : Beverage(
    id = "cola",
    name = "콜라",
    price = 1100
) {
    override fun getDescription(): String {
        return "탄산이 가득한 시원한 콜라입니다."
    }
}