package com.example.resepappy.viewmodel
//
//import android.app.Application
//import androidx.lifecycle.AndroidViewModel
//import androidx.lifecycle.viewModelScope
//import com.example.resepappy.modeldata.Komentar
//import com.example.resepappy.repositori.AplikasiResep
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.StateFlow
//import kotlinx.coroutines.launch
//import kotlinx.serialization.json.buildJsonObject
//import kotlinx.serialization.json.put
//
//class KomentarViewModel(application: Application) : AndroidViewModel(application) {
//
//    private val repository = (application as AplikasiResep).apiService
//
//    private val _komentarList = MutableStateFlow<List<Komentar>>(emptyList())
//    val komentarList: StateFlow<List<Komentar>> = _komentarList
//
//    private val _addState = MutableStateFlow<ResultState<Unit>>(ResultState.Loading)
//    val addState: StateFlow<ResultState<Unit>> = _addState
//
//    fun loadKomentar(idResep: Int) {
//        viewModelScope.launch {
//            try {
//                val body = buildJsonObject { put("id_resep", idResep) }
//                val response = repository.getKomentar(body)
//                if (response.isSuccessful) {
//                    _komentarList.value = response.body() ?: emptyList()
//                }
//            } catch (e: Exception) {
//                // Handle
//            }
//        }
//    }
//
//    fun addKomentar(idUser: Int, idResep: Int, isi: String) {
//        viewModelScope.launch {
//            _addState.value = ResultState.Loading
//            try {
//                val body = buildJsonObject {
//                    put("id_user", idUser)
//                    put("id_resep", idResep)
//                    put("isi_komentar", isi)
//                }
//                val response = repository.addKomentar(body) // Anda perlu tambahkan di ApiService
//                if (response.isSuccessful && response.body()?.containsKey("message") == true) {
//                    _addState.value = ResultState.Success(Unit)
//                    loadKomentar(idResep)
//                } else {
//                    _addState.value = ResultState.Error("Gagal menambah komentar")
//                }
//            } catch (e: Exception) {
//                _addState.value = ResultState.Error(e.message ?: "Error")
//            }
//        }
//    }
//}