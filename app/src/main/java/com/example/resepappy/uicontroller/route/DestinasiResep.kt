package com.example.resepappy.uicontroller.route

import com.example.resepappy.R
import kotlinx.serialization.Serializable

@Serializable
data class DestinasiBuatResep(val idUser: Int) {
    companion object {
        const val route = "buat_resep/{idUser}"
    }
}

object DestinasiDetailResep : DestinasiNavigasi {
    override val route = "detail_resep"
    override val titleRes = R.string.detail_resep
    const val resepIdArg = "idResep"
    val routeWithArgs = "$route/{$resepIdArg}"
}

@Serializable
data class DestinasiEditResep(val idResep: Int) {
    companion object {
        const val route = "edit_resep/{idResep}"
    }
}