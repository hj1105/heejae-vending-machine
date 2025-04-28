package org.example.model.beverage

class Water : Beverage(
    id = "water",
    name = "물",
    price = 600
) {
    override fun getDescription(): String {
        return "깨끗하고 순수한 생수입니다."
    }
}