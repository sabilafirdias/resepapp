package com.example.resepappy.modeldata

import kotlinx.serialization.Serializable

@Serializable
data class Komentar(
    val id_komentar: Int = 0,
    val id_resep: Int,
    val id_user: Int,
    val username: String = "",
    val isi_komentar: String,
    val created_at: String = ""
)