package com.example.smartrecipes.ui

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.smartrecipes.databinding.ActivityAdminCreateRecipeBinding
import com.google.firebase.firestore.FirebaseFirestore

class AdminCreateRecipeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminCreateRecipeBinding
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminCreateRecipeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Настройка спиннера сложности
        val difficultyOptions = listOf("Лёгкая", "Средняя", "Сложная")
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            difficultyOptions
        )
        binding.spinnerDifficulty.adapter = adapter

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

            // Получаем выбранную сложность и приводим к ключу для Firestore
            val spinnerValue = binding.spinnerDifficulty.selectedItem as String
            val difficulty = when (spinnerValue) {
                "Лёгкая" -> "легко"
                "Средняя" -> "средне"
                "Сложная" -> "сложно"
                else -> "легко"  // на всякий случай
            }

            val docRef = db.collection("Recipes").document()
            val recipe = Recipe(
                id = docRef.id,
                name = name,
                subtitle = subtitle,
                ingredients = ingredients,
                instructions = instructions,
                imageUrl = "",
                difficulty = difficulty,
                isLocal = false
            )

            docRef.set(recipe)
                .addOnSuccessListener {
                    Toast.makeText(this, "Рецепт добавлен для всех пользователей", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Ошибка: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }

        binding.btnBack.setOnClickListener { finish() }
    }
}