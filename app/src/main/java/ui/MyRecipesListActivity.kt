package com.example.smartrecipes.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.smartrecipes.databinding.ActivityMyRecipesListBinding
import com.example.smartrecipes.ui.data.LocalRecipes

class MyRecipesListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMyRecipesListBinding
    private lateinit var adapter: RecipeAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyRecipesListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = RecipeAdapter { recipe ->
            val intent = Intent(this, DetailsRecipeActivity::class.java)
            intent.putExtra("recipe", recipe)
            startActivity(intent)
        }

        binding.rvMyRecipes.layoutManager = LinearLayoutManager(this)
        binding.rvMyRecipes.adapter = adapter

        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        val list = LocalRecipes.getAll(this)
        adapter.submitList(list)
    }
}