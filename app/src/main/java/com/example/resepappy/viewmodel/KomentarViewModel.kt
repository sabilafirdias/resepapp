package com.example.resepappy.viewmodel

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.resepappy.modeldata.Komentar
import com.example.resepappy.repositori.ResepRepository
import kotlinx.coroutines.launch

class KomentarViewModel(private val repository: ResepRepository) : ViewModel() {
    var listKomentar = mutableStateListOf<Komentar>()
    var isLoading by mutableStateOf(false)

    fun getKomentar(idResep: Int) {
        viewModelScope.launch {
            isLoading = true
            try {
                val response = repository.getKomentar(idResep)
                if (response.isSuccessful) {
                    listKomentar.clear()
                    listKomentar.addAll(response.body() ?: emptyList())
                }
            } catch (e: Exception) { e.printStackTrace() }
            finally { isLoading = false }
        }
    }

    fun kirimKomentar(idResep: Int, idUser: Int, isi: String, onComplete: () -> Unit) {
        if (isi.isBlank()) return
        viewModelScope.launch {
            try {
                val newKomen = Komentar(
                    id_resep = idResep,
                    id_user = idUser,
                    isi_komentar = isi
                )

                val response = repository.addKomentar(newKomen)
                if (response.isSuccessful) {
                    getKomentar(idResep)
                    onComplete()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}