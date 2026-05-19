package com.example.yourapp.repository

import com.example.yourapp.model.*
import com.example.yourapp.network.ApiService
import retrofit2.HttpException
import java.io.IOException

class AuthRepositoryImpl(
    private val apiService: ApiService
) : AuthRepository {

    override suspend fun login(login: String, password: String): Result<LoginResponse> {
        return try {
            val response = apiService.login(LoginRequest(login, password))
            Result.success(response)
        } catch (e: HttpException) {
            // Обработка HTTP ошибок от сервера
            val errorMessage = when (e.code()) {
                401 -> "Invalid login or password"
                400 -> "Invalid request format"
                500 -> "Server error, please try again later"
                else -> "Login failed: ${e.message()}"
            }
            Result.failure(Exception(errorMessage))
        } catch (e: IOException) {
            Result.failure(Exception("Network error. Check your connection to 192.168.200.160"))
        } catch (e: Exception) {
            Result.failure(Exception("Unexpected error: ${e.message}"))
        }
    }

    override suspend fun register(request: RegisterRequest): Result<Unit> {
        return try {
            apiService.register(request)
            Result.success(Unit)
        } catch (e: HttpException) {
            val errorMessage = when (e.code()) {
                409 -> "User with this login already exists"
                400 -> "Invalid registration data"
                500 -> "Server error"
                else -> "Registration failed: ${e.message()}"
            }
            Result.failure(Exception(errorMessage))
        } catch (e: IOException) {
            Result.failure(Exception("Network error. Cannot reach server at 192.168.200.160"))
        } catch (e: Exception) {
            Result.failure(Exception("Registration error: ${e.message}"))
        }
    }

    override suspend fun getUsers(): Result<List<UserDto>> {
        return try {
            val users = apiService.getUsers()
            Result.success(users)
        } catch (e: HttpException) {
            when (e.code()) {
                401 -> Result.failure(Exception("Session expired. Please login again"))
                403 -> Result.failure(Exception("Access denied"))
                else -> Result.failure(Exception("Failed to load users: ${e.message()}"))
            }
        } catch (e: IOException) {
            Result.failure(Exception("Network error. Cannot connect to server"))
        } catch (e: Exception) {
            Result.failure(Exception("Error loading users: ${e.message}"))
        }
    }

    override suspend fun getGroups(): Result<List<GroupDto>> {
        return try {
            val groups = apiService.getGroups()
            Result.success(groups)
        } catch (e: HttpException) {
            Result.failure(Exception("Failed to load groups: ${e.message()}"))
        } catch (e: IOException) {
            Result.failure(Exception("Network error loading groups"))
        } catch (e: Exception) {
            Result.failure(Exception("Error loading groups: ${e.message}"))
        }
    }
}