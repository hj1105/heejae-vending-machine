package org.example.model.beverage

object BeverageFactory {
    fun createBeverage(id: String): Beverage? {
        return when (id.lowercase()) {
            "cola" -> Cola()
            "water" -> Water()
            "coffee" -> Coffee()
            else -> null
        }
    }

    fun getAllBeverages(): List<Beverage> {
        return listOf(Cola(), Water(), Coffee())
    }
}