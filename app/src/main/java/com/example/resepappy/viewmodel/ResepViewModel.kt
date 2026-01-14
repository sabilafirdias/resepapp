package com.example.resepappy.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.resepappy.modeldata.DetailResep
import com.example.resepappy.modeldata.Resep
import com.example.resepappy.modeldata.ResepRequest
import com.example.resepappy.modeldata.toResep // Pastikan ini terimpor
import com.example.resepappy.modeldata.UIStateResep
import com.example.resepappy.repositori.ResepRepository
import kotlinx.coroutines.launch

sealed interface StatusUiResep {
    data class FormInput(val uiState: UIStateResep) : StatusUiResep
    object OperationLoading : StatusUiResep
    object OperationSuccess : StatusUiResep
    object OperationError : StatusUiResep
    data class ResepLoaded(val resep: List<Resep>) : StatusUiResep
    data class DetailResepLoaded(val resep: Resep) : StatusUiResep
    object DataError : StatusUiResep
    object DataLoading : StatusUiResep
}

class ResepViewModel(
    private val repository: ResepRepository
) : ViewModel() {

    var statusUi: StatusUiResep by mutableStateOf(StatusUiResep.FormInput(UIStateResep()))
        private set

    var uiStateResep by mutableStateOf(UIStateResep())
        private set

    init {
        updateFormState(DetailResep())
    }

    fun updateFormState(detailResep: DetailResep) {
        val isValid = detailResep.judul.isNotBlank() &&
                detailResep.langkah.isNotBlank() &&
                detailResep.kategori.isNotBlank()

        uiStateResep = UIStateResep(detailResep, isValid)
        statusUi = StatusUiResep.FormInput(uiStateResep)
    }

    fun createResep(idUser: Int) {
        val currentForm = uiStateResep.detailResep
        viewModelScope.launch {
            statusUi = StatusUiResep.OperationLoading
            try {
                val request = ResepRequest.CreateResepRequest(
                    id_user = idUser,
                    judul = currentForm.judul,
                    langkah = currentForm.langkah,
                    catatan = currentForm.catatan.ifBlank { null },
                    kategori = currentForm.kategori,
                    bahan = emptyList()
                )
                val response = repository.tambahResep(request)
                statusUi = if (response.isSuccessful) StatusUiResep.OperationSuccess
                else StatusUiResep.OperationError
            } catch (e: Exception) {
                statusUi = StatusUiResep.OperationError
            }
        }
    }

    fun updateResep(idResep: Int, idUser: Int) {
        val currentForm = uiStateResep.detailResep
        viewModelScope.launch {
            statusUi = StatusUiResep.OperationLoading
            try {
                val request = ResepRequest.UpdateResepRequest(
                    id_resep = idResep,
                    id_user = idUser,
                    judul = currentForm.judul,
                    langkah = currentForm.langkah,
                    catatan = currentForm.catatan.ifBlank { null },
                    kategori = currentForm.kategori,
                    bahan = emptyList()
                )
                val response = repository.updateResep(idResep, request)
                statusUi = if (response.isSuccessful) StatusUiResep.OperationSuccess
                else StatusUiResep.OperationError
            } catch (e: Exception) {
                statusUi = StatusUiResep.OperationError
            }
        }
    }

    fun loadResepTerbaru() {
        searchResep("")
    }

    fun searchResep(keyword: String) {
        viewModelScope.launch {
            statusUi = StatusUiResep.DataLoading
            try {
                val response = repository.searchResep(keyword)
                if (response.isSuccessful) {
                    // Perbaikan: Mapping data dan masukkan ke ResepLoaded
                    val listResep = response.body()?.map { it.toResep() } ?: emptyList()
                    statusUi = StatusUiResep.ResepLoaded(listResep)
                } else {
                    statusUi = StatusUiResep.DataError
                }
            } catch (e: Exception) {
                statusUi = StatusUiResep.DataError
            }
        }
    }

    fun getResepById(id: Int) {
        viewModelScope.launch {
            statusUi = StatusUiResep.DataLoading
            try {
                // Perbaikan: Tambahkan pemanggilan repository
                val response = repository.getResepDetail(id)
                if (response.isSuccessful) {
                    val resep = response.body()?.toResep()
                    statusUi = if (resep != null) StatusUiResep.DetailResepLoaded(resep)
                    else StatusUiResep.DataError
                } else {
                    statusUi = StatusUiResep.DataError
                }
            } catch (e: Exception) {
                statusUi = StatusUiResep.DataError
            }
        }
    }
}