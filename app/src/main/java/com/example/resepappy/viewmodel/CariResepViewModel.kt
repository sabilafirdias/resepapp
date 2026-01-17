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
                    val dataDariApi = response.body() ?: emptyList()

                    listHasilCari.clear()
                    dataDariApi.forEach { resepResponse ->
                        val resep = resepResponse.toResep()
                        val matchingBahan = resep.bahan.any { it.nama_bahan.contains(newQuery, ignoreCase = true) }
                        val matchingJudul = resep.judul.contains(newQuery, ignoreCase = true)

                        if (matchingJudul || matchingBahan) {
                            listHasilCari.add(resep)
                        }
                    }
                }
            } catch (e: Exception) { }
            finally {
                isSearching = false
            }
        }
    }
}