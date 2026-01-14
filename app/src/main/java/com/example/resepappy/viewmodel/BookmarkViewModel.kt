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

sealed interface StatusUiBookmark {
    object Loading : StatusUiBookmark
    data class Success(val resepList: List<Resep>) : StatusUiBookmark
    object Error : StatusUiBookmark
}

class BookmarkViewModel(private val repository: ResepRepository) : ViewModel() {

    var statusUi: StatusUiBookmark by mutableStateOf(StatusUiBookmark.Loading)
        private set

    fun loadBookmarks(idUser: Int) {
        viewModelScope.launch {
            statusUi = StatusUiBookmark.Loading
            try {
                val response = repository.getBookmarks(idUser)
                if (response.isSuccessful) {
                    val data = response.body()?.map { it.toResep() } ?: emptyList()
                    statusUi = StatusUiBookmark.Success(data)
                } else {
                    statusUi = StatusUiBookmark.Error
                }
            } catch (e: Exception) {
                statusUi = StatusUiBookmark.Error
            }
        }
    }

    fun toggleBookmark(idUser: Int, idResep: Int) {
        viewModelScope.launch {
            try {
                val response = repository.toggleBookmark(idUser, idResep)
                if (response.isSuccessful) {
                    loadBookmarks(idUser)
                }
            } catch (e: Exception) { }
        }
    }
}