package com.example.smartrecipes.ui

import java.io.Serializable

data class Recipe(
    val id: String = "",
    val name: String = "",
    val subtitle: String = "",
    val ingredients: List<String> = emptyList(),
    val instructions: String = "",
    val imageUrl: String? = null,
    val difficulty: String = "",
    val isLocal: Boolean = false        // новое поле
) : Serializable