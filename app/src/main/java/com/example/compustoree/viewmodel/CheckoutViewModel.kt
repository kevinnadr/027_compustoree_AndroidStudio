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

    // --- DATA PRODUK (Diambil dari Server) ---
    var produk: Produk? by mutableStateOf(null)

    // --- INPUTAN USER (STATE) ---
    var jumlahBeli by mutableStateOf(1)

    // Mengambil alamat default dari Profil User saat ini
    var alamat by mutableStateOf(UserSession.currentUser?.alamat ?: "")

    var metodeBayar by mutableStateOf("Transfer Bank") // Default
    var kurir by mutableStateOf("JNE") // Default

    // --- STATUS UI ---
    var isLoading by mutableStateOf(false)
    var message by mutableStateOf("")

    // --- LOGIKA HITUNG BIAYA (REALTIME) ---

    // 1. Hitung Ongkir otomatis berdasarkan kurir yang dipilih
    val ongkir: Double
        get() = when (kurir) {
            "JNE" -> 20000.0
            "SiCepat" -> 18000.0
            "J&T" -> 15000.0
            else -> 0.0
        }

    // 2. Hitung Subtotal (Harga Barang x Jumlah Beli)
    val subtotal: Double
        get() = (produk?.harga ?: 0.0) * jumlahBeli

    // 3. Hitung Total Akhir (Ini yang dipakai untuk QR Code & Data ke Server)
    val totalBayar: Double
        get() = subtotal + ongkir

    // --- FUNGSI-FUNGSI ---

    // Load Data Produk berdasarkan ID
    fun loadProduk(id: Int) {
        viewModelScope.launch {
            isLoading = true
            try {
                produk = RetrofitClient.instance.getProductById(id)

                // Jika field alamat masih kosong, coba isi lagi dari profil user (fallback)
                if (alamat.isEmpty()) {
                    alamat = UserSession.currentUser?.alamat ?: ""
                }
            } catch (e: Exception) {
                e.printStackTrace()
                message = "Gagal memuat produk"
            } finally {
                isLoading = false
            }
        }
    }

    // Fungsi Buat Pesanan (Checkout)
    fun buatPesanan(onSuccess: () -> Unit) {
        val currentProduk = produk
        val userEmail = UserSession.currentUser?.email

        // Validasi
        if (currentProduk == null || userEmail == null) {
            message = "Terjadi kesalahan data (Sesi habis/Produk invalid)"
            return
        }
        if (alamat.trim().isEmpty()) {
            message = "Alamat pengiriman wajib diisi!"
            return
        }

        viewModelScope.launch {
            isLoading = true
            message = "" // Reset pesan error
            try {
                // 1. Siapkan Data Item
                val item = OrderItemRequest(
                    produkId = currentProduk.id,
                    jumlah = jumlahBeli,
                    harga = currentProduk.harga ?: 0.0
                )

                // 2. Siapkan Request Lengkap
                // Penting: Kita kirim 'totalBayar' yang sudah termasuk ongkir
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
                onSuccess() // Pindah ke halaman sukses/riwayat
            } catch (e: Exception) {
                e.printStackTrace()
                message = "Gagal: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }
}