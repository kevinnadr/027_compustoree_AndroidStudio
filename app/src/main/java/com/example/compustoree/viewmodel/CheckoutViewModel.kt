package com.example.compustoree.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.compustoree.model.OrderItemRequest
import com.example.compustoree.model.OrderRequest
import com.example.compustoree.model.Produk
import com.example.compustoree.model.UserSession
import com.example.compustoree.service.RetrofitClient
import kotlinx.coroutines.launch

class CheckoutViewModel : ViewModel() {

    // --- DATA PRODUK ---
    var produk: Produk? by mutableStateOf(null)

    // --- INPUT USER ---
    var jumlahBeli by mutableStateOf(1)
    var alamat by mutableStateOf(UserSession.currentUser?.alamat ?: "")
    var metodeBayar by mutableStateOf("Transfer Bank")
    var kurir by mutableStateOf("JNE")

    // --- STATUS UI ---
    var isLoading by mutableStateOf(false)
    var message by mutableStateOf("")

    // --- LOGIKA BIAYA ---
    val ongkir: Double
        get() = when (kurir) {
            "JNE" -> 20000.0
            "SiCepat" -> 18000.0
            "J&T" -> 15000.0
            else -> 0.0
        }

    val subtotal: Double
        get() = (produk?.harga ?: 0.0) * jumlahBeli

    val totalBayar: Double
        get() = subtotal + ongkir

    // --- FUNGSI ---
    fun loadProduk(id: Int) {
        viewModelScope.launch {
            isLoading = true
            try {
                produk = RetrofitClient.instance.getProductById(id)
                if (alamat.isEmpty()) {
                    alamat = UserSession.currentUser?.alamat ?: ""
                }
            } catch (e: Exception) {
                message = "Gagal memuat produk"
            } finally {
                isLoading = false
            }
        }
    }

    fun buatPesanan(onSuccess: () -> Unit) {
        val currentProduk = produk ?: return
        val userEmail = UserSession.currentUser?.email ?: return

        if (alamat.isBlank()) {
            message = "Alamat pengiriman wajib diisi!"
            return
        }

        viewModelScope.launch {
            isLoading = true
            try {
                // 1. Buat Item Pesanan
                val item = OrderItemRequest(
                    produkId = currentProduk.id,
                    jumlah = jumlahBeli,
                    harga = currentProduk.harga ?: 0.0
                )

                // 2. Buat Request Pesanan
                val request = OrderRequest(
                    userEmail = userEmail,
                    totalHarga = totalBayar,
                    metodePembayaran = metodeBayar,
                    metodePengiriman = kurir,
                    alamatPengiriman = alamat,
                    items = listOf(item)
                )

                // 3. Kirim ke Server
                RetrofitClient.instance.createOrder(request)
                message = "Pesanan Berhasil Dibuat!"
                onSuccess()
            } catch (e: Exception) {
                message = "Gagal: ${e.message}"
                e.printStackTrace()
            } finally {
                isLoading = false
            }
        }
    }
}