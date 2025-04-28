package org.example.model.inventory

class Inventory<T> {
    private val items = mutableMapOf<T, Int>()

    fun addItem(item: T, quantity: Int = 1): Boolean {
        if (quantity <= 0) return false

        items[item] = items.getOrDefault(item, 0) + quantity
        return true
    }

    fun removeItem(item: T, quantity: Int = 1): Boolean {
        val currentQuantity = items.getOrDefault(item, 0)
        if (currentQuantity < quantity) return false

        items[item] = currentQuantity - quantity
        if (items[item] == 0) {
            items.remove(item)
        }

        return true
    }

    fun getQuantity(item: T): Int {
        return items.getOrDefault(item, 0)
    }

    fun hasItem(item: T): Boolean {
        return items.getOrDefault(item, 0) > 0
    }

    fun setQuantity(item: T, quantity: Int): Boolean {
        if (quantity < 0) return false

        if (quantity == 0) {
            items.remove(item)
        } else {
            items[item] = quantity
        }

        return true
    }

    fun getAllItems(): Map<T, Int> {
        return items.toMap()
    }

    fun getAvailableItems(): Set<T> {
        return items.filter { it.value > 0 }.keys
    }

    fun updateInventory(newInventory: Map<T, Int>): Boolean {
        items.clear()
        items.putAll(newInventory.filter { it.value > 0 })
        return true
    }

    fun clear() {
        items.clear()
    }

    override fun toString(): String {
        return items.entries.joinToString("\n") { (item, quantity) ->
            "$item: $quantity 개"
        }
    }
}