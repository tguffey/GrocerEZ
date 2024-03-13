package com.example.grocerez.ui

// class for the quantity of items
class ItemAmount (private val value: Float, private val unit: Unit) {
    // turn amount with value and symbol into string format
    override fun toString() : String {
        return "$value ${unit.symbol}"
    }

    // get the value of the unit in decimal form
    fun getValueF() : Float {
        return value
    }

    // get the symbol of the unit
    fun getUnit() : String {
        return unit.symbol
    }

    // allows any class to call function statically
    companion object {
        // get all possible units for drop down menu
        fun getAllUnits(): Array<String> {
            var units: Array<String> = emptyArray()
            Unit.entries.forEach { units += it.symbol }
            return units
        }
    }
}

enum class Unit (val symbol: String) {
    NONE(""),
    GRAM("g"),
    KILOGRAM("kg"),
    POUND("lb"),
    COUNT("count");

    // Look up enum value by symbol
    companion object {
        fun getBySymbol(symb: String) = entries.firstOrNull { it.symbol == symb }
    }
}