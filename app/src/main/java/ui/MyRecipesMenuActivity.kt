package com.example.smartrecipes.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.smartrecipes.databinding.ActivityMyRecipesMenuBinding

class MyRecipesMenuActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMyRecipesMenuBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyRecipesMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnCreateRecipe.setOnClickListener {
            startActivity(Intent(this, CreateMyRecipeActivity::class.java))
        }

        binding.btnViewMyRecipes.setOnClickListener {
            startActivity(Intent(this, MyRecipesListActivity::class.java))
        }

        binding.btnBackFromMyRecipesMenu.setOnClickListener {
            finish()
        }
    }
}