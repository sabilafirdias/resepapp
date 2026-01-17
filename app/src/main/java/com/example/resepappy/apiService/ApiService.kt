package com.example.resepappy.apiService

import com.example.resepappy.modeldata.Bookmark
import com.example.resepappy.modeldata.EditProfilRequest
import com.example.resepappy.modeldata.Komentar
import com.example.resepappy.modeldata.LoginRequest
import com.example.resepappy.modeldata.LoginResponse
import com.example.resepappy.modeldata.OperationResponse
import com.example.resepappy.modeldata.RegisterRequest
import com.example.resepappy.modeldata.Resep
import com.example.resepappy.modeldata.ResepRequest
import com.example.resepappy.modeldata.ResepResponse
import com.example.resepappy.modeldata.UniqueCheckResponse
import com.example.resepappy.modeldata.User
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


    @GET("user/get_profil.php")
    suspend fun getProfil(@Query("id_user") idUser: Int): Response<User>

    @PUT("user/update_profil.php")
    suspend fun updateProfil(@Body request: EditProfilRequest): Response<OperationResponse>

    @DELETE("user/delete_akun.php")
    suspend fun deleteAkun(@Query("id_user") idUser: Int): Response<OperationResponse>



    @GET("resep/get_all_resep.php") // Sesuaikan dengan nama file PHP Anda
    suspend fun getAllResep(): Response<List<ResepResponse>>

    @GET("resep/get_resep.php")
    suspend fun getResepDetail(@Query("id_resep") id: Int): Response<ResepResponse>

    @GET("resep/get_resep_by_user.php")
    suspend fun getResepByUser(@Query("id_user") idUser: Int): Response<List<Resep>>

    @POST("resep/create_resep.php")
    suspend fun createResep(@Body request: ResepRequest.CreateResepRequest): Response<OperationResponse>

    @PUT("resep/update_resep.php")
    suspend fun updateResep(@Body request: ResepRequest.UpdateResepRequest): Response<OperationResponse>

    @DELETE("resep/delete_resep.php")
    suspend fun deleteResep(
        @Query("id_resep") idResep: Int,
        @Query("id_user") idUser: Int
    ): Response<OperationResponse>

    @GET("resep/search_resep.php")
    suspend fun searchResep(@Query("q") query: String): Response<List<ResepResponse>>



    @POST("bookmark/toggle_bookmark.php")
    suspend fun toggleBookmark(@Body bookmark: Bookmark): Response<OperationResponse>

    @GET("bookmark/get_bookmark.php")
    suspend fun getBookmarks(@Query("id_user") idUser: Int): Response<List<ResepResponse>>

    @GET("bookmark/get_jumlah_bookmark.php")
    suspend fun getCountBookmarks(@Query("id_resep") id: Int): Response<Int>



    @GET("komentar/get_komen.php")
    suspend fun getKomentar(@Query("id_resep") idResep: Int): Response<List<Komentar>>

    @POST("komentar/tambah_komen.php")
    suspend fun addKomentar(@Body komentar: Komentar): Response<OperationResponse>
}
