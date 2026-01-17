package com.example.compustoree.service

import com.example.compustoree.model.*
import retrofit2.http.*

interface ApiService {

    // --- AUTH ---
    @POST("users/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @POST("users/register")
    suspend fun register(@Body request: RegisterRequest): RegisterResponse

    @PUT("users/{id}")
    suspend fun updateUser(
        @Path("id") id: Int,
        @Body body: Map<String, String>
    ): LoginResponse

    // --- PRODUK (INI YANG KURANG) ---
    @GET("products")
    suspend fun getProducts(): List<Produk> // ✅ Tambahkan ini!

    @GET("products/{id}")
    suspend fun getProductById(@Path("id") id: Int): Produk

    @POST("products")
    suspend fun addProduct(@Body data: HashMap<String, Any>): Any

    @PUT("products/{id}")
    suspend fun updateProduct(@Path("id") id: Int, @Body data: HashMap<String, Any>): Any

    @DELETE("products/{id}")
    suspend fun deleteProduct(@Path("id") id: Int): Any

    // --- TRANSAKSI ---
    @POST("orders")
    suspend fun createOrder(@Body request: OrderRequest): Any

    @GET("orders")
    suspend fun getRiwayat(@Query("email") email: String?): List<RiwayatOrder>

    @GET("transactions")
    suspend fun getAllTransactions(): List<RiwayatOrder>

    @PUT("orders/{id}/status")
    suspend fun updateStatusOrder(@Path("id") id: Int, @Body body: Map<String, String>): Any

    @DELETE("transactions/{id}")
    suspend fun deleteTransaction(@Path("id") id: Int): Any
}