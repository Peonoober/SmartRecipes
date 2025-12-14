package com.example.smartrecipes.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.smartrecipes.databinding.ActivityFavoritesBinding
import com.example.smartrecipes.ui.data.LocalFavorites

class FavoritesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFavoritesBinding
    private lateinit var adapter: RecipeAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavoritesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = RecipeAdapter { recipe ->
            val intent = Intent(this, DetailsRecipeActivity::class.java)
            intent.putExtra("recipe", recipe)
            startActivity(intent)
        }

        binding.rvFavorites.layoutManager = LinearLayoutManager(this)
        binding.rvFavorites.adapter = adapter

        binding.btnBack.setOnClickListener { finish() }
    }

    override fun onResume() {
        super.onResume()
        val list = LocalFavorites.getAll(this)
        adapter.submitList(list)
    }
}