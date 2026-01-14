package com.example.resepappy.uicontroller.route

import kotlinx.serialization.Serializable

@Serializable
data class DestinasiBuatResep(val idUser: Int) {
    companion object {
        const val route = "buat_resep/{idUser}"
    }
}

@Serializable
data class DestinasiDetailResep(val idResep: Int) {
    companion object {
        const val route = "detail_resep/{idResep}"
    }
}

@Serializable
data class DestinasiEditResep(val idResep: Int) {
    companion object {
        const val route = "edit_resep/{idResep}"
    }
}