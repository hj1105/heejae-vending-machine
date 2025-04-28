package org.example.model.beverage

class Coffee : Beverage(
    id = "coffee",
    name = "커피",
    price = 700
) {
    override fun getDescription(): String {
        return "향기로운 블랙 커피입니다."
    }
}