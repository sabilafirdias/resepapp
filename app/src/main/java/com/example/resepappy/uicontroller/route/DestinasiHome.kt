package com.example.resepappy.uicontroller.route

import com.example.resepappy.R
import kotlinx.serialization.Serializable

@Serializable
object DestinasiHome {
    const val route = "home"
    val titleRes = R.string.home_title
}

@Serializable
object DestinasiCari : DestinasiNavigasi {
    override val route = "cari_resep"
    override val titleRes = R.string.cari_resep
}