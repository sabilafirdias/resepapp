package com.example.resepappy.modeldata

import kotlinx.serialization.Serializable

@Serializable
data class Bahan(
    val nama_bahan: String = "",
    val takaran: String = ""
)

fun DetailBahan.toBahan(): Bahan = Bahan(nama_bahan, takaran)

fun Bahan.toDetailBahan(): DetailBahan = DetailBahan(nama_bahan, takaran)

data class DetailBahan(
    val nama_bahan: String = "",
    val takaran: String = ""
)