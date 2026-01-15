package com.example.resepappy.modeldata

import kotlinx.serialization.Serializable

@Serializable
data class RegisterRequest(
    val username: String,
    val email: String,
    val password: String
)

@Serializable
data class OperationResponse(
    val message: String? = null,
    val error: String? = null
)

@Serializable
data class UniqueCheckResponse(
    val email_exists: Boolean = false,
    val username_exists: Boolean = false
)

@Serializable
data class LoginRequest(
    val email: String,
    val password: String
)

@Serializable
data class LoginResponse(
    val message: String? = null,
    val error: String? = null,
    val user: User? = null
)

@Serializable
data class User(
    val id_user: Int,
    val username: String,
    val email: String
)

@Serializable
data class EditProfilRequest(
    val id_user: Int,
    val username: String,
    val email: String,
    val old_password: String? = null, // Hanya diisi jika ganti password
    val new_password: String? = null  // Hanya diisi jika ganti password
)

// Untuk form input UI
data class DetailUser(
    val username: String = "",
    val email: String = "",
    val password: String = ""
    // TIDAK ADA id_user! (belum ada saat input)
)

// State UI untuk form
data class UIStateUser(
    val detailUser: DetailUser = DetailUser(),
    val usernameError: String? = null,
    val emailError: String? = null,
    val passwordError: String? = null,
    val passwordConfirmError: String? = null,
    val errorMessage: String? = null,
    val isLoginMode: Boolean = false
) {
    val isEntryValid: Boolean = when {
        isLoginMode -> {
            emailError == null &&
            passwordError == null &&
            detailUser.email.isNotBlank() &&
            detailUser.password.isNotBlank()
        }
        else -> {
            usernameError == null &&
            emailError == null &&
            passwordError == null &&
            passwordConfirmError == null &&
            detailUser.username.isNotBlank() &&
            detailUser.email.isNotBlank() &&
            detailUser.password.isNotBlank()
        }
    }
}

// Konversi dari form ke request register
fun DetailUser.toRegisterRequest() = RegisterRequest(
    username = username,
    email = email,
    password = password
)

// Konversi dari response API ke UI state
fun User.toUiStateUser(isEntryValid: Boolean = false) = UIStateUser(
    detailUser = DetailUser(
        username = username,
        email = email
    )
)