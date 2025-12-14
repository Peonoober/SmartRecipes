package com.example.smartrecipes.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.smartrecipes.databinding.ActivityCreateMyRecipeBinding
import com.example.smartrecipes.ui.data.LocalRecipes

class CreateMyRecipeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateMyRecipeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateMyRecipeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSave.setOnClickListener {
            val name = binding.etName.text.toString().trim()
            val subtitle = binding.etSubtitle.text.toString().trim()
            val ingredientsText = binding.etIngredients.text.toString().trim()
            val instructions = binding.etInstructions.text.toString().trim()

            if (name.isEmpty()) {
                Toast.makeText(this, "Введите название рецепта", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val ingredients = ingredientsText
                .split('\n', ',', ';')
                .map { it.trim() }
                .filter { it.isNotEmpty() }

            val id = "local_" + System.currentTimeMillis()

            val recipe = Recipe(
                id = id,
                name = name,
                subtitle = subtitle,
                ingredients = ingredients,
                instructions = instructions,
                imageUrl = null,      // заглушка (картинка по умолчанию)
                difficulty = "",
                isLocal = true
            )

            LocalRecipes.add(this, recipe)
            Toast.makeText(this, "Рецепт сохранён", Toast.LENGTH_SHORT).show()
            finish()
        }

        binding.btnBack.setOnClickListener {
            finish()
        }
    }
}