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

    fun loadByKategori(idUser: Int? = null) {
        viewModelScope.launch {
            statusUi = StatusUiHome.Loading
            try {
                val responseResep = repository.getAllResep()
                val bookmarkIds = if (idUser != null) {
                    val responseBookmarks = repository.getBookmarks(idUser)
                    if (responseBookmarks.isSuccessful) {
                        responseBookmarks.body()?.map { it.id_resep }?.toSet() ?: emptySet()
                    } else {
                        emptySet()
                    }
                } else {
                    emptySet()
                }

                if (responseResep.isSuccessful) {
                    val dataApi = responseResep.body()?.map { response ->
                        val resep = response.toResep()
                        resep.copy(is_bookmarked = bookmarkIds.contains(resep.id_resep))
                    } ?: emptyList()

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
            statusUi = current.copy(selectedCategory = category)
        }
    }

    fun toggleBookmark(idUser: Int, idResep: Int) {
        viewModelScope.launch {
            try {
                val response = repository.toggleBookmark(idUser, idResep)
                if (response.isSuccessful) {
                    updateResepBookmarkStatus(idResep)
                }
            } catch (e: Exception) { }
        }
    }

    private fun updateResepBookmarkStatus(idResep: Int) {
        val currentState = statusUi
        if (currentState is StatusUiHome.Success) {
            fun List<Resep>.mapStatus() = map {
                if (it.id_resep == idResep) it.copy(is_bookmarked = !it.is_bookmarked) else it
            }

            statusUi = currentState.copy(
                allResep = currentState.allResep.mapStatus(),
                makananBerat = currentState.makananBerat.mapStatus(),
                cemilan = currentState.cemilan.mapStatus(),
                minuman = currentState.minuman.mapStatus()
            )
        }
    }
}