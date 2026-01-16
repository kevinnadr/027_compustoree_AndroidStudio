package com.example.compustoree.model


object UserSession {
    // Variabel ini akan menyimpan data user yang sedang login
    var currentUser: User? = null

    // Cek apakah sedang login?
    fun isLoggedIn(): Boolean {
        return currentUser != null
    }

    // Logout (Hapus data)
    fun logout() {
        currentUser = null
    }
}