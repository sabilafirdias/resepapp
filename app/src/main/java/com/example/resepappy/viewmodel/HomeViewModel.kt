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

sealed interface StatusUiHome {
    data class Success(
        val allResep: List<Resep> = emptyList(),
        val makananBerat: List<Resep> = emptyList(),
        val cemilan: List<Resep> = emptyList(),
        val minuman: List<Resep> = emptyList(),
        val selectedCategory: String = "Semua"
    ) : StatusUiHome
    object Error : StatusUiHome
    object Loading : StatusUiHome
}

class HomeViewModel(private val repository: ResepRepository) : ViewModel() {

    var statusUi: StatusUiHome by mutableStateOf(StatusUiHome.Loading)
        private set

    init {
        loadByKategori()
    }

    fun loadByKategori() {
        viewModelScope.launch {
            statusUi = StatusUiHome.Loading
            try {
                val response = repository.getAllResep()
                if (response.isSuccessful) {
                    val dataApi = response.body()?.map { it.toResep() } ?: emptyList()

                    statusUi = StatusUiHome.Success(
                        allResep = dataApi,
                        makananBerat = dataApi.filter { it.kategori == "Makanan Berat" },
                        cemilan = dataApi.filter { it.kategori == "Cemilan" },
                        minuman = dataApi.filter { it.kategori == "Minuman" },
                        selectedCategory = "Semua"
                    )
                } else {
                    statusUi = StatusUiHome.Error
                }
            } catch (e: Exception) {
                statusUi = StatusUiHome.Error
            }
        }
    }

    fun filterByCategory(category: String) {
        val current = statusUi
        if (current is StatusUiHome.Success) {
            // Cukup update label kategori, UI akan otomatis hitung ulang list-nya
            statusUi = current.copy(selectedCategory = category)
        }
    }

    fun toggleBookmark(idUser: Int, idResep: Int) {
        if (idUser <= 0) return

        viewModelScope.launch {
            try {
                val response = repository.toggleBookmark(idUser, idResep)
                if (response.isSuccessful) {
                    loadByKategori()
                } else {
                    statusUi = StatusUiHome.Error
                }
            } catch (e: Exception) {
                statusUi = StatusUiHome.Error
            }
        }
    }
}