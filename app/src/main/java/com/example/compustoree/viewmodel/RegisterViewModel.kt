package com.example.compustoree.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.compustoree.model.RegisterRequest
import com.example.compustoree.service.RetrofitClient
import kotlinx.coroutines.launch

class RegisterViewModel : ViewModel() {

    // --- STATE INPUT ---
    var nama by mutableStateOf("")
    var email by mutableStateOf("")
    var password by mutableStateOf("")
    var noHp by mutableStateOf("")

    // --- STATE UI ---
    var isLoading by mutableStateOf(false)
    var message by mutableStateOf("")

    // Fungsi Register
    fun register(onSuccess: () -> Unit) {
        if (nama.isEmpty() || email.isEmpty() || password.isEmpty() || noHp.isEmpty()) {
            message = "Semua kolom wajib diisi!"
            return
        }

        viewModelScope.launch {
            isLoading = true
            try {
                // Membuat Request Object
                val request = RegisterRequest(
                    nama = nama,
                    email = email,
                    password = password,
                    noHp = noHp
                )

                // Panggil API
                val response = RetrofitClient.instance.register(request)

                // Jika sukses (tidak throw error)
                message = response.message
                onSuccess()

            } catch (e: Exception) {
                message = "Gagal mendaftar: ${e.message}"
                e.printStackTrace()
            } finally {
                isLoading = false
            }
        }
    }
}