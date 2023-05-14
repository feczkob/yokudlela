package com.yokudlela.kitchen.business.service

import com.yokudlela.kitchen.business.handler.RecipeHandler
import com.yokudlela.kitchen.business.model.Ingredient
import com.yokudlela.kitchen.business.model.Measure
import com.yokudlela.kitchen.business.model.Recipe
import org.springframework.stereotype.Service

@Service
class RecipeService : RecipeHandler {

    override fun loadRecipeByMenuItemId(menuItemId: Int): Recipe {
        val ingredient = Ingredient(1, 10, 5f, "unit", "asd")
        val measure = Measure("measure_id", ingredient, 3f, "unit")
        return Recipe("recipe_id", listOf(measure), 1, "my recipe", "asd", 10, 1)
    }
}