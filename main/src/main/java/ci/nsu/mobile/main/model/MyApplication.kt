package ci.nsu.mobile.main

import android.app.Application
import ci.nsu.mobile.main.auth.TokenManager
import ci.nsu.mobile.main.model.ApiService
import ci.nsu.mobile.main.model.AuthRepositoryImpl
import ci.nsu.mobile.main.model.RetrofitClient
import ci.nsu.mobile.main.repository.AuthRepository

class MyApplication : Application() {
    lateinit var authRepository: AuthRepository
    lateinit var tokenManager: TokenManager

    override fun onCreate() {
        super.onCreate()

        tokenManager = TokenManager(this)
        val apiService: ApiService = RetrofitClient.getInstance(tokenManager)
        authRepository = AuthRepositoryImpl(apiService)
    }
}

