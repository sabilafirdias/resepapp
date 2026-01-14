package com.example.resepappy.modeldata

import kotlinx.serialization.Serializable

@Serializable
data class Bookmark(
    val id_user: Int,
    val id_resep: Int
)