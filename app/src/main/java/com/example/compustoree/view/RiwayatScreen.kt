package com.example.compustoree.view

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.compustoree.model.RiwayatOrder
import com.example.compustoree.viewmodel.RiwayatUiState
import com.example.compustoree.viewmodel.RiwayatViewModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale

// Warna Tema (Sesuai dengan Home & Profile)
private val BluePrimary = Color(0xFF2563EB)
private val BlueDark = Color(0xFF1E3A8A)
private val BgColor = Color(0xFFF8FAFC)

@Composable
fun RiwayatScreen(
    viewModel: RiwayatViewModel = viewModel()
) {
    LaunchedEffect(Unit) {
        viewModel.loadData()
    }

    Scaffold(
        containerColor = BgColor
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            // --- HEADER MODERN ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp) // Header tinggi
            ) {
                // Background Lengkung
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                        .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
                        .background(
                            Brush.horizontalGradient(colors = listOf(BluePrimary, BlueDark))
                        )
                )

                // Teks Judul
                Column(
                    modifier = Modifier
                        .padding(top = 40.dp, start = 24.dp)
                ) {
                    Text(
                        text = if (viewModel.isAdmin) "Admin Dashboard" else "Pesanan Saya",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 14.sp
                    )
                    Text(
                        text = if (viewModel.isAdmin) "Kelola Pesanan" else "Riwayat Belanja",
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // --- LIST PESANAN ---
            when (val state = viewModel.uiState) {
                is RiwayatUiState.Loading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = BluePrimary)
                    }
                }
                is RiwayatUiState.Success -> {
                    if (state.orders.isEmpty()) {
                        EmptyState()
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                            modifier = Modifier.weight(1f) // Isi sisa layar
                        ) {
                            items(state.orders) { order ->
                                if (viewModel.isAdmin) {
                                    AdminOrderCard(order, viewModel)
                                } else {
                                    UserOrderCard(order)
                                }
                            }
                        }
                    }
                }
                is RiwayatUiState.Error -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Refresh, null, tint = Color.Red, modifier = Modifier.size(40.dp))
                            Text("Gagal memuat data", color = Color.Gray)
                            Button(onClick = { viewModel.loadData() }, modifier = Modifier.padding(top = 8.dp)) {
                                Text("Coba Lagi")
                            }
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// 1. KARTU TRANSAKSI USER (MODERN)
// ==========================================
@Composable
fun UserOrderCard(order: RiwayatOrder) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(0.dp), // Shadow kita atur manual biar soft
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(16.dp))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Kartu: Tanggal & Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.DateRange, null, modifier = Modifier.size(16.dp), tint = Color.Gray)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(formatDate(order.tanggalTransaksi), fontSize = 12.sp, color = Color.Gray)
                }
                StatusBadge(order.statusPengiriman ?: "Diproses")
            }

            Divider(color = Color.LightGray.copy(alpha = 0.2f), modifier = Modifier.padding(vertical = 12.dp))

            // Body Kartu: Produk
            Row(verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(
                    model = order.gambarProduk ?: "",
                    contentDescription = null,
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .border(1.dp, Color.LightGray.copy(alpha = 0.3f), RoundedCornerShape(10.dp)),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = order.namaProduk ?: "Produk Tidak Dikenal",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "${order.jumlah} Barang",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Footer: Total Harga
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Total Belanja", fontSize = 11.sp, color = Color.Gray)
                    Text(
                        text = formatCurrency(order.totalHarga ?: 0.0),
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = BluePrimary
                    )
                }
                // Tombol Beli Lagi (Opsional)
                Button(
                    onClick = { /* TODO: Arahkan ke Detail */ },
                    colors = ButtonDefaults.buttonColors(containerColor = BluePrimary.copy(alpha = 0.1f)),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                    modifier = Modifier.height(32.dp),
                    elevation = ButtonDefaults.buttonElevation(0.dp)
                ) {
                    Text("Detail", color = BluePrimary, fontSize = 12.sp)
                }
            }
        }
    }
}

