package com.example.smartrecipes.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.smartrecipes.databinding.ActivityAdminDeleteRecipeBinding
import com.google.firebase.firestore.FirebaseFirestore

class AdminDeleteRecipeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminDeleteRecipeBinding
    private lateinit var adapter: RecipeAdapter
    private val db = FirebaseFirestore.getInstance()

    private val recipes = mutableListOf<Recipe>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminDeleteRecipeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = RecipeAdapter { recipe ->
            onRecipeClick(recipe)
        }

        binding.rvAdminRecipes.layoutManager = LinearLayoutManager(this)
        binding.rvAdminRecipes.adapter = adapter

        binding.btnFind.setOnClickListener {
            searchRecipes()
        }

        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    private fun searchRecipes() {
        val query = binding.etName.text.toString().trim().lowercase()

        db.collection("Recipes")
            .get()
            .addOnSuccessListener { snap ->
                val all = snap.documents.mapNotNull { d ->
                    d.toObject(Recipe::class.java)?.copy(id = d.id)
                }

                val filtered = if (query.isEmpty()) {
                    all
                } else {
                    all.filter { it.name.lowercase().contains(query) }
                }

                recipes.clear()
                recipes.addAll(filtered)
                adapter.submitList(recipes.toList())

                binding.tvResult.text = "Найдено рецептов: ${recipes.size}"
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Ошибка: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun onRecipeClick(recipe: Recipe) {
        val options = arrayOf("Открыть рецепт", "Удалить рецепт", "Отмена")

        AlertDialog.Builder(this)
            .setTitle(recipe.name)
            .setItems(options) { dialog, which ->
                when (which) {
                    0 -> { // Открыть
                        val intent = Intent(this, DetailsRecipeActivity::class.java)
                        intent.putExtra("recipe", recipe)
                        startActivity(intent)
                    }
                    1 -> { // Удалить
                        confirmDeleteRecipe(recipe)
                    }
                    else -> dialog.dismiss()
                }
            }
            .show()
    }

    private fun confirmDeleteRecipe(recipe: Recipe) {
        AlertDialog.Builder(this)
            .setTitle("Удалить рецепт")
            .setMessage("Вы точно хотите удалить рецепт \"${recipe.name}\"?")
            .setPositiveButton("Удалить") { _, _ ->
                deleteRecipe(recipe)
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    private fun deleteRecipe(recipe: Recipe) {
        if (recipe.id.isBlank()) {
            Toast.makeText(this, "Нельзя удалить рецепт без ID", Toast.LENGTH_SHORT).show()
            return
        }

        db.collection("Recipes").document(recipe.id)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(this, "Рецепт удалён", Toast.LENGTH_SHORT).show()
                recipes.removeAll { it.id == recipe.id }
                adapter.submitList(recipes.toList())
                binding.tvResult.text = "Найдено рецептов: ${recipes.size}"
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Ошибка удаления: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}