package com.example.smartrecipes.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.smartrecipes.databinding.ActivityMainMenuBinding
import com.example.smartrecipes.ui.data.AvatarStorage
import com.example.smartrecipes.ui.data.ThemeManager
import com.google.firebase.firestore.FirebaseFirestore

class MainMenuActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainMenuBinding
    private lateinit var adapter: RecipeAdapter
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        // применяем сохранённую тему до создания Activity
        ThemeManager.applyTheme(this)

        super.onCreate(savedInstanceState)
        binding = ActivityMainMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Лента рецептов
        adapter = RecipeAdapter { recipe ->
            val intent = Intent(this, DetailsRecipeActivity::class.java)
            intent.putExtra("recipe", recipe)
            startActivity(intent)
        }

        binding.rvFeed.layoutManager = LinearLayoutManager(this)
        binding.rvFeed.adapter = adapter

        loadAllRecipes()
        loadAvatarToTopBar()

        // Настройки
        binding.ivSettings.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }

        // Профиль
        binding.ivProfile.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        // Поиск по ингредиентам
        binding.btnSearchByIngredients.setOnClickListener {
            startActivity(Intent(this, SearchActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        loadAvatarToTopBar()
    }

    private fun loadAvatarToTopBar() {
        val bmp = AvatarStorage.loadBitmap(this)
        if (bmp != null) {
            binding.ivProfile.setImageBitmap(bmp)
        } else {
            binding.ivProfile.setImageResource(android.R.drawable.ic_menu_myplaces)
            // при желании можно заменить на свой placeholder
            // binding.ivProfile.setImageResource(R.drawable.ic_profile_placeholder)
        }
    }

    private fun loadAllRecipes() {
        db.collection("Recipes")
            .get()
            .addOnSuccessListener { snap ->
                val list = snap.documents.mapNotNull { d ->
                    d.toObject(Recipe::class.java)?.copy(id = d.id)
                }
                if (list.isEmpty()) {
                    binding.tvEmpty.visibility = View.VISIBLE
                    binding.rvFeed.visibility = View.GONE
                } else {
                    binding.tvEmpty.visibility = View.GONE
                    binding.rvFeed.visibility = View.VISIBLE
                    adapter.submitList(list)
                }
            }
            .addOnFailureListener {
                binding.tvEmpty.visibility = View.VISIBLE
                binding.rvFeed.visibility = View.GONE
            }
    }
}