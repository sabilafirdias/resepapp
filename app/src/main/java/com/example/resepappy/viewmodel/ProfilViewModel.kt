package com.example.resepappy.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.resepappy.modeldata.EditProfilRequest
import com.example.resepappy.modeldata.User
import com.example.resepappy.modeldata.toResep
import com.example.resepappy.repositori.ResepRepository
import com.example.resepappy.modeldata.Resep
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

sealed interface StatusUiProfil {
    data class ProfilLoaded(val profil: User) : StatusUiProfil
    object Loading : StatusUiProfil
    object Success : StatusUiProfil
    data class Error(val message: String) : StatusUiProfil
}

class ProfilViewModel(
    private val repository: ResepRepository
) : ViewModel() {

    var statusUi by mutableStateOf<StatusUiProfil>(StatusUiProfil.Loading)
        private set

    var isEditing by mutableStateOf(false)
        private set

    var currentProfil by mutableStateOf<User?>(null)
        private set

    var listResepUser by mutableStateOf<List<Resep>>(emptyList())
        private set

    var editUsername by mutableStateOf("")
    var editEmail by mutableStateOf("")
    var oldPassword by mutableStateOf("")
    var newPassword by mutableStateOf("")
    var confirmPassword by mutableStateOf("")

    var errorMessage by mutableStateOf("")
        private set
    var successMessage by mutableStateOf("")

    var listBookmarkUser by mutableStateOf<List<Resep>>(emptyList())
        private set

    fun clearMessages() {
        errorMessage = ""
        successMessage = ""
    }

    fun loadProfil(idUser: Int) {
        viewModelScope.launch {
            statusUi = StatusUiProfil.Loading
            statusUi = try {
                val response = repository.getProfil(idUser)
                if (response.isSuccessful) {
                    val profil = response.body()!!
                    currentProfil = profil
                    resetFields(profil)
                    StatusUiProfil.ProfilLoaded(profil)
                } else {
                    StatusUiProfil.Error("Gagal memuat profil")
                }
            } catch (e: IOException) {
                StatusUiProfil.Error("Tidak ada koneksi internet")
            } catch (e: HttpException) {
                StatusUiProfil.Error("Server error")
            }
        }
    }

    fun loadResepUser(idUser: Int) {
        viewModelScope.launch {
            try {
                val response = repository.getResepByUserId(idUser) // Pastikan fungsi ini ada di repository
                if (response.isSuccessful) {
                    listResepUser = response.body() ?: emptyList()
                }
            } catch (e: Exception) { }
        }
    }

    fun toggleBookmark(idUser: Int, idResep: Int) {
        viewModelScope.launch {
            try {
                val response = repository.toggleBookmark(idUser, idResep)
                if (response.isSuccessful) {
                    // Refresh list bookmark setelah status berubah di server
                    loadBookmarkUser(idUser)
                }
            } catch (e: Exception) {
                // Biarkan kosong sesuai gaya codingan Anda
            }
        }
    }

    fun loadBookmarkUser(idUser: Int) {
        viewModelScope.launch {
            try {
                val response = repository.getBookmarks(idUser)
                if (response.isSuccessful) {
                    listBookmarkUser = response.body()?.map { it.toResep() } ?: emptyList()
                }
            } catch (e: Exception) {
                listBookmarkUser = emptyList()
            }
        }
    }

    private fun resetFields(profil: User) {
        editUsername = profil.username
        editEmail = profil.email
        oldPassword = ""
        newPassword = ""
        confirmPassword = ""
    }

    fun startEdit() {
        isEditing = true
        errorMessage = ""
    }

    fun cancelEdit() {
        isEditing = false
        currentProfil?.let { resetFields(it) }
        errorMessage = ""
    }

    suspend fun saveChanges(idUser: Int): Boolean {
        errorMessage = ""
        successMessage = ""

        if (editUsername.isBlank()) {
            errorMessage = "Username wajib diisi"
            return false
        }

        val emailPattern = "^[A-Za-z0-9+_.-]+@[A-za-z0-9.-]+\\.[a-z]+$"
        if (!editEmail.matches(emailPattern.toRegex())) {
            errorMessage = "Format email tidak valid"
            return false
        }

        if (oldPassword.isNotBlank() || newPassword.isNotBlank() || confirmPassword.isNotBlank()) {
            if (oldPassword.isBlank()) {
                errorMessage = "Password lama diperlukan untuk keamanan"
                return false
            }
            if (newPassword.length < 8) {
                errorMessage = "Password baru minimal 8 karakter"
                return false
            }
            if (newPassword != confirmPassword) {
                errorMessage = "Konfirmasi password baru tidak cocok"
                return false
            }
        }

        return try {
            val request = EditProfilRequest(
                id_user = idUser,
                username = editUsername,
                email = editEmail,
                old_password = oldPassword.ifBlank { null },
                new_password = newPassword.ifBlank { null }
            )

            val response = repository.updateProfil(request)
            if (response.isSuccessful) {
                successMessage = "Profil berhasil diperbarui!"
                isEditing = false
                val refresh = repository.getProfil(idUser)
                if (refresh.isSuccessful) {
                    currentProfil = refresh.body()
                    currentProfil?.let { resetFields(it) }
                }
                true
            } else {
                errorMessage = response.errorBody()?.string() ?: "Gagal memperbarui profil"
                false
            }
        } catch (e: IOException) {
            errorMessage = "Gagal terhubung ke server"
            false
        } catch (e: Exception) {
            errorMessage = "Terjadi kesalahan: ${e.message}"
            false
        }
    }

    suspend fun deleteAccount(idUser: Int): Boolean {
        return try {
            val response = repository.deleteAkun(idUser)
            response.isSuccessful && response.body()?.message != null
        } catch (e: Exception) {
            errorMessage = "Gagal menghapus akun"
            false
        }
    }
}