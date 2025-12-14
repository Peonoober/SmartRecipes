package com.example.smartrecipes.ui.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import java.io.ByteArrayOutputStream

object AvatarStorage {

    private const val PREF_NAME = "avatar_prefs"
    private const val KEY_AVATAR_BASE64 = "avatar_base64"

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    /** Сохранить изображение из Uri в SharedPreferences (как Base64) */
    fun saveFromUri(context: Context, uri: Uri) {
        try {
            context.contentResolver.openInputStream(uri).use { input ->
                if (input != null) {
                    val original = BitmapFactory.decodeStream(input) ?: return

                    // уменьшаем до 256x256, чтобы не занимать много места
                    val scaled = Bitmap.createScaledBitmap(
                        original,
                        256,
                        256,
                        true
                    )

                    val baos = ByteArrayOutputStream()
                    scaled.compress(Bitmap.CompressFormat.PNG, 90, baos)
                    val bytes = baos.toByteArray()
                    val base64 = Base64.encodeToString(bytes, Base64.DEFAULT)

                    prefs(context).edit().putString(KEY_AVATAR_BASE64, base64).apply()
                }
            }
        } catch (_: Exception) {
            // если что-то пошло не так — просто не сохраняем
        }
    }

    /** Загрузить сохранённый аватар как Bitmap, или null, если нет */
    fun loadBitmap(context: Context): Bitmap? {
        return try {
            val base64 = prefs(context).getString(KEY_AVATAR_BASE64, null) ?: return null
            val bytes = Base64.decode(base64, Base64.DEFAULT)
            BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        } catch (e: Exception) {
            null
        }
    }

    fun clear(context: Context) {
        prefs(context).edit().remove(KEY_AVATAR_BASE64).apply()
    }
}