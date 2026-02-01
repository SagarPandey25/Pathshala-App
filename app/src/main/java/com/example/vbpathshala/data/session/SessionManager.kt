package com.example.vbpathshala.data.session

import android.content.Context

/* ---------------- USER MODEL ---------------- */
data class User(
    val id: String,
    val first_name: String,
    val last_name: String,
    val email: String,
    val role: String,
    val created_at: String
)

/* ---------------- SESSION MANAGER ---------------- */
object SessionManager {

    private const val PREF_NAME = "user_session"

    private const val KEY_TOKEN = "jwt_token"
    private const val KEY_LOGIN = "isLoggedIn"

    private const val KEY_USER_ID = "user_id"
    private const val KEY_FIRST_NAME = "first_name"
    private const val KEY_LAST_NAME = "last_name"
    private const val KEY_EMAIL = "email"
    private const val KEY_ROLE = "role"
    private const val KEY_CREATED_AT = "created_at"

    /* SAVE LOGIN SESSION */
    fun saveSession(
        context: Context,
        token: String,
        user: com.example.vbpathshala.ui.auth.login.User
    ) {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_TOKEN, token)
            .putString(KEY_USER_ID, user.id)
            .putString(KEY_FIRST_NAME, user.first_name)
            .putString(KEY_LAST_NAME, user.last_name)
            .putString(KEY_EMAIL, user.email)
            .putString(KEY_ROLE, user.role)
            .putString(KEY_CREATED_AT, user.created_at)
            .putBoolean(KEY_LOGIN, true)
            .apply()
    }

    /* GET USER DETAILS */
    fun getUser(context: Context): User {
        val pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return User(
            id = pref.getString(KEY_USER_ID, "") ?: "",
            first_name = pref.getString(KEY_FIRST_NAME, "") ?: "",
            last_name = pref.getString(KEY_LAST_NAME, "") ?: "",
            email = pref.getString(KEY_EMAIL, "") ?: "",
            role = pref.getString(KEY_ROLE, "") ?: "",
            created_at = pref.getString(KEY_CREATED_AT, "") ?: ""
        )
    }

    /* GET TOKEN */
    fun getToken(context: Context): String? =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .getString(KEY_TOKEN, null)

    /* CHECK LOGIN */
    fun isLoggedIn(context: Context): Boolean =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .getBoolean(KEY_LOGIN, false)

    /* LOGOUT */
    fun clearSession(context: Context) {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .edit()
            .clear()
            .apply()
    }
}
