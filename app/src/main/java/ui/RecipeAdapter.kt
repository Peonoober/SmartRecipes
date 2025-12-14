package com.example.smartrecipes.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.smartrecipes.databinding.ItemRecipeCardBinding

class RecipeAdapter(
    private val onClick: (Recipe) -> Unit
) : RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder>() {

    private val items = mutableListOf<Recipe>()

    fun submitList(list: List<Recipe>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    inner class RecipeViewHolder(val binding: ItemRecipeCardBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(r: Recipe) {
            binding.tvName.text = r.name
            binding.tvDescription.text = r.subtitle

            binding.root.setOnClickListener {
                onClick(r)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val binding = ItemRecipeCardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return RecipeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size
}
