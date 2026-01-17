package com.example.compustoree.viewmodel



import androidx.compose.runtime.getValue

import androidx.compose.runtime.mutableStateOf

import androidx.compose.runtime.setValue

import androidx.lifecycle.ViewModel

import androidx.lifecycle.viewModelScope

import com.example.compustoree.service.RetrofitClient

import kotlinx.coroutines.launch



class EditProductViewModel : ViewModel() {



    // --- STATE DATA ---

    var nama by mutableStateOf("")

    var kategori by mutableStateOf("")

    var harga by mutableStateOf("")

    var stok by mutableStateOf("")

    var deskripsi by mutableStateOf("")

    var gambarUrl by mutableStateOf("")



    // State UI

    var isLoading by mutableStateOf(false)

    var message by mutableStateOf("")



    // ✅ FUNGSI 1: LOAD DATA LAMA (Agar form tidak kosong saat dibuka)

    fun loadProduct(id: Int) {

        viewModelScope.launch {

            isLoading = true

            try {

                val produk = RetrofitClient.instance.getProductById(id)

                // Isi form dengan data yang sudah ada

                nama = produk.nama ?: ""

                kategori = produk.kategori ?: ""

                harga = produk.harga?.toInt().toString()

                stok = produk.stok?.toString() ?: "0"

                deskripsi = produk.deskripsi ?: ""

                gambarUrl = produk.gambar ?: ""

            } catch (e: Exception) {

                message = "Gagal memuat data: ${e.message}"

            } finally {

                isLoading = false

            }

        }

    }



    // ✅ FUNGSI 2: UPDATE DATA (STRATEGI BOM KUNCI)

    fun updateProduct(id: Int, onSuccess: () -> Unit) {

        viewModelScope.launch {

            isLoading = true

            try {

                // 🔥 KITA KIRIM SEMUA KEMUNGKINAN NAMA KUNCI

                val data = HashMap<String, Any>()



                // 1. Variasi NAMA (Agar tidak jadi "Tanpa Nama")

                data["nama"] = nama

                data["nama_produk"] = nama  // <-- Ini biasanya yang dipakai database

                data["name"] = nama

                data["product_name"] = nama



                // 2. Variasi GAMBAR

                data["gambar"] = gambarUrl

                data["image"] = gambarUrl

                data["photo"] = gambarUrl

                data["url"] = gambarUrl

                data["foto"] = gambarUrl



                // 3. Data Lainnya

                data["kategori"] = kategori

                data["merk"] = "Generic"

                data["harga"] = harga.toDoubleOrNull() ?: 0.0

                data["stok"] = stok.toIntOrNull() ?: 0

                data["deskripsi"] = deskripsi



                // Kirim Update ke Server

                RetrofitClient.instance.updateProduct(id, data)



                message = "Produk Berhasil Diupdate!"

                onSuccess()

            } catch (e: Exception) {

                e.printStackTrace()

                message = "Gagal Update: ${e.message}"

            } finally {

                isLoading = false

            }

        }

    }

}