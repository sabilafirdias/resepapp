package com.example.resepappy.uicontroller.route

import com.example.resepappy.R

object DestinasiKomentar : DestinasiNavigasi {
    override val route = "komentar"
    override val titleRes = R.string.app_name
    const val resepIdArg = "idResep"
    val routeWithArgs = "$route/{$resepIdArg}"
}