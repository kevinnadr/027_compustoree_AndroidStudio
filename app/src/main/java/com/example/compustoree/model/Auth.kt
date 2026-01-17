package com.example.compustoree.model


import com.google.gson.annotations.SerializedName

// --- LOGIN ---
data class LoginRequest(
    val email: String,
    val password: String
)

// --- REGISTER ---
data class RegisterRequest(
    val nama: String,
    val email: String,
    val password: String,
    // Gunakan SerializedName karena di database/server pakai 'no_hp' (snake_case)
    // sedangkan di Kotlin kita pakai 'noHp' (camelCase)
    @SerializedName("no_hp") val noHp: String
)

data class RegisterResponse(
    val message: String
)