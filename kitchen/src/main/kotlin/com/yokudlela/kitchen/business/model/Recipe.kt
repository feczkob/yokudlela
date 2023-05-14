package com.yokudlela.kitchen.business.model

class Recipe(
    val recipeId: String,
    val ingredientMeasures: List<Measure>,
    val menuItemId: Int,
    val name: String,
    val preparationDescription: String,
    val preparationMinutes: Int,
    val portion: Int,
)
