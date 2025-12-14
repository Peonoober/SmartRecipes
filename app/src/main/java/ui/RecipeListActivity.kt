package com.example.smartrecipes.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.smartrecipes.databinding.ActivityRecipeListBinding
import com.google.firebase.firestore.FirebaseFirestore

class RecipeListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRecipeListBinding
    private lateinit var adapter: RecipeAdapter
    private val db = FirebaseFirestore.getInstance()

    // Параметры фильтра
    private var nameQuery: String = ""
    private var difficultyFilter: String? = null
    private var selectedIngredients: List<String> = emptyList()

    data class ScoredRecipe(
        val recipe: Recipe,
        val matches: Int    // сколько выбранных ингредиентов есть в рецепте
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecipeListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Получаем фильтры из Intent
        nameQuery = intent.getStringExtra("nameQuery")?.trim().orEmpty()
        difficultyFilter = intent.getStringExtra("difficulty")
        selectedIngredients =
            intent.getStringArrayListExtra("ingredients") ?: emptyList()

        adapter = RecipeAdapter { recipe ->
            val intent = Intent(this, DetailsRecipeActivity::class.java)
            intent.putExtra("recipe", recipe)
            startActivity(intent)
        }

        binding.rvRecipes.layoutManager = LinearLayoutManager(this)
        binding.rvRecipes.adapter = adapter

        loadRecipesFromFirestore()

        binding.btnBack.setOnClickListener {
            finish() // назад к экрану поиска
        }
    }

    private fun loadRecipesFromFirestore() {
        db.collection("Recipes")
            .get()
            .addOnSuccessListener { snapshot ->
                val allRecipes = snapshot.documents.mapNotNull { document ->
                    document.toObject(Recipe::class.java)?.copy(id = document.id)
                }

                val scoredList = mutableListOf<ScoredRecipe>()

                // приведём выбранные ингредиенты к нижнему регистру один раз
                val selectedLower = selectedIngredients.map { it.lowercase() }

                for (recipe in allRecipes) {
                    // 1. Фильтр по названию (подстрока, без учёта регистра)
                    if (nameQuery.isNotEmpty() &&
                        !recipe.name.contains(nameQuery, ignoreCase = true)
                    ) {
                        continue
                    }

                    // 2. Фильтр по сложности (если выбрана)
                    if (!difficultyFilter.isNullOrBlank() &&
                        !recipe.difficulty.equals(difficultyFilter, ignoreCase = true)
                    ) {
                        continue
                    }

                    // 3. Подсчёт совпадений по ингредиентам
                    val matchCount = if (selectedLower.isNotEmpty()) {
                        val recipeLower = recipe.ingredients.map { it.lowercase() }
                        selectedLower.count { sel ->
                            // считаем совпадение, если выбранный ингредиент содержится
                            // хотя бы в одной строке ингредиентов рецепта
                            recipeLower.any { ing -> ing.contains(sel) }
                        }
                    } else {
                        0
                    }

                    // Если пользователь выбрал ингредиенты, но в рецепте нет НИ ОДНОГО из них — пропускаем рецепт
                    if (selectedLower.isNotEmpty() && matchCount == 0) {
                        continue
                    }

                    scoredList.add(ScoredRecipe(recipe, matchCount))
                }

                // Сортировка: если выбирали ингредиенты — по числу совпадений, иначе — как есть
                val finalList: List<Recipe> = if (selectedLower.isNotEmpty()) {
                    scoredList
                        .sortedByDescending { it.matches }
                        .map { it.recipe }
                } else {
                    scoredList.map { it.recipe }
                }

                if (finalList.isEmpty()) {
                    binding.tvEmpty.visibility = View.VISIBLE
                    binding.rvRecipes.visibility = View.GONE
                } else {
                    binding.tvEmpty.visibility = View.GONE
                    binding.rvRecipes.visibility = View.VISIBLE
                    adapter.submitList(finalList)
                }
            }
            .addOnFailureListener { e ->
                Log.e("FIRESTORE", "Error loading recipes", e)
                binding.tvEmpty.visibility = View.VISIBLE
                binding.rvRecipes.visibility = View.GONE
            }
    }
}