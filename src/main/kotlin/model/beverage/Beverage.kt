package org.example.model.beverage

abstract class Beverage(
    val id: String,
    val name: String,
    val price: Int
) {
    abstract fun getDescription(): String

    override fun toString(): String {
        return "$name ($price 원)"
    }
}