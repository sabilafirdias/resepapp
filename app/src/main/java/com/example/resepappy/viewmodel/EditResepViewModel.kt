package com.example.resepappy.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.resepappy.modeldata.Bahan
import com.example.resepappy.modeldata.ResepRequest
import com.example.resepappy.modeldata.toResep
import com.example.resepappy.repositori.ResepRepository
import kotlinx.coroutines.launch

class EditResepViewModel(private val repository: ResepRepository) : ViewModel() {

    var judul by mutableStateOf("")
    var kategori by mutableStateOf("")
    var langkah by mutableStateOf("")
    var catatan by mutableStateOf("")

    val bahanList = mutableStateListOf<Bahan>()

    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)
    var isUpdateSuccess by mutableStateOf(false)

    fun loadResepData(idResep: Int) {
        if (judul.isNotEmpty()) return

        viewModelScope.launch {
            isLoading = true
            try {
                val response = repository.getResepDetail(idResep)
                if (response.isSuccessful) {
                    val resep = response.body()?.toResep()
                    resep?.let {
                        judul = it.judul
                        kategori = it.kategori
                        langkah = it.langkah
                        catatan = it.catatan ?: ""
                        bahanList.clear()
                        bahanList.addAll(it.bahan)
                    }
                }
            } catch (e: Exception) {
                errorMessage = "Gagal memuat data: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    fun updateResep(idResep: Int, idUser: Int) {
        if (judul.isBlank() || kategori.isBlank() || langkah.isBlank() || bahanList.isEmpty()) {
            errorMessage = "Semua kolom wajib diisi!"
            return
        }

        viewModelScope.launch {
            isLoading = true
            try {
                val request = ResepRequest.UpdateResepRequest(
                    id_resep = idResep,
                    id_user = idUser,
                    judul = judul,
                    kategori = kategori,
                    langkah = langkah,
                    catatan = catatan,
                    bahan = bahanList.toList()
                )
                val response = repository.updateResep(idResep, request)
                if (response.isSuccessful) {
                    isUpdateSuccess = true
                } else {
                    errorMessage = "Gagal menyimpan: ${response.message()}"
                }
            } catch (e: Exception) {
                errorMessage = "Masalah jaringan: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }
}