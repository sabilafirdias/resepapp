package com.example.resepappy.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.resepappy.modeldata.Resep
import com.example.resepappy.modeldata.toResep
import com.example.resepappy.repositori.ResepRepository
import kotlinx.coroutines.launch

sealed interface DetailUiState {
    object Loading : DetailUiState
    data class Success(val resep: Resep) : DetailUiState
    data class Error(val message: String) : DetailUiState
}

class DetailResepViewModel(private val repository: ResepRepository) : ViewModel() {

    var uiState by mutableStateOf<DetailUiState>(DetailUiState.Loading)
        private set

    var jumlahBookmark by mutableStateOf(0)
        private set

    fun getDetailResep(idResep: Int) {
        viewModelScope.launch {
            uiState = DetailUiState.Loading
            try {
                val response = repository.getResepDetail(idResep)
                if (response.isSuccessful) {
                    val resep = response.body()?.toResep()
                    if (resep != null) {
                        uiState = DetailUiState.Success(resep)
                        fetchJumlahBookmark(idResep)
                    } else {
                        uiState = DetailUiState.Error("Resep tidak ditemukan")
                    }
                } else {
                    uiState = DetailUiState.Error("Gagal mengambil data: ${response.message()}")
                }
            } catch (e: Exception) {
                uiState = DetailUiState.Error(e.message ?: "Terjadi kesalahan")
            }
        }
    }

    private fun fetchJumlahBookmark(idResep: Int) {
        viewModelScope.launch {
            try {
                // Menggunakan getCountBookmarks yang mengembalikan Response<Int>
                val response = repository.getCountBookmarks(idResep)
                if (response.isSuccessful) {
                    jumlahBookmark = response.body() ?: 0
                }
            } catch (e: Exception) {
                // Opsional: Log error jika gagal ambil jumlah bookmark
            }
        }
    }

    suspend fun hapusResep(idResep: Int): Boolean {
        return try {
            repository.hapusResep(idResep).isSuccessful
        } catch (e: Exception) {
            false
        }
    }
}
