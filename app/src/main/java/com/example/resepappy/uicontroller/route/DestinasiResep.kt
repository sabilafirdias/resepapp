package com.example.resepappy.uicontroller.route

import com.example.resepappy.R
import kotlinx.serialization.Serializable

@Serializable
data class DestinasiBuatResep(val idUser: Int) {
    companion object {
        const val route = "buat_resep/{idUser}"
        val titleRes = R.string.create_resep
    }
}

@Serializable
data class DestinasiDetailResep(val idResep: Int) {
    companion object {
        const val route = "detail_resep/{idResep}"
        val titleRes = R.string.detail_resep
    }
}

@Serializable
data class DestinasiEditResep(val idResep: Int) {
    companion object {
        const val route = "edit_resep/{idResep}"
        val titleRes = R.string.edit_resep
    }
}