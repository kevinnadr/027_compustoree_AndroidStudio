package com.example.compustoree.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.compustoree.model.Produk
import com.example.compustoree.service.RetrofitClient
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    // Data List Produk
    var listProduk: List<Produk> by mutableStateOf(emptyList())

    // Status Loading
    var isLoading by mutableStateOf(false)

    // Pesan Error (jika ada masalah jaringan)
    var errorMessage by mutableStateOf("")

    // Fungsi untuk mengambil data dari Server
    fun loadProduk() {
        viewModelScope.launch {
            isLoading = true
            errorMessage = "" // Reset error sebelum mencoba
            try {
                // Panggil API getProducts
                listProduk = RetrofitClient.instance.getAllProducts()
            } catch (e: Exception) {
                // Jika gagal (internet mati / server error)
                errorMessage = "Gagal memuat: ${e.message}"
                e.printStackTrace()
            } finally {
                // Selesai loading
                isLoading = false
            }
        }
    }
}