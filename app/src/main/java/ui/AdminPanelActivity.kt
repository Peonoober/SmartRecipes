package com.example.smartrecipes.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.smartrecipes.databinding.ActivityAdminPanelBinding
import com.google.firebase.auth.FirebaseAuth

class AdminPanelActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminPanelBinding
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminPanelBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnCreateRecipe.setOnClickListener {
            startActivity(Intent(this, AdminCreateRecipeActivity::class.java))
        }

        binding.btnDeleteRecipe.setOnClickListener {
            startActivity(Intent(this, AdminDeleteRecipeActivity::class.java))
        }

        binding.btnDeleteUser.setOnClickListener {
            startActivity(Intent(this, AdminDeleteUserActivity::class.java))
        }

        // Кнопка "Назад" — выходим из аккаунта и возвращаемся к экрану авторизации
        binding.btnBackFromAdmin.setOnClickListener {
            auth.signOut()
            CurrentUser.login = null

            val intent = Intent(this, LoginActivity::class.java).apply {
                // очищаем back stack, чтобы нельзя было вернуться кнопкой "Назад"
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            startActivity(intent)
        }
    }
}