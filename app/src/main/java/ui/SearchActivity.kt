package com.example.smartrecipes.ui

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.smartrecipes.databinding.ActivitySearchBinding

class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 1. Настраиваем Spinner (сложность)
        val difficultyOptions = listOf("Выбрать сложность", "Лёгкая", "Средняя", "Сложная")
        val spinnerAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            difficultyOptions
        )
        binding.spinnerDifficulty.adapter = spinnerAdapter

        // 2. Кнопка выбора ингредиентов — открываем экран выбора
        binding.btnSelectIngredients.setOnClickListener {
            val intent = Intent(this, IngredientsActivity::class.java)
            startActivity(intent)
        }

        // 3. Кнопка "Сбросить фильтры"
        binding.btnResetFilters.setOnClickListener {
            binding.etName.setText("")
            binding.spinnerDifficulty.setSelection(0)  // "Выбрать сложность"
            SearchFilters.selectedIngredients = emptyList()
            updateSelectedIngredientsText()
            Toast.makeText(this, "Фильтры сброшены", Toast.LENGTH_SHORT).show()
        }

        // 4. Показать результаты — передаём фильтры в RecipeListActivity
        binding.btnShowResults.setOnClickListener {
            val nameQuery = binding.etName.text.toString().trim()

            val spinnerValue = binding.spinnerDifficulty.selectedItem as String
            val difficultyKey: String? = when (spinnerValue) {
                "Лёгкая" -> "легко"
                "Средняя" -> "средне"
                "Сложная" -> "сложно"
                else -> null   // "Выбрать сложность"
            }

            val selectedIngredients = ArrayList(SearchFilters.selectedIngredients)

            val intent = Intent(this, RecipeListActivity::class.java).apply {
                putExtra("nameQuery", nameQuery)
                if (!difficultyKey.isNullOrEmpty()) {
                    putExtra("difficulty", difficultyKey)
                }
                putStringArrayListExtra("ingredients", selectedIngredients)
            }
            startActivity(intent)
        }

        // 5. Назад
        binding.btnBackFromSearch.setOnClickListener {
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        // При возврате с выбора ингредиентов обновляем текст «окошка»
        updateSelectedIngredientsText()
    }

    private fun updateSelectedIngredientsText() {
        val list = SearchFilters.selectedIngredients
        binding.tvSelectedIngredients.text =
            if (list.isEmpty()) {
                "Ингредиенты: не выбраны"
            } else {
                "Ингредиенты: " + list.joinToString()
            }
    }
}