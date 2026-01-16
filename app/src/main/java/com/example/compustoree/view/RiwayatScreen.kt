package com.example.compustoree.view

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.compustoree.model.RiwayatOrder
import com.example.compustoree.viewmodel.RiwayatUiState // Import State baru
import com.example.compustoree.viewmodel.RiwayatViewModel

@Composable
fun RiwayatScreen(
    viewModel: RiwayatViewModel = viewModel()
) {
    // Load data saat pertama kali
    LaunchedEffect(Unit) {
        viewModel.loadData()
    }

    // Ambil state terbaru dari ViewModel
    val uiState = viewModel.uiState

    Column(modifier = Modifier.padding(16.dp)) {
        val title = if (viewModel.isAdmin) "Kelola Pesanan (Admin)" else "Riwayat Belanja Saya"
        Text(title, fontSize = 24.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(16.dp))

        // --- LOGIKA UI BERDASARKAN STATUS (LOADING / SUKSES / ERROR) ---
        when (uiState) {
            is RiwayatUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is RiwayatUiState.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Gagal memuat data. Cek koneksi internet.", color = Color.Red)
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = { viewModel.loadData() }) { Text("Coba Lagi") }
                    }
                }
            }
            is RiwayatUiState.Success -> {
                val listRiwayat = uiState.orders

                if (listRiwayat.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Belum ada riwayat transaksi.", color = Color.Gray)
                    }
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(listRiwayat) { order ->
                            ItemRiwayat(
                                order = order,
                                isAdmin = viewModel.isAdmin,
                                onUpdateStatus = { id, status -> viewModel.updateStatus(id, status) },
                                onDeleteClick = { id -> viewModel.deleteOrder(id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ItemRiwayat(
    order: RiwayatOrder,
    isAdmin: Boolean,
    onUpdateStatus: (Int, String) -> Unit,
    onDeleteClick: (Int) -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    val currentStatus = order.status ?: "Diproses"
    val statusColor = when (currentStatus) {
        "Diproses" -> Color(0xFFFFA000)
        "Dikirim" -> Color(0xFF1976D2)
        "Selesai" -> Color(0xFF388E3C)
        "Dibatalkan" -> Color(0xFFD32F2F)
        else -> Color.Gray
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Hapus Pesanan?") },
            text = { Text("Data ini akan dihapus permanen.") },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteClick(order.id)
                        showDeleteDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) { Text("Hapus") }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Batal") }
            }
        )
    }

    Card(
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color.LightGray)
    ) {
        Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = currentStatus.uppercase(),
                        color = statusColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                    Text(
                        text = (order.tanggal ?: "2024-01-01").take(10),
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }

                if (isAdmin) {
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = Color.Red)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Divider()
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = order.namaBarang ?: "Produk Dihapus",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )

            val hargaSatuan = (order.totalHarga ?: 0.0) / (order.jumlah ?: 1)
            Text("${order.jumlah ?: 1} x Rp ${formatRupiah(hargaSatuan)}")

            Spacer(modifier = Modifier.height(4.dp))
            Text("Total: Rp ${formatRupiah(order.totalHarga ?: 0.0)}", fontWeight = FontWeight.Bold)

            if (isAdmin) {
                Spacer(modifier = Modifier.height(12.dp))
                Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F4C3))) {
                    Column(modifier = Modifier.padding(8.dp).fillMaxWidth()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Person, null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Pembeli: ${order.namaPembeli ?: "User Dihapus"}", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                        Text("Alamat: ${order.alamatKirim ?: "-"}", fontSize = 12.sp)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (currentStatus != "Diproses") {
                        Button(
                            onClick = { onUpdateStatus(order.id, "Diproses") },
                            modifier = Modifier.weight(1f).height(35.dp),
                            contentPadding = PaddingValues(0.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFA000))
                        ) { Text("Proses", fontSize = 10.sp) }
                    }

                    if (currentStatus != "Dikirim") {
                        Button(
                            onClick = { onUpdateStatus(order.id, "Dikirim") },
                            modifier = Modifier.weight(1f).height(35.dp),
                            contentPadding = PaddingValues(0.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2))
                        ) { Text("Kirim", fontSize = 10.sp) }
                    }

                    if (currentStatus != "Dibatalkan") {
                        Button(
                            onClick = { onUpdateStatus(order.id, "Dibatalkan") },
                            modifier = Modifier.weight(1f).height(35.dp),
                            contentPadding = PaddingValues(0.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F))
                        ) { Text("Batal", fontSize = 10.sp) }
                    }
                }
            }
        }
    }
}