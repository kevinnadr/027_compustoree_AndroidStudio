package com.example.compustoree.util


import java.text.NumberFormat
import java.util.Locale

// Hapus kata kunci 'private' agar bisa diakses semua file
fun formatRupiah(number: Double): String {
    val format = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
    return format.format(number).replace("Rp", "Rp ").substringBefore(",")
}