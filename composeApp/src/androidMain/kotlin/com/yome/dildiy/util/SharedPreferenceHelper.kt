package com.yome.dildiy.util

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.yome.dildiy.remote.dto.User
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

// Helper object for SharedPreferences
object PreferencesHelper {
    private const val PREFS_NAME = "CartPreferences"
    private const val CART_COUNT_KEY = "cart_count"
    private const val USER_DATA_KEY = "user_data"

    private const val PREFS_NAME2 = "app_preferences"
    private const val JWT_TOKEN_KEY = "jwt_token"

    fun saveJwtToken(context: Context, token: String) {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREFS_NAME2, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(JWT_TOKEN_KEY, "Bearer "+ token)
        editor.apply()
    }

    fun getJwtToken(context: Context): String? {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREFS_NAME2, Context.MODE_PRIVATE)
        return sharedPreferences.getString(JWT_TOKEN_KEY, null)
    }

    fun clearJwtToken(context: Context) {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREFS_NAME2, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.remove(JWT_TOKEN_KEY)
        editor.commit()
        val token = getJwtToken(context)
        Log.d("PreferencesHelper", "Token after clearing: $token")
    }

    // Function to get SharedPreferences instance
    private fun getPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    // Function to get cart count
    fun getCartCount(context: Context): Int {
        return getPreferences(context).getInt(CART_COUNT_KEY, 0) // Default to 0 if not set
    }

    // Function to save cart count
    fun setCartCount(context: Context, count: Int) {
        getPreferences(context).edit().putInt(CART_COUNT_KEY, count).apply()
    }

    fun getUsername(context: Context): String? {
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val userJson = sharedPreferences.getString(USER_DATA_KEY, null)
        return if (userJson != null) {
            try {
                // Deserialize the JSON string back into a User object
                val user = Json.decodeFromString<User>(userJson) // Assuming `User` is serializable
                user.username // Return the ID
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        } else {
            null // No user data found
        }
    }

    fun getUsername2(context: Context): User? {
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val userJson = sharedPreferences.getString(USER_DATA_KEY, null)
        return if (userJson != null) {
            try {
                // Deserialize the JSON string back into a User object
                val user = Json.decodeFromString<User>(userJson) // Assuming `User` is serializable
                user
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        } else {
            null // No user data found
        }
    }

    // Function to save User object (you'll need to serialize it to JSON first)
    fun saveUser(context: Context, user: User) {
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            // Serialize User object to JSON and store it
            val userJson = Json.encodeToString(user) // Assuming Users class is serializable
            putString(USER_DATA_KEY, userJson)
            apply()
        }
    }

}
