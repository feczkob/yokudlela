package com.yokudlela.kitchen.business.service

import com.yokudlela.kitchen.business.handler.IngredientHandler
import org.springframework.stereotype.Service

@Service
class IngredientService : IngredientHandler {

    override fun loadQuantityWh(ingredientId: Int): Int {
        TODO("Not yet implemented")
    }

    override fun obtainIngredientWh(ingredientId: Int, quantity: Float) {
        TODO("Not yet implemented")
    }

    override fun notifyMissingIngredient(ingredientId: Int) {
        TODO("Not yet implemented")
    }
}