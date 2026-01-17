package com.example.compustoree.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.compustoree.model.UserSession
import com.example.compustoree.viewmodel.DetailViewModel
import java.text.NumberFormat
import java.util.Locale

// Warna Tema (Konsisten)
private val BluePrimary = Color(0xFF2563EB)
private val BgColor = Color(0xFFF8FAFC)
private val TextDark = Color(0xFF1E293B)

@Composable
fun DetailScreen(
    produkId: Int,
    onBackClick: () -> Unit,
    onBuyClick: (Int) -> Unit,
    onEditClick: (Int) -> Unit,
    viewModel: DetailViewModel = viewModel()
) {
    val isAdmin = UserSession.currentUser?.role == "admin"
    val produk = viewModel.produk
    val scrollState = rememberScrollState()

    // Load data otomatis
    LaunchedEffect(produkId) {
        viewModel.loadProduk(produkId)
    }

    Box(modifier = Modifier.fillMaxSize().background(BgColor)) {
        if (viewModel.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = BluePrimary)
            }
        } else if (produk != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(bottom = 80.dp) // Padding bawah agar konten tidak tertutup tombol beli
            ) {
                // --- 1. HEADER GAMBAR PRODUK ---
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(350.dp) // Gambar besar
                        .background(Color.White)
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(produk.gambar)
                            .crossfade(true)
                            .build(),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )

                    // Gradient hitam di bawah gambar agar teks terlihat (opsional)
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.1f)),
                                    startY = 300f
                                )
                            )
                    )
                }

                // --- 2. KONTEN DETAIL (Card Melengkung ke Atas) ---
                Column(
                    modifier = Modifier
                        .offset(y = (-24).dp) // Efek menumpuk ke atas gambar
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                        .background(BgColor)
                        .padding(24.dp)
                ) {
                    // Kategori Badge
                    Surface(
                        color = BluePrimary.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = produk.kategori?.uppercase() ?: "UMUM",
                            color = BluePrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Nama Produk
                    Text(
                        text = produk.nama ?: "Tanpa Nama",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark,
                        lineHeight = 32.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Harga & Stok
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = formatRupiah(produk.harga ?: 0.0),
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = BluePrimary
                        )

                        // Info Stok
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if ((produk.stok ?: 0) > 0) Icons.Default.CheckCircle else Icons.Default.KeyboardArrowLeft,
                                contentDescription = null,
                                tint = if ((produk.stok ?: 0) > 0) Color(0xFF16A34A) else Color.Red,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Stok: ${produk.stok}",
                                color = Color.Gray,
                                fontSize = 14.sp
                            )
                        }
                    }

                    Divider(modifier = Modifier.padding(vertical = 24.dp), color = Color.LightGray.copy(alpha = 0.3f))

                    // Deskripsi
                    Text(
                        text = "Deskripsi Produk",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = TextDark
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = produk.deskripsi ?: "Tidak ada deskripsi tersedia untuk produk ini.",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        lineHeight = 22.sp
                    )

                    // --- ADMIN PANEL (Hanya Muncul jika Admin) ---
                    if (isAdmin) {
                        Spacer(modifier = Modifier.height(32.dp))
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF7ED)), // Warna Oranye muda
                            border = BoxDefaults.border,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Person, null, tint = Color(0xFFEA580C))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Admin Area", fontWeight = FontWeight.Bold, color = Color(0xFFEA580C))
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("ID Produk: ${produk.id}", fontSize = 12.sp)
                                Text("Merk: ${produk.merk ?: "-"}", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }

        // --- 3. CUSTOM FLOATING TOP BAR ---
        // Tombol Kembali & Edit Melayang di atas
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 48.dp, start = 20.dp, end = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Tombol Back (Bulat Putih)
            IconButton(
                onClick = onBackClick,
                modifier = Modifier
                    .size(44.dp)
                    .background(Color.White, CircleShape)
                    .padding(4.dp)
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Kembali", tint = TextDark)
            }

            // Tombol Edit (Khusus Admin)
            if (isAdmin) {
                IconButton(
                    onClick = { onEditClick(produkId) },
                    modifier = Modifier
                        .size(44.dp)
                        .background(BluePrimary, CircleShape)
                        .padding(4.dp)
                ) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color.White)
                }
            }
        }

        // --- 4. BOTTOM BAR STICKY (Tombol Beli) ---
        if (!isAdmin && produk != null) {
            Surface(
                modifier = Modifier.align(Alignment.BottomCenter),
                color = Color.White,
                shadowElevation = 16.dp,
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
            ) {
                Row(
                    modifier = Modifier
                        .padding(20.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Total Harga", fontSize = 12.sp, color = Color.Gray)
                        Text(
                            text = formatRupiah(produk.harga ?: 0.0),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = BluePrimary
                        )
                    }

                    Button(
                        onClick = { onBuyClick(produkId) },
                        modifier = Modifier
                            .height(50.dp)
                            .width(160.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BluePrimary),
                        elevation = ButtonDefaults.buttonElevation(4.dp)
                    ) {
                        Icon(Icons.Default.ShoppingCart, null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Beli", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// Helper Format Rupiah (Private)
private fun formatRupiah(number: Double): String {
    val format = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
    return format.format(number).replace("Rp", "Rp ").substringBefore(",")
}

// Extension untuk Border Box (Optional)
private object BoxDefaults {
    val border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFE0B2))
}