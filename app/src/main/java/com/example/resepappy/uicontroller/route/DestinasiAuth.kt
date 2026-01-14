package com.example.resepappy.uicontroller.route

import com.example.resepappy.R
import kotlinx.serialization.Serializable

@Serializable
object DestinasiWelcome {
    const val route = "welcome"
}

@Serializable
object DestinasiLogin {
    const val route = "login"
    val titleRes = R.string.login_title
}

@Serializable
object DestinasiRegister {
    const val route = "item_entry"
    val titleRes = R.string.register_title
}