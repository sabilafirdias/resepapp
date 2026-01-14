package com.example.resepappy.apiService

import com.example.resepappy.modeldata.*
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.DELETE
import retrofit2.http.Query

interface ApiService {

    @POST("auth/register.php")
    suspend fun register(@Body request: RegisterRequest): Response<OperationResponse>
    @POST("auth/login.php")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @GET("auth/check_unique.php")
    suspend fun checkUnique(
        @Query("username") username: String,
        @Query("email") email: String
    ): UniqueCheckResponse

    @GET("auth/check.php")
    suspend fun checkUser(
        @Query("username") username: String,
        @Query("email") email: String
    ): List<User>

    @GET("user/get_profil.php")
    suspend fun getProfil(@Query("id_user") idUser: Int): Response<User>

    @PUT("user/update_profil.php")
    suspend fun updateProfil(@Body request: EditProfilRequest): Response<OperationResponse>

    @DELETE("user/delete_akun.php")
    suspend fun deleteAkun(@Query("id_user") idUser: Int): Response<OperationResponse>

    @GET("resep/get_all_resep.php") // Sesuaikan dengan nama file PHP Anda
    suspend fun getAllResep(): Response<List<ResepResponse>>

    @GET("resep/get_resep.php")
    suspend fun getResep(@Query("id_resep") id: Int): Response<Resep>

    // ApiService.kt
    @GET("resep/get_resep_by_user.php")
    suspend fun getResepByUser(@Query("id_user") idUser: Int): Response<List<Resep>>

    @POST("resep/create_resep.php")
    suspend fun createResep(@Body request: ResepRequest.CreateResepRequest): Response<OperationResponse>

    @PUT("resep/update_resep.php")
    suspend fun updateResep(@Body request: ResepRequest.UpdateResepRequest): Response<OperationResponse>

    @DELETE("resep/delete_resep.php")
    suspend fun deleteResep(@Query("id_resep") id: Int): Response<OperationResponse>

    @GET("resep/search_resep.php")
    suspend fun searchResep(@Query("keyword") keyword: String): Response<List<Resep>>



    @GET("komentar/get_komen.php")
    suspend fun getKomentar(@Query("id_resep") idResep: Int): Response<List<Komentar>>

    @POST("komentar/tambah_komen.php")
    suspend fun addKomentar(@Body komentar: Komentar): Response<OperationResponse>



//    @POST("bookmark/tambah_bookmark.php")
//    suspend fun addBookmark(@Body bookmark: Bookmark): Response<OperationResponse>
//
//    @DELETE("bookmark/hapus_bookmark.php")
//    suspend fun removeBookmark(@Query("id") id: Int): Response<OperationResponse>

    @POST("bookmark/toggle_bookmark.php")
    suspend fun toggleBookmark(@Body bookmark: Bookmark): Response<OperationResponse>

    @GET("bookmark/get_bookmark.php")
    suspend fun getBookmarks(@Query("id_user") idUser: Int): Response<List<ResepResponse>>
}
