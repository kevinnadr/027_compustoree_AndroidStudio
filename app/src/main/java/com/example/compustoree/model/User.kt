package com.example.compustoree.model


import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("id_user") val id: Int = 0,
    val email: String,
    val nama: String? = "",
    val role: String = "user",
    @SerializedName("no_hp") val noHp: String? = "",
    val alamat: String? = "",
    @SerializedName("foto_url") val fotoUrl: String? = "",


)