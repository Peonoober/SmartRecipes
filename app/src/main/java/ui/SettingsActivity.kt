package com.example.smartrecipes.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.smartrecipes.databinding.ActivitySettingsBinding
import com.example.smartrecipes.ui.data.ThemeManager
import com.google.firebase.auth.FirebaseAuth

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        // Применяем текущую тему
        ThemeManager.applyTheme(this)

        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Инициализируем switch исходя из сохранённого значения
        val isDark = ThemeManager.isDark(this)
        binding.switchTheme.isChecked = isDark

        binding.switchTheme.setOnCheckedChangeListener { _, checked ->
            // меняем тему и пересоздаём активити
            ThemeManager.setDark(this, checked)
            // пересоздаём весь экран, чтобы применились цвета
            recreate()
        }

        // Выйти из аккаунта
        binding.btnLogoutAccount.setOnClickListener {
            auth.signOut()
            CurrentUser.login = null

            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        binding.btnBackFromSettings.setOnClickListener {
            finish()
        }
    }
}