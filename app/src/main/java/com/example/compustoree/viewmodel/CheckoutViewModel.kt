package com.example.compustoree.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.compustoree.model.OrderRequest
import com.example.compustoree.model.Produk
import com.example.compustoree.model.UserSession
import com.example.compustoree.service.RetrofitClient
import kotlinx.coroutines.launch

class CheckoutViewModel : ViewModel() {

    var produk: Produk? by mutableStateOf(null)
    var jumlahBeli by mutableStateOf(1)

    // Data User (Otomatis ambil dari sesi)
    var namaUser by mutableStateOf(UserSession.currentUser?.nama ?: "")
    var emailUser by mutableStateOf(UserSession.currentUser?.email ?: "")
    var noHpUser by mutableStateOf(UserSession.currentUser?.noHp ?: "")

    // Opsi Pilihan (State Baru)
    var alamat by mutableStateOf(UserSession.currentUser?.alamat ?: "")
    var metodePengiriman by mutableStateOf("Diantar") // Default: Diantar
    var metodePembayaran by mutableStateOf("Transfer Bank") // Default: Transfer

    var isLoading by mutableStateOf(false)
    var statusTransaksi by mutableStateOf("")

    // Hitung Total
    val totalBayar: Double
        get() = (produk?.harga ?: 0.0) * jumlahBeli

    fun loadProduk(id: Int) {
        viewModelScope.launch {
            try {
                produk = RetrofitClient.instance.getProductById(id)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun tambahJumlah() {
        val stok = produk?.stok ?: 0
        if (jumlahBeli < stok) jumlahBeli++
    }

    fun kurangJumlah() {
        if (jumlahBeli > 1) jumlahBeli--
    }

    fun prosesCheckout(onSuccess: () -> Unit) {
        viewModelScope.launch {
            isLoading = true
            try {
                // Logika: Jika "Ambil di Toko", alamatnya kita set hardcode
                val alamatFinal = if (metodePengiriman == "Ambil Ditempat") {
                    "PICKUP - AMBIL SENDIRI DI TOKO"
                } else {
                    alamat // Pakai alamat yang diketik user
                }

                // Gabungkan Metode Bayar & Kirim biar tersimpan di server
                val metodeFinal = "$metodePembayaran ($metodePengiriman)"

                val order = OrderRequest(
                    userEmail = emailUser,
                    productId = produk?.id ?: 0,
                    jumlah = jumlahBeli,
                    totalHarga = totalBayar,
                    metodePembayaran = metodeFinal, // Kirim gabungan
                    alamatPengiriman = alamatFinal
                )

                // Kirim ke Server
                val response = RetrofitClient.instance.createOrder(order)
                statusTransaksi = "Sukses! ID: ${response.id}"
                onSuccess()

            } catch (e: Exception) {
                statusTransaksi = "Gagal: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }
}