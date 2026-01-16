package com.example.resepappy.repositori

import com.example.resepappy.apiService.ApiService
import com.example.resepappy.modeldata.*
import retrofit2.Response

interface ResepRepository {

    suspend fun register(request: RegisterRequest): Response<OperationResponse>
    suspend fun login(request: LoginRequest): Response<LoginResponse>

    suspend fun checkUnique(username: String, email: String): UniqueCheckResponse

    var currentUserId: Int?

    suspend fun getProfil(idUser: Int): Response<User>
    suspend fun updateProfil(request: EditProfilRequest): Response<OperationResponse>
    suspend fun deleteAkun(idUser: Int): Response<OperationResponse>

    suspend fun getAllResep(): Response<List<ResepResponse>>
    suspend fun getResepDetail(id: Int): Response<ResepResponse>
    suspend fun getResepByUserId(idUser: Int): Response<List<Resep>>
    suspend fun searchResep(keyword: String): Response<List<ResepResponse>>
    suspend fun tambahResep(request: ResepRequest.CreateResepRequest): Response<OperationResponse>
    suspend fun updateResep(id: Int, request: ResepRequest.UpdateResepRequest): Response<OperationResponse>
    suspend fun hapusResep(id: Int, idUser: Int): Response<OperationResponse>
//    suspend fun addBookmark(bookmark: Bookmark): Response<OperationResponse>
//    suspend fun removeBookmark(id: Int): Response<OperationResponse>

    suspend fun toggleBookmark(idUser: Int, idResep: Int): Response<OperationResponse>
    suspend fun getBookmarks(idUser: Int): Response<List<ResepResponse>>
    suspend fun getCountBookmarks(id: Int): Response<Int>

    suspend fun addKomentar(komentar: Komentar): Response<OperationResponse>
    suspend fun getKomentar(idResep: Int): Response<List<Komentar>>
}

class JaringanResepRepository(
    private val apiService: ApiService) : ResepRepository
{
    override suspend fun register(request: RegisterRequest): Response<OperationResponse> =
        apiService.register(request)

    override suspend fun login(request: LoginRequest): Response<LoginResponse> =
        apiService.login(request)

    override suspend fun checkUnique(username: String, email: String): UniqueCheckResponse {
        return apiService.checkUnique(username, email)
    }

    override var currentUserId: Int? = null

    override suspend fun getProfil(idUser: Int): Response<User> =
        apiService.getProfil(idUser)

    override suspend fun updateProfil(request: EditProfilRequest): Response<OperationResponse> =
        apiService.updateProfil(request)

    override suspend fun deleteAkun(idUser: Int): Response<OperationResponse> =
        apiService.deleteAkun(idUser)

    override suspend fun getAllResep(): Response<List<ResepResponse>> =
        apiService.getAllResep()

    override suspend fun getResepByUserId(idUser: Int): Response<List<Resep>> =
        apiService.getResepByUser(idUser)

    override suspend fun getResepDetail(id: Int): Response<ResepResponse> =
        apiService.getResepDetail(id)

    override suspend fun searchResep(query: String): Response<List<ResepResponse>> =
        apiService.searchResep(query)

    override suspend fun tambahResep(request: ResepRequest.CreateResepRequest): Response<OperationResponse> =
        apiService.createResep(request)

    override suspend fun updateResep(id: Int, request: ResepRequest.UpdateResepRequest): Response<OperationResponse> =
        apiService.updateResep(request)

    override suspend fun hapusResep(id: Int, idUser: Int): Response<OperationResponse> =
        apiService.deleteResep(id, idUser)

    override suspend fun toggleBookmark(idUser: Int, idResep: Int): Response<OperationResponse> {
        return apiService.toggleBookmark(Bookmark(id_user = idUser, id_resep = idResep)) }

    override suspend fun getBookmarks(idUser: Int): Response<List<ResepResponse>> {
        return apiService.getBookmarks(idUser) }

    override suspend fun getCountBookmarks(id: Int): Response<Int> =
        apiService.getCountBookmarks(id)

    override suspend fun addKomentar(komentar: Komentar): Response<OperationResponse> =
        apiService.addKomentar(komentar)

    override suspend fun getKomentar(idResep: Int): Response<List<Komentar>> =
        apiService.getKomentar(idResep)
}