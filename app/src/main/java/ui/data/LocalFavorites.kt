package com.example.smartrecipes.ui.data

import android.content.Context
import com.example.smartrecipes.ui.Recipe
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object LocalFavorites {

    private const val PREF_NAME = "local_favorites_prefs"
    private const val KEY_FAVORITES = "favorites"

    private val gson = Gson()

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    fun getAll(context: Context): List<Recipe> {
        val json = prefs(context).getString(KEY_FAVORITES, null) ?: return emptyList()
        val type = object : TypeToken<List<Recipe>>() {}.type
        return gson.fromJson<List<Recipe>>(json, type) ?: emptyList()
    }

    private fun saveAll(context: Context, list: List<Recipe>) {
        val json = gson.toJson(list)
        prefs(context).edit().putString(KEY_FAVORITES, json).apply()
    }

    fun add(context: Context, recipe: Recipe) {
        val current = getAll(context).toMutableList()
        // избегаем дублей по id
        if (current.none { it.id == recipe.id }) {
            current.add(recipe)
            saveAll(context, current)
        }
    }

    fun remove(context: Context, recipeId: String) {
        val current = getAll(context)
        val newList = current.filterNot { it.id == recipeId }
        saveAll(context, newList)
    }
}