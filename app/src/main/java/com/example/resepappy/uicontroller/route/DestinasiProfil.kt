package com.example.resepappy.uicontroller.route

import com.example.resepappy.R
import kotlinx.serialization.Serializable

@Serializable
data class DestinasiProfil(val idUser: Int) {
    companion object {
        const val route = "profil/{idUser}"
        val titleRes = R.string.profile_title
    }
}

@Serializable
object DestinasiEditProfil : DestinasiNavigasi {
    override val route = "edit_profil/{idUser}"
    override val titleRes = R.string.edit_profil
}