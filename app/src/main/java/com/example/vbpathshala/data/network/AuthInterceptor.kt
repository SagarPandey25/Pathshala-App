package com.example.vbpathshala.data.network.AuthInterceptor

import android.content.Context
import android.util.Log
import com.example.vbpathshala.data.session.SessionManager
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val context: Context) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {

        val token = SessionManager.getToken(context)

        Log.d("AuthInterceptor", "Token: $token")

        val request = if (!token.isNullOrEmpty()) {
            Log.d("AuthInterceptor", "Adding Authorization Header")
            chain.request()
                .newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        } else {
            Log.d("AuthInterceptor", "Token is null or empty")
            chain.request()
        }

        return chain.proceed(request)
    }
}
