package com.yokudlela.kitchen.business.handler

interface IngredientHandler {

    fun loadQuantityWh(ingredientId: Int): Int
    fun obtainIngredientWh(ingredientId: Int, quantity: Float)
    fun notifyMissingIngredient(ingredientId: Int)
}