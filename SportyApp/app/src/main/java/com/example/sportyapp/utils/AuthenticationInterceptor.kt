package com.example.sportyapp.utils

import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

class AuthenticationInterceptor() : Interceptor {

    private val login: String = "admin"
    private val password: String = "admin"
    private val credentials: String = okhttp3.Credentials.basic(login, password)

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val authenticatedRequest = request.newBuilder()
            .header("Authorization", credentials).build()
        return chain.proceed(authenticatedRequest)
    }

}