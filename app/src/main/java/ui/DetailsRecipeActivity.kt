package com.example.smartrecipes.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.smartrecipes.databinding.ActivityRecipeDetailsBinding
import com.example.smartrecipes.ui.data.FirebaseFavorites
import com.example.smartrecipes.ui.data.LocalFavorites
import com.example.smartrecipes.ui.data.LocalRecipes

class DetailsRecipeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRecipeDetailsBinding
    private lateinit var recipe: Recipe

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRecipeDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val r = intent.getSerializableExtra("recipe") as? Recipe
        if (r == null) {
            Toast.makeText(this, "Рецепт не найден", Toast.LENGTH_SHORT).show()
            finish()
            return
        } else {
            recipe = r
        }

        // Текстовые поля
        binding.tvRecipeTitle.text = recipe.name
        binding.tvRecipeSubtitle.text = recipe.subtitle
        binding.tvRecipeIngredients.text = recipe.ingredients.joinToString("\n") { "- $it" }
        binding.tvRecipeInstructions.text = recipe.instructions

        if (recipe.isLocal) {
            // Локальный рецепт: кнопка удаляет сам рецепт
            binding.btnAddToFav.text = "Удалить рецепт"
            binding.btnAddToFav.setOnClickListener {
                LocalRecipes.remove(this, recipe.id)
                // на всякий случай уберём и из локального избранного,
                // если такой рецепт там был
                LocalFavorites.remove(this, recipe.id)

                Toast.makeText(this, "Рецепт удалён", Toast.LENGTH_SHORT).show()
                finish()
            }
        } else {
            // Обычный рецепт: избранное
            FirebaseFavorites.isFavorite(recipe.id) { isFav ->
                runOnUiThread { setFavButtonText(isFav) }
            }

            binding.btnAddToFav.setOnClickListener {
                FirebaseFavorites.isFavorite(recipe.id) { isFav ->
                    if (isFav) {
                        // удаляем из Firebase и локального избранного
                        FirebaseFavorites.remove(recipe)
                        LocalFavorites.remove(this, recipe.id)

                        runOnUiThread {
                            Toast.makeText(this, "Удалено из избранного", Toast.LENGTH_SHORT).show()
                            setFavButtonText(false)
                        }
                    } else {
                        // добавляем в Firebase и локальное избранное
                        FirebaseFavorites.add(recipe)
                        LocalFavorites.add(this, recipe)

                        runOnUiThread {
                            Toast.makeText(this, "Добавлено в избранное", Toast.LENGTH_SHORT).show()
                            setFavButtonText(true)
                        }
                    }
                }
            }
        }

        binding.btnBackDetails.setOnClickListener { finish() }
    }

    private fun setFavButtonText(isFav: Boolean) {
        binding.btnAddToFav.text =
            if (isFav) "Удалить из избранного" else "Добавить в избранное"
    }
}