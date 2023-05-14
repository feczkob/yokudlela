package com.yokudlela.kitchen.business.handler

import com.yokudlela.kitchen.business.model.Recipe

interface RecipeHandler {

    fun loadRecipeByMenuItemId(menuItemId: Int): Recipe
}