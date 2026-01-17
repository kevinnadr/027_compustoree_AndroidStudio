package com.example.compustoree.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.compustoree.model.UserSession
import com.example.compustoree.service.RetrofitClient
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {

    // Status Mode Edit
    var isEditing by mutableStateOf(false)
    var isLoading by mutableStateOf(false)

    // Data Form (Default ambil dari Sesi Login)
    var nama by mutableStateOf(UserSession.currentUser?.nama ?: "")
    var phone by mutableStateOf(UserSession.currentUser?.noHp ?: "")
    var alamat by mutableStateOf(UserSession.currentUser?.alamat ?: "")

    // Pesan Feedback
    var message by mutableStateOf("")

    // Fungsi Simpan Perubahan ke Server
    fun saveProfile() {
        val user = UserSession.currentUser ?: return

        viewModelScope.launch {
            isLoading = true
            try {
                // Siapkan data body untuk dikirim
                val body = mapOf(
                    "nama" to nama,
                    "no_hp" to phone,
                    "alamat" to alamat
                )

                // Panggil API Update User
                val response = RetrofitClient.instance.updateUser(user.id, body)

                // Jika sukses, update data sesi lokal
                if (response.user != null) {
                    UserSession.currentUser = response.user

                    // Update state UI dengan data baru
                    nama = response.user.nama ?: ""
                    phone = response.user.noHp ?: ""
                    alamat = response.user.alamat ?: ""

                    message = "Profil berhasil diperbarui!"
                    isEditing = false // Keluar dari mode edit
                } else {
                    message = "Gagal: Respon server kosong"
                }
            } catch (e: Exception) {
                message = "Gagal update: ${e.message}"
                e.printStackTrace()
            } finally {
                isLoading = false
            }
        }
    }

    // Fungsi Batal Edit (Reset ke data awal)
    fun cancelEdit() {
        val user = UserSession.currentUser
        nama = user?.nama ?: ""
        phone = user?.noHp ?: ""
        alamat = user?.alamat ?: ""
        isEditing = false
        message = ""
    }
}