package com.example.smartrecipes.ui.data

import android.util.Log
import com.example.smartrecipes.ui.Recipe
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore

object FirebaseFavorites {

    private val db: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
    private val auth: FirebaseAuth
        get() = FirebaseAuth.getInstance()

    // Подколлекция избранного:
    // Users/{uid}/favorites/{recipeId}
    private fun favCollection(): CollectionReference? {
        val user = auth.currentUser
        if (user == null) {
            Log.w("FAVORITES", "favCollection: currentUser is null")
            return null
        }
        return db.collection("Users")
            .document(user.uid)
            .collection("favorites")
    }

    fun add(recipe: Recipe) {
        val col = favCollection() ?: return
        if (recipe.id.isBlank()) {
            Log.w("FAVORITES", "add: recipe.id is blank")
            return
        }

        col.document(recipe.id)
            .set(mapOf("id" to recipe.id))
            .addOnSuccessListener {
                Log.d("FAVORITES", "Added ${recipe.id} to favorites")
            }
            .addOnFailureListener { e ->
                Log.e("FAVORITES", "Failed to add favorite", e)
            }
    }

    fun remove(recipe: Recipe) {
        val col = favCollection() ?: return
        if (recipe.id.isBlank()) {
            Log.w("FAVORITES", "remove: recipe.id is blank")
            return
        }

        col.document(recipe.id)
            .delete()
            .addOnSuccessListener {
                Log.d("FAVORITES", "Removed ${recipe.id} from favorites")
            }
            .addOnFailureListener { e ->
                Log.e("FAVORITES", "Failed to remove favorite", e)
            }
    }

    fun isFavorite(recipeId: String, callback: (Boolean) -> Unit) {
        val col = favCollection() ?: run {
            callback(false)
            return
        }
        if (recipeId.isBlank()) {
            callback(false)
            return
        }

        col.document(recipeId)
            .get()
            .addOnSuccessListener { snap ->
                callback(snap.exists())
            }
            .addOnFailureListener { e ->
                Log.e("FAVORITES", "isFavorite error", e)
                callback(false)
            }
    }

    fun loadFavorites(callback: (List<String>) -> Unit) {
        val col = favCollection() ?: run {
            callback(emptyList())
            return
        }

        col.get()
            .addOnSuccessListener { snap ->
                val ids = snap.documents.map { it.id }
                callback(ids)
            }
            .addOnFailureListener { e ->
                Log.e("FAVORITES", "loadFavorites error", e)
                callback(emptyList())
            }
    }
}