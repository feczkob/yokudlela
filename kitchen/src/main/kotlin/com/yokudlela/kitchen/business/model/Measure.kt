package com.yokudlela.kitchen.business.model

data class Measure(
    val measureId: String,
    val ingredient: Ingredient,
    val quantity: Float,
    val unit: String,
)
