package com.example.compustoree.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.compustoree.viewmodel.CartRepository
import com.example.compustoree.viewmodel.KeranjangViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KeranjangScreen(
    onBackClick: () -> Unit,
    onCheckoutClick: () -> Unit,
    viewModel: KeranjangViewModel = viewModel()
) {
    val items = viewModel.cartItems

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Keranjang Saya") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        },
        bottomBar = {
            if (items.isNotEmpty()) {
                Surface(
                    shadowElevation = 8.dp,
                    color = Color.White
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Total:", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            Text(
                                "Rp ${formatRupiah(viewModel.hitungTotal())}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = onCheckoutClick,
                            modifier = Modifier.fillMaxWidth().height(50.dp)
                        ) {
                            Text("Checkout (${items.size} Barang)")
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        if (items.isEmpty()) {
            Box(
                modifier = Modifier.padding(innerPadding).fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Keranjang masih kosong", color = Color.Gray)
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(innerPadding).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(items) { item ->
                    Card(
                        elevation = CardDefaults.cardElevation(2.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp).fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AsyncImage(
                                model = item.produk.gambar ?: "https://via.placeholder.com/150",
                                contentDescription = null,
                                modifier = Modifier.size(80.dp),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(item.produk.nama ?: "-", fontWeight = FontWeight.Bold)
                                Text("Rp ${formatRupiah(item.produk.harga ?: 0.0)}")
                            }

                            // Tombol Plus Minus
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(onClick = { viewModel.kurangJumlah(item) }) {
                                    Icon(Icons.Default.Delete, null, tint = Color.Red) // Ganti jadi minus jika mau
                                }
                                Text("${item.jumlah}", fontWeight = FontWeight.Bold)
                                IconButton(onClick = { viewModel.tambahJumlah(item) }) {
                                    Icon(Icons.Default.Add, null)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ... kode KeranjangScreen lainnya di atas ...

// ✅ TAMBAHKAN INI DI BAGIAN PALING BAWAH FILE
private fun formatRupiah(number: Double): String {
    val format = java.text.NumberFormat.getCurrencyInstance(java.util.Locale("id", "ID"))
    return format.format(number).replace("Rp", "Rp ").substringBefore(",")
}