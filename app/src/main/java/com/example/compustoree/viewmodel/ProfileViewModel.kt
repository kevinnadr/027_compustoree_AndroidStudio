package com.example.compustoree.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.compustoree.model.User
import com.example.compustoree.model.UserSession

class ProfileViewModel : ViewModel() {
    var user: User? by mutableStateOf(null)

    init {
        // Ambil data dari sesi saat ini
        user = UserSession.currentUser
    }

    fun logout() {
        // Hapus sesi
        UserSession.logout()
    }
}