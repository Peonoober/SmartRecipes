package com.example.smartrecipes.ui.data

import android.content.Context
import com.example.smartrecipes.ui.Recipe
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object LocalRecipes {

    private const val PREF_NAME = "my_recipes_prefs"
    private const val KEY_RECIPES = "my_recipes"

    private val gson = Gson()

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    fun getAll(context: Context): List<Recipe> {
        val json = prefs(context).getString(KEY_RECIPES, null) ?: return emptyList()
        val type = object : TypeToken<List<Recipe>>() {}.type
        return gson.fromJson<List<Recipe>>(json, type) ?: emptyList()
    }

    private fun saveAll(context: Context, list: List<Recipe>) {
        val json = gson.toJson(list)
        prefs(context).edit().putString(KEY_RECIPES, json).apply()
    }

    fun add(context: Context, recipe: Recipe) {
        val current = getAll(context).toMutableList()
        current.add(recipe)
        saveAll(context, current)
    }

    fun remove(context: Context, recipeId: String) {
        val current = getAll(context)
        val newList = current.filterNot { it.id == recipeId }
        saveAll(context, newList)
    }
}