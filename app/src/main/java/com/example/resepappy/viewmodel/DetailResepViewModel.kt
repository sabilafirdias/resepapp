package com.example.resepappy.viewmodel

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.resepappy.modeldata.Resep
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
                val response = repository.getResepByUserId(idResep) // Pastikan fungsi ini ada di repository
                if (response.isSuccessful) {
                    val resep = response.body()
                    if (resep != null) {
                        uiState = DetailUiState.Success(resep)
                        // Ambil jumlah bookmark jika ada endpointnya
                        fetchJumlahBookmark(idResep)
                    }
                }
            } catch (e: Exception) {
                uiState = DetailUiState.Error(e.message ?: "Terjadi kesalahan")
            }
        }
    }

    private fun fetchJumlahBookmark(idResep: Int) {
        viewModelScope.launch {
            try {
                val response = repository.getBookmarks(idResep)
                if (response.isSuccessful) jumlahBookmark = response.body() ?: 0
            } catch (e: Exception) { }
        }
    }

    suspend fun hapusResep(idResep: Int): Boolean {
        return try {
            repository.hapusResep(idResep).isSuccessful
        } catch (e: Exception) { false }
    }
}
