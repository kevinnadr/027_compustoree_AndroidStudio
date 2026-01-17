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

    // Penampung data produk
    var products: List<Produk> by mutableStateOf(emptyList())
        private set // Setter private agar hanya bisa diubah di ViewModel

    // Status UI
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf("")

    // Fungsi Load Data
    fun loadProducts() {
        viewModelScope.launch {
            isLoading = true
            errorMessage = "" // Reset error
            try {
                products = RetrofitClient.instance.getProducts()
            } catch (e: Exception) {
                errorMessage = "Gagal memuat: ${e.message}"
                e.printStackTrace()
            } finally {
                isLoading = false
            }
        }
    }

    // Fungsi Hapus Produk
    fun deleteProduct(id: Int) {
        viewModelScope.launch {
            try {
                RetrofitClient.instance.deleteProduct(id)
                // Refresh data setelah hapus
                loadProducts()
            } catch (e: Exception) {
                e.printStackTrace()
                errorMessage = "Gagal menghapus: ${e.message}"
            }
        }
    }
}