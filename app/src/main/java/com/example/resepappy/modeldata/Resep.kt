package com.example.resepappy.modeldata

import kotlinx.serialization.Serializable

@Serializable
data class Resep(
    val id_resep: Int = 0,
    val id_user: Int = 0,
    val judul: String = "",
    val langkah: String = "",
    val catatan: String? = null,
    val kategori: String = "",
    val created_at: String = "",
    val updated_at: String = "",
    val username: String = "",
    val bahan: List<Bahan> = emptyList(),
    var is_bookmarked: Boolean = false,
    val jumlah_komentar: Int = 0
)

@Serializable
sealed interface ResepRequest {
    @Serializable
    data class CreateResepRequest(
        val id_user: Int = 0,
        val judul: String,
        val langkah: String,
        val catatan: String?,
        val kategori: String,
        val bahan: List<Bahan>
    ) : ResepRequest

    @Serializable
    data class UpdateResepRequest(
        val id_resep: Int,
        val id_user: Int,
        val judul: String,
        val langkah: String,
        val catatan: String?,
        val kategori: String,
        val bahan: List<Bahan>
    ) : ResepRequest
}

@Serializable
data class ResepResponse(
    val id_resep: Int,
    val id_user: Int,
    val judul: String,
    val langkah: String,
    val catatan: String? = null,
    val kategori: String,
    val created_at: String,
    val updated_at: String? = null,
    val username: String = "", // Tambahkan ini
    val bahan: List<Bahan> = emptyList() // Tambahkan ini
)

fun ResepResponse.toResep(): Resep = Resep(
    id_resep = id_resep,
    id_user = id_user,
    judul = judul,
    langkah = langkah,
    catatan = catatan,
    kategori = kategori,
    created_at = created_at,
    updated_at = updated_at ?: "",
    username = username,
    bahan = bahan
)

data class DetailResep(
    val judul: String = "",
    val langkah: String = "",
    val catatan: String = "",
    val kategori: String = "Makanan Berat",
    val bahan: List<DetailBahan> = emptyList()
)
data class UIStateResep(
    val detailResep: DetailResep = DetailResep(),
    val isEntryValid: Boolean = false
)