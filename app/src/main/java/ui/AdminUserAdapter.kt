package com.example.smartrecipes.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.smartrecipes.databinding.ItemAdminUserBinding

data class AdminUser(
    val uid: String,
    val email: String
)

class AdminUserAdapter(
    private val onClick: (AdminUser) -> Unit
) : RecyclerView.Adapter<AdminUserAdapter.UserViewHolder>() {

    private val items = mutableListOf<AdminUser>()

    fun submitList(list: List<AdminUser>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    inner class UserViewHolder(val binding: ItemAdminUserBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(user: AdminUser) {
            binding.tvEmail.text = user.email.ifBlank { "(email не указан)" }
            binding.tvUid.text = "UID: ${user.uid}"

            binding.root.setOnClickListener {
                onClick(user)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = ItemAdminUserBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return UserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size
}