package com.example.smartrecipes.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.smartrecipes.databinding.ActivityAdminDeleteUserBinding
import com.google.firebase.firestore.FirebaseFirestore

class AdminDeleteUserActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminDeleteUserBinding
    private val db = FirebaseFirestore.getInstance()
    private lateinit var adapter: AdminUserAdapter

    private val users = mutableListOf<AdminUser>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminDeleteUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = AdminUserAdapter { user ->
            onUserClick(user)
        }

        binding.rvAdminUsers.layoutManager = LinearLayoutManager(this)
        binding.rvAdminUsers.adapter = adapter

        binding.btnFindUser.setOnClickListener {
            searchUsers()
        }

        binding.btnBackFromDeleteUser.setOnClickListener {
            finish()
        }
    }

    private fun searchUsers() {
        val query = binding.etLogin.text.toString().trim().lowercase()

        db.collection("Users")
            .get()
            .addOnSuccessListener { snap ->
                val all = snap.documents.map { d ->
                    val email = d.getString("email") ?: ""
                    AdminUser(uid = d.id, email = email)
                }

                val filtered = if (query.isEmpty()) {
                    all
                } else {
                    all.filter { it.email.lowercase().contains(query) }
                }

                users.clear()
                users.addAll(filtered)
                adapter.submitList(users.toList())

                binding.tvUserResult.text = "Найдено пользователей: ${users.size}"
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Ошибка: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun onUserClick(user: AdminUser) {
        AlertDialog.Builder(this)
            .setTitle("Удалить пользователя")
            .setMessage("Вы точно хотите удалить пользователя \"${user.email}\"?")
            .setPositiveButton("Удалить") { _, _ ->
                deleteUser(user)
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    private fun deleteUser(user: AdminUser) {
        // удаляем favorites, затем документ пользователя
        val userDoc = db.collection("Users").document(user.uid)

        userDoc.collection("favorites")
            .get()
            .addOnSuccessListener { favSnap ->
                for (fav in favSnap.documents) {
                    fav.reference.delete()
                }

                userDoc.delete()
                    .addOnSuccessListener {
                        Toast.makeText(this, "Пользователь удалён (данные в Firestore)", Toast.LENGTH_SHORT).show()
                        users.removeAll { it.uid == user.uid }
                        adapter.submitList(users.toList())
                        binding.tvUserResult.text = "Найдено пользователей: ${users.size}"
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Ошибка удаления: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Ошибка удаления избранного: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}