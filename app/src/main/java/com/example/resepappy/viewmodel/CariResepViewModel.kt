package com.example.resepappy.viewmodel

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.resepappy.modeldata.Resep
import com.example.resepappy.modeldata.toResep
import com.example.resepappy.repositori.ResepRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class CariResepViewModel(private val repository: ResepRepository) : ViewModel() {

    var searchQuery by mutableStateOf("")
    var listHasilCari = mutableStateListOf<Resep>()
    var isSearching by mutableStateOf(false)
    var searchingStarted by mutableStateOf(false)

    private var searchJob: Job? = null

    fun onQueryChange(newQuery: String) {
        searchQuery = newQuery
        searchingStarted = true

        // Debounce: Menunggu user selesai mengetik (500ms) sebelum menembak API
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            if (newQuery.isBlank()) {
                listHasilCari.clear()
                isSearching = false
                return@launch
            }

            delay(500)
            isSearching = true
            try {
                val response = repository.searchResep(newQuery)
                if (response.isSuccessful) {
                    listHasilCari.clear()
                    response.body()?.forEach {
                        listHasilCari.add(it.toResep())
                    }
                }
            } catch (e: Exception) {
                // Handle Error
            } finally {
                isSearching = false
            }
        }
    }
}