// ==========================================
// 2. KARTU TRANSAKSI ADMIN (MODERN)
// ==========================================
@Composable
fun AdminOrderCard(order: RiwayatOrder, viewModel: RiwayatViewModel) {
    var showDialog by remember { mutableStateOf(false) }

    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(16.dp))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Baris Atas: Nama Pembeli & Badge Status
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = CircleShape,
                    color = BluePrimary.copy(alpha = 0.1f),
                    modifier = Modifier.size(32.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Person, null, tint = BluePrimary, modifier = Modifier.size(16.dp))
                    }
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(order.namaPembeli ?: "Guest", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text(formatDate(order.tanggalTransaksi), fontSize = 11.sp, color = Color.Gray)
                }
                Spacer(modifier = Modifier.weight(1f))
                StatusBadge(order.statusPengiriman ?: "Diproses")
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Info Alamat
            Row(verticalAlignment = Alignment.Top) {
                Icon(Icons.Default.LocationOn, null, tint = Color.Gray, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = order.alamatPengiriman ?: "Alamat tidak tersedia",
                    fontSize = 12.sp,
                    color = Color.DarkGray,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Divider(modifier = Modifier.padding(vertical = 12.dp), color = Color.LightGray.copy(alpha = 0.2f))

            // Produk Info
            Row(verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(
                    model = order.gambarProduk ?: "",
                    contentDescription = null,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(order.namaProduk ?: "-", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                    Text(
                        text = "Total: ${formatCurrency(order.totalHarga ?: 0.0)}",
                        fontSize = 13.sp,
                        color = BluePrimary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Tombol Aksi
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { viewModel.deleteOrder(order.idTransaksi) }) {
                    Icon(Icons.Default.Delete, null, tint = Color.Red.copy(alpha = 0.7f))
                }

                Button(
                    onClick = { showDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = BluePrimary),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.height(36.dp)
                ) {
                    Icon(Icons.Default.Edit, null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Update Status", fontSize = 12.sp)
                }
            }
        }
    }

    // Dialog Update Status
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Update Status Pesanan", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    val statusOptions = listOf("Diproses", "Dikirim", "Selesai", "Dibatalkan")
                    statusOptions.forEach { status ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.updateStatus(order.idTransaksi, status)
                                    showDialog = false
                                }
                                .padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = (order.statusPengiriman == status),
                                onClick = {
                                    viewModel.updateStatus(order.idTransaksi, status)
                                    showDialog = false
                                },
                                colors = RadioButtonDefaults.colors(selectedColor = BluePrimary)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(status, fontSize = 16.sp)
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Batal", color = Color.Red)
                }
            },
            containerColor = Color.White
        )
    }
}

// ==========================================
// 3. KOMPONEN PENDUKUNG
// ==========================================

@Composable
fun StatusBadge(status: String) {
    val (bgColor, textColor) = when (status.lowercase()) {
        "selesai" -> Pair(Color(0xFFDCFCE7), Color(0xFF166534)) // Hijau Soft
        "dikirim" -> Pair(Color(0xFFDBEAFE), Color(0xFF1E40AF)) // Biru Soft
        "dibatalkan" -> Pair(Color(0xFFFEE2E2), Color(0xFF991B1B)) // Merah Soft
        else -> Pair(Color(0xFFFEF3C7), Color(0xFF92400E)) // Kuning (Diproses)
    }

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(8.dp),
    ) {
        Text(
            text = status.uppercase(),
            color = textColor,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            letterSpacing = 0.5.sp
        )
    }
}

@Composable
fun EmptyState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Outlined.DateRange,
                contentDescription = null,
                tint = Color.LightGray,
                modifier = Modifier.size(80.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Belum ada riwayat transaksi",
                color = Color.Gray,
                fontSize = 16.sp
            )
        }
    }
}

// Helper Format Tanggal
fun formatDate(dateString: String?): String {
    if (dateString == null) return "-"
    return try {
        val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        val date = parser.parse(dateString)
        val formatter = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("id", "ID"))
        formatter.format(date ?: "")
    } catch (e: Exception) {
        if (dateString.length >= 10) dateString.substring(0, 10) else dateString
    }
}

// Helper Format Rupiah (Private agar tidak konflik)
private fun formatCurrency(number: Double): String {
    val format = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
    return format.format(number).replace("Rp", "Rp ").substringBefore(",")
}