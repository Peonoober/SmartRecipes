package com.example.smartrecipes.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.smartrecipes.databinding.ActivityProfileBinding
import com.example.smartrecipes.ui.data.AvatarStorage
import com.google.firebase.auth.FirebaseAuth

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private val auth = FirebaseAuth.getInstance()

    companion object {
        private const val PICK_IMAGE_REQUEST = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val email = auth.currentUser?.email ?: CurrentUser.login ?: "Неизвестно"
        binding.tvEmail.text = "Email: $email"

        loadAvatarIntoView()

        binding.btnChangePhoto.setOnClickListener {
            openImagePicker()
        }

        binding.btnCreateRecipe.setOnClickListener {
            startActivity(Intent(this, CreateMyRecipeActivity::class.java))
        }

        binding.btnMyRecipes.setOnClickListener {
            startActivity(Intent(this, MyRecipesListActivity::class.java))
        }

        binding.btnFavorites.setOnClickListener {
            startActivity(Intent(this, FavoritesActivity::class.java))
        }

        binding.btnBackFromProfile.setOnClickListener {
            finish()
        }
    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        try {
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        } catch (e: Exception) {
            Toast.makeText(this, "Не удалось открыть галерею: ${e.message}", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun loadAvatarIntoView() {
        val bmp = AvatarStorage.loadBitmap(this)
        if (bmp != null) {
            binding.ivAvatar.setImageBitmap(bmp)
        } else {
            // заглушка по умолчанию
            binding.ivAvatar.setImageResource(android.R.drawable.ic_menu_myplaces)
            // если хочешь свой значок:
            // binding.ivAvatar.setImageResource(R.drawable.ic_profile_placeholder)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            val uri = data.data
            if (uri != null) {
                AvatarStorage.saveFromUri(this, uri)
                loadAvatarIntoView()
                Toast.makeText(this, "Фото профиля обновлено", Toast.LENGTH_SHORT).show()
            }
        }
    }
}