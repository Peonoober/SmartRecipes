package com.example.smartrecipes.ui

import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.smartrecipes.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        binding.btnCreateAccount.setOnClickListener {
            val email = binding.etNewLogin.text.toString().trim()
            val pass = binding.etNewPassword.text.toString().trim()

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Введите корректный email", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (pass.length < 6) {
                Toast.makeText(this, "Пароль ≥ 6 символов", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (email == pass) {
                Toast.makeText(this, "Email и пароль не должны совпадать", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.createUserWithEmailAndPassword(email, pass)
                .addOnSuccessListener { result ->
                    val user = result.user
                    val uid = user!!.uid

                    val userData = hashMapOf(
                        "email" to email,
                        "isAdmin" to false
                    )

                    db.collection("Users").document(uid)
                        .set(userData)
                        .addOnSuccessListener {
                            // Отправляем письмо с подтверждением
                            user.sendEmailVerification()
                                .addOnSuccessListener {
                                    Toast.makeText(
                                        this,
                                        "Аккаунт создан. Подтвердите email: $email",
                                        Toast.LENGTH_LONG
                                    ).show()
                                    // выходим из аккаунта, чтобы нельзя было войти без подтверждения
                                    auth.signOut()
                                    finish()
                                }
                                .addOnFailureListener { e ->
                                    Toast.makeText(
                                        this,
                                        "Ошибка отправки письма: ${e.message}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(
                                this,
                                "Ошибка сохранения профиля: ${e.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Ошибка создания аккаунта: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }

        binding.btnBack.setOnClickListener { finish() }
    }
}