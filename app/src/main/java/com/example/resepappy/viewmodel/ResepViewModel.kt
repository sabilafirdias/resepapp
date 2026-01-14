package com.example.resepappy.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.resepappy.modeldata.Bahan
import com.example.resepappy.modeldata.DetailResep
import com.example.resepappy.modeldata.Resep
import com.example.resepappy.modeldata.ResepRequest
import com.example.resepappy.modeldata.UIStateResep
import com.example.resepappy.repositori.ResepRepository
import kotlinx.coroutines.launch

sealed interface StatusUiResep {
    // State untuk form input
    data class FormInput(val uiState: UIStateResep) : StatusUiResep

    // State untuk operasi CRUD
    object OperationLoading : StatusUiResep
    object OperationSuccess : StatusUiResep
    object OperationError : StatusUiResep

    // State untuk data loading
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

    private fun validasiInput(): Boolean {
        val detail = uiStateResep.detailResep
        return detail.judul.isNotBlank() && detail.bahan.isNotEmpty()
    }

    fun updateFormState(detailResep: DetailResep) {
        val isValid = detailResep.judul.isNotBlank() &&
                detailResep.langkah.isNotBlank() &&
                detailResep.kategori.isNotBlank()

        statusUi = StatusUiResep.FormInput(
            UIStateResep(detailResep, isValid)
        )
    }

    fun createResep(idUser: Int) {
        val currentForm = when (val state = statusUi) {
            is StatusUiResep.FormInput -> state.uiState.detailResep
            else -> return // Jika tidak dalam state form, batalkan
        }

        viewModelScope.launch {
            statusUi = StatusUiResep.OperationLoading
            statusUi = try {
                val request = ResepRequest.CreateResepRequest(
                    id_user = idUser,
                    judul = currentForm.judul,
                    langkah = currentForm.langkah,
                    catatan = currentForm.catatan.ifBlank { null },
                    kategori = currentForm.kategori,
                    bahan = emptyList() // Sesuaikan jika ada manajemen bahan
                )
                val response = repository.tambahResep(request)
                if (response.isSuccessful && response.body()?.message != null) {
                    StatusUiResep.OperationSuccess
                } else {
                    StatusUiResep.OperationError
                }
            } catch (e: Exception) {
                StatusUiResep.OperationError
            }
        }
    }

    fun updateResep(idResep: Int, idUser: Int) {
        val currentForm = when (val state = statusUi) {
            is StatusUiResep.FormInput -> state.uiState.detailResep
            else -> return
        }

        viewModelScope.launch {
            statusUi = StatusUiResep.OperationLoading
            statusUi = try {
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
                if (response.isSuccessful && response.body()?.message != null) {
                    StatusUiResep.OperationSuccess
                } else {
                    StatusUiResep.OperationError
                }
            } catch (e: Exception) {
                StatusUiResep.OperationError
            }
        }
    }

    fun loadResepTerbaru() {
        searchResep("")
    }

    fun searchResep(keyword: String) {
        viewModelScope.launch {
            statusUi = StatusUiResep.DataLoading
            statusUi = try {
                val response = repository.searchResep(keyword)
                if (response.isSuccessful) {
                    StatusUiResep.ResepLoaded(response.body() ?: emptyList())
                } else {
                    StatusUiResep.DataError
                }
            } catch (e: Exception) {
                StatusUiResep.DataError
            }
        }
    }

    fun getResepById(id: Int) {
        viewModelScope.launch {
            statusUi = StatusUiResep.DataLoading
            statusUi = try {
                val response = repository.getAllResep()
                if (response.isSuccessful) {
                    val foundResep = response.body()?.find { it.id_resep == id }
                    if (foundResep != null) {
                        StatusUiResep.DataError
                    } else {
                        StatusUiResep.DataError
                    }
                } else {
                    StatusUiResep.DataError
                }
            } catch (e: Exception) {
                StatusUiResep.DataError
            }
        }
    }
}