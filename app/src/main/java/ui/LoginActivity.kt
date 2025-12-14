package com.example.smartrecipes.ui

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.smartrecipes.databinding.ActivityLoginBinding
import com.example.smartrecipes.ui.data.LocalAuth
import com.example.smartrecipes.ui.data.ThemeManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

object CurrentUser {
    var login: String? = null   // здесь храним email
}

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        // применяем тему
        ThemeManager.applyTheme(this)

        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        // Автовход только если email подтвержден
        val current = auth.currentUser
        if (current != null && current.isEmailVerified) {
            db.collection("Users").document(current.uid).get()
                .addOnSuccessListener { doc ->
                    val isAdmin = doc.getBoolean("isAdmin") == true
                    val intent = if (isAdmin) {
                        Intent(this, AdminPanelActivity::class.java)
                    } else {
                        Intent(this, MainMenuActivity::class.java)
                    }
                    startActivity(intent)
                    finish()
                }
                .addOnFailureListener {
                    startActivity(Intent(this, MainMenuActivity::class.java))
                    finish()
                }
            return
        }

        binding.btnLogin.setOnClickListener {
            val email = binding.etLogin.text.toString().trim()
            val pass = binding.etPassword.text.toString().trim()

            if (email.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Введите email и пароль", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Некорректный email", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.signInWithEmailAndPassword(email, pass)
                .addOnSuccessListener { result ->
                    val user = result.user
                    if (user != null && user.isEmailVerified) {
                        val uid = user.uid

                        // сохраняем для офлайн-входа
                        CurrentUser.login = email
                        LocalAuth.saveUser(this, email, pass)

                        // читаем Users/{uid} и проверяем isAdmin
                        db.collection("Users").document(uid).get()
                            .addOnSuccessListener { doc ->
                                val isAdmin = doc.getBoolean("isAdmin") == true
                                val intent = if (isAdmin) {
                                    Intent(this, AdminPanelActivity::class.java)
                                } else {
                                    Intent(this, MainMenuActivity::class.java)
                                }
                                startActivity(intent)
                                finish()
                            }
                            .addOnFailureListener {
                                // если не удалось прочитать doc – считаем обычным пользователем
                                startActivity(Intent(this, MainMenuActivity::class.java))
                                finish()
                            }
                    } else {
                        auth.signOut()
                        Toast.makeText(
                            this,
                            "Подтвердите email. Проверьте почту.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
                .addOnFailureListener { e ->
                    // офлайн-вход: всегда обычный пользователь
                    if (LocalAuth.checkUser(this, email, pass)) {
                        CurrentUser.login = email
                        Toast.makeText(this, "Офлайн-вход", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, MainMenuActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(this, "Ошибка входа: ${e.message}", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
        }

        binding.btnRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }
}