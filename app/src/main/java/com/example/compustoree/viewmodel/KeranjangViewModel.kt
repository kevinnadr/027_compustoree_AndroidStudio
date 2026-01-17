package com.example.compustoree.viewmodel


import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.example.compustoree.model.Produk

// Objek Tunggal (Singleton) agar data keranjang tersimpan meski pindah layar
object CartRepository {
    val items = mutableStateListOf<CartItem>()

    fun addItem(produk: Produk) {
        val existing = items.find { it.produk.id == produk.id }
        if (existing != null) {
            existing.jumlah++
        } else {
            items.add(CartItem(produk, 1))
        }
    }

    fun removeItem(produk: Produk) {
        val existing = items.find { it.produk.id == produk.id }
        if (existing != null) {
            if (existing.jumlah > 1) {
                existing.jumlah--
            } else {
                items.remove(existing)
            }
        }
    }

    fun clear() {
        items.clear()
    }

    fun totalHarga(): Double {
        return items.sumOf { (it.produk.harga ?: 0.0) * it.jumlah }
    }
}

// Model sederhana untuk item keranjang
class CartItem(
    val produk: Produk,
    var jumlah: Int
)

class KeranjangViewModel : ViewModel() {
    val cartItems = CartRepository.items

    fun tambahJumlah(item: CartItem) {
        CartRepository.addItem(item.produk)
    }

    fun kurangJumlah(item: CartItem) {
        CartRepository.removeItem(item.produk)
    }

    fun hitungTotal(): Double {
        return CartRepository.totalHarga()
    }
}