package com.example.resepappy.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.resepappy.modeldata.*
import com.example.resepappy.repositori.ResepRepository

class AuthViewModel(
    private val resepRepository: ResepRepository,
) : ViewModel() {

    var uiStateUser by mutableStateOf(UIStateUser())
        private set

    var isLoading by mutableStateOf(false)
        private set

    var loginError by mutableStateOf("")
        private set

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun isStrongPassword(password: String): Boolean {
        return password.length >= 8 &&
                password.any { it.isUpperCase() } &&
                password.any { it.isLowerCase() } &&
                password.any { it.isDigit() }
    }

    private fun validasiInputRegister(): Boolean {
        val u = uiStateUser.detailUser
        return u.username.isNotBlank() &&
                isValidEmail(u.email) &&
                isStrongPassword(u.password)
    }

    private fun validasiInputLogin(): Boolean {
        val detail = uiStateUser.detailUser
        return detail.email.isNotBlank() &&
                detail.password.isNotBlank()
    }

    fun updateUsername(username: String) {
        uiStateUser = uiStateUser.copy(
            detailUser = uiStateUser.detailUser.copy(username = username),
            usernameError = if (username.isBlank()) "Username wajib diisi" else null
        )
    }

    fun setLoginMode() {
        uiStateUser = uiStateUser.copy(isLoginMode = true)
    }

    fun updateEmail(email: String) {
        uiStateUser = uiStateUser.copy(
            detailUser = uiStateUser.detailUser.copy(email = email),
            emailError = when {
                email.isBlank() -> "Email wajib diisi"
                !isValidEmail(email) -> "Format email tidak valid"
                else -> null
            }
        )
    }

    fun updatePassword(password: String) {
        uiStateUser = uiStateUser.copy(
            detailUser = uiStateUser.detailUser.copy(password = password),
            passwordError = when {
                password.isBlank() -> "Password wajib diisi"
                !isStrongPassword(password) && !uiStateUser.isLoginMode ->
                    "Minimal 8 karakter, 1 huruf besar, 1 angka"
                else -> null
            }
        )
    }

    suspend fun checkUsernameAvailability(username: String) {
        if (username.isBlank()) return

        try {
            val response = resepRepository.checkUnique(username, "")

            if (response.username_exists) {
                uiStateUser = uiStateUser.copy(
                    usernameError = "Username '$username' sudah dipakai. Coba yang lain!"
                )
            } else {
                // Jika tidak ada, hapus error
                uiStateUser = uiStateUser.copy(usernameError = null)
            }

        } catch (e: Exception) {
            uiStateUser = uiStateUser.copy(
                usernameError = "Tidak bisa memeriksa username. Cek koneksi internet Anda."
            )
        }
    }

    suspend fun register(): Boolean {
        updateUsername(uiStateUser.detailUser.username)
        updateEmail(uiStateUser.detailUser.email)
        updatePassword(uiStateUser.detailUser.password)

        if (!uiStateUser.isEntryValid) {
            uiStateUser = uiStateUser.copy(
                errorMessage = "Perbaiki kesalahan pada form"
            )
            return false
        }

        if (!validasiInputRegister()) return false

        isLoading = true
        uiStateUser = uiStateUser.copy(errorMessage = null)

        return try {
            val user = uiStateUser.detailUser

            when {
                user.username.isBlank() -> {
                    uiStateUser = uiStateUser.copy(errorMessage = "Username wajib diisi")
                    return false
                }
                !isValidEmail(user.email) -> {
                    uiStateUser = uiStateUser.copy(errorMessage = "Format email tidak valid")
                    return false
                }
                !isStrongPassword(user.password) -> {
                    uiStateUser = uiStateUser.copy(
                        errorMessage = "Password minimal 8 karakter dan harus mengandung huruf besar, huruf kecil, dan angka"
                    )
                    return false
                }
            }

            val check = resepRepository.checkUnique(
                user.username,
                user.email
            )

            when {
                check.email_exists -> {
                    uiStateUser = uiStateUser.copy(errorMessage = "Email sudah terdaftar")
                    return false
                }
                check.username_exists -> {
                    uiStateUser = uiStateUser.copy(errorMessage = "Username sudah dipakai")
                    return false
                }
            }

            val response = resepRepository.register(user.toRegisterRequest())

            if (response.isSuccessful) {
                val body = response.body()
                if (body?.error != null) {
                    uiStateUser = uiStateUser.copy(errorMessage = body.error)
                    false
                } else {
                    true
                }
            } else {
                uiStateUser = uiStateUser.copy(
                    errorMessage = "Register gagal (${response.code()})"
                )
                false
            }

        } catch (e: Exception) {
            uiStateUser = uiStateUser.copy(
                errorMessage = "Error: ${e.message}"
            )
            false
        } finally {
            isLoading = false
        }
    }

    suspend fun login(): User? {
        if (!validasiInputLogin()) {
            loginError = "Email dan password wajib diisi"
            return null
        }

        isLoading = true
        loginError = ""

        return try {
            val request = LoginRequest(
                email = uiStateUser.detailUser.email,
                password = uiStateUser.detailUser.password
            )

            val response = resepRepository.login(request)

            if (response.isSuccessful) {
                val body = response.body()
                if (body?.user != null) {
                    return body.user
                } else {
                    loginError = body?.error ?: "Login gagal"
                    null
                }

            } else {
                // ⬇️ BACA ERROR JSON DARI SERVER
                val errorJson = response.errorBody()?.string()

                if (errorJson != null && errorJson.contains("error")) {
                    val msg = errorJson.substringAfter("\"error\":\"").substringBefore("\"")
                    loginError = msg
                } else {
                    loginError = "Server error (${response.code()})"
                }
                null
            }

        } catch (e: Exception) {
            loginError = "Tidak bisa terhubung ke server"
            null
        } finally {
            isLoading = false
        }
    }
}