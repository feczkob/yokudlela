package com.yokudlela.kitchen.business.model

import com.yokudlela.kitchen.business.handler.IngredientHandler

class Ingredient(
    private val ingredientId: Int,
    private val maxCapacity: Int,
    // TODO dont use float, use double instead (java)
    var quantity: Float,
    val unit: String,
    val details: String,
) {

    lateinit var ingredientHandler: IngredientHandler

    /**
     * Checking if the warehouse has enough resources of this ingredient
     * @param quantityNeeded Float amount of needed quantity
     * @param orderItem OrderItem whose status is to be changed
     * if there are not enough resources in the warehouse
     */
    fun checkQuantityWh(quantityNeeded: Float, orderItem: OrderItem) {
        val quantityInWh = ingredientHandler.loadQuantityWh(ingredientId)
        if (quantityInWh < quantityNeeded) {
            orderItem.status = Status.FAILED
            ingredientHandler.notifyMissingIngredient(ingredientId)
        }
    }

    /**
     * Get resources from the warehouse
     */
    fun obtainFromWh() {
        ingredientHandler.obtainIngredientWh(ingredientId, quantity)
    }
}