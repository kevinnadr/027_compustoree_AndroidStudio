package com.example.compustoree.view

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.compustoree.model.UserSession
import com.example.compustoree.viewmodel.CheckoutViewModel
import java.text.NumberFormat
import java.util.Locale

// Warna Tema Konsisten
private val BluePrimary = Color(0xFF2563EB)
private val BlueDark = Color(0xFF1E3A8A)
private val BgColor = Color(0xFFF8FAFC)
private val CardBg = Color.White

@Composable
fun CheckoutScreen(
    produkId: Int,
    onBackClick: () -> Unit,
    onSuccess: () -> Unit,
    viewModel: CheckoutViewModel = viewModel()
) {
    val context = LocalContext.current
    val produk = viewModel.produk
    val scrollState = rememberScrollState()

    // Load Data
    LaunchedEffect(produkId) { viewModel.loadProduk(produkId) }

    // Toast Handler
    LaunchedEffect(viewModel.message) {
        if (viewModel.message.isNotEmpty()) {
            Toast.makeText(context, viewModel.message, Toast.LENGTH_SHORT).show()
            viewModel.message = "" // Reset agar toast tidak muncul terus
        }
    }

    Scaffold(
        containerColor = BgColor,
        bottomBar = {
            // --- BOTTOM BAR PEMBAYARAN ---
            if (produk != null) {
                Surface(
                    color = Color.White,
                    shadowElevation = 16.dp,
                    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .padding(horizontal = 24.dp, vertical = 20.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Total Pembayaran", fontSize = 12.sp, color = Color.Gray)
                            Text(
                                "Rp ${formatRupiah(viewModel.totalBayar)}",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = BluePrimary
                            )
                        }

                        Button(
                            onClick = { viewModel.buatPesanan(onSuccess) },
                            modifier = Modifier
                                .height(50.dp)
                                .width(160.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = BluePrimary),
                            enabled = !viewModel.isLoading
                        ) {
                            if (viewModel.isLoading) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                            } else {
                                Text("Bayar Sekarang", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        if (produk == null) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = BluePrimary)
            }
        } else {
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .verticalScroll(scrollState)
            ) {
                // --- 1. HEADER GRADIENT ---
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
                        .background(Brush.horizontalGradient(listOf(BluePrimary, BlueDark)))
                ) {
                    Row(
                        modifier = Modifier
                            .padding(top = 40.dp, start = 16.dp)
                            .align(Alignment.TopStart),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = onBackClick) {
                            Icon(Icons.Default.ArrowBack, null, tint = Color.White)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Konfirmasi Pesanan",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Column(modifier = Modifier.padding(24.dp)) {

                    // --- 2. DETAIL PRODUK (CARD) ---
                    Text("Barang yang dibeli", fontWeight = FontWeight.Bold, color = Color.Gray, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(8.dp))

                    Card(
                        colors = CardDefaults.cardColors(containerColor = CardBg),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth().shadow(4.dp, RoundedCornerShape(16.dp))
                    ) {
                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            AsyncImage(
                                model = produk.gambar,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(70.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color.LightGray),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(produk.nama ?: "", fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                Text("${viewModel.jumlahBeli} x Rp ${formatRupiah(produk.harga ?: 0.0)}", fontSize = 12.sp, color = Color.Gray)
                            }

                            // Stepper Quantity
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .background(BgColor, RoundedCornerShape(8.dp))
                                    .padding(4.dp)
                            ) {
                                Icon(
                                    Icons.Default.Remove, null,
                                    modifier = Modifier.size(20.dp).clickable { if (viewModel.jumlahBeli > 1) viewModel.jumlahBeli-- },
                                    tint = if (viewModel.jumlahBeli > 1) Color.Black else Color.Gray
                                )
                                Text(
                                    "${viewModel.jumlahBeli}",
                                    modifier = Modifier.padding(horizontal = 12.dp),
                                    fontWeight = FontWeight.Bold
                                )
                                Icon(
                                    Icons.Default.Add, null,
                                    modifier = Modifier.size(20.dp).clickable { viewModel.jumlahBeli++ }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // --- 3. ALAMAT PENGIRIMAN ---
                    SectionHeader(icon = Icons.Default.LocationOn, title = "Alamat Pengiriman")
                    OutlinedTextField(
                        value = viewModel.alamat,
                        onValueChange = { viewModel.alamat = it },
                        placeholder = { Text("Jalan, Nomor Rumah, RT/RW, Kota...") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BluePrimary,
                            unfocusedBorderColor = Color.LightGray,
                            focusedContainerColor = CardBg,
                            unfocusedContainerColor = CardBg
                        ),
                        minLines = 2
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // --- 4. OPSI PENGIRIMAN ---
                    SectionHeader(icon = Icons.Default.LocalShipping, title = "Kurir Pengiriman")
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        ShippingOption("JNE", "Rp 20rb", viewModel.kurir == "JNE") { viewModel.kurir = "JNE" }
                        ShippingOption("J&T", "Rp 15rb", viewModel.kurir == "J&T") { viewModel.kurir = "J&T" }
                        ShippingOption("SiCepat", "Rp 18rb", viewModel.kurir == "SiCepat") { viewModel.kurir = "SiCepat" }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // --- 5. METODE PEMBAYARAN ---
                    SectionHeader(icon = Icons.Default.Payment, title = "Metode Pembayaran")
                    Card(
                        colors = CardDefaults.cardColors(containerColor = CardBg),
                        shape = RoundedCornerShape(12.dp),
                        border = UserSession.currentUser?.let { null } ?: androidx.compose.foundation.BorderStroke(1.dp, Color.LightGray)
                    ) {
                        Column {
                            PaymentOption("Transfer Bank", viewModel.metodeBayar) { viewModel.metodeBayar = it }
                            Divider(color = BgColor)
                            PaymentOption("COD (Bayar di Tempat)", viewModel.metodeBayar) { viewModel.metodeBayar = it }
                            Divider(color = BgColor)
                            PaymentOption("E-Wallet (OVO/GoPay/QRIS)", viewModel.metodeBayar) { viewModel.metodeBayar = it }
                        }
                    }

                    // --- QR CODE (Jika E-Wallet dipilih) ---
                    if (viewModel.metodeBayar.contains("E-Wallet")) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = androidx.compose.foundation.BorderStroke(2.dp, BluePrimary),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp).fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("Scan QRIS", fontWeight = FontWeight.Bold, color = BluePrimary)
                                Spacer(modifier = Modifier.height(12.dp))
                                AsyncImage(
                                    model = "https://api.qrserver.com/v1/create-qr-code/?size=200x200&data=BayarCompuStore_Rp${viewModel.totalBayar.toInt()}",
                                    contentDescription = "QR Code",
                                    modifier = Modifier.size(150.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Total: Rp ${formatRupiah(viewModel.totalBayar)}", fontSize = 12.sp, color = Color.Gray)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // --- 6. RINGKASAN BIAYA ---
                    Card(
                        colors = CardDefaults.cardColors(containerColor = CardBg),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            SummaryRow("Subtotal Produk", viewModel.subtotal)
                            SummaryRow("Ongkos Kirim", viewModel.ongkir)
                            Divider(modifier = Modifier.padding(vertical = 12.dp), color = Color.LightGray.copy(alpha = 0.5f))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Total Tagihan", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Text("Rp ${formatRupiah(viewModel.totalBayar)}", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = BluePrimary)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(80.dp)) // Ruang untuk BottomBar
                }
            }
        }
    }
}

// --- WIDGET HELPER UI ---

@Composable
fun SectionHeader(icon: ImageVector, title: String) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 12.dp)) {
        Icon(icon, null, tint = BluePrimary, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF1E293B))
    }
}

@Composable
fun RowScope.ShippingOption(name: String, price: String, isSelected: Boolean, onClick: () -> Unit) {
    val borderColor = if (isSelected) BluePrimary else Color.LightGray
    val bgColor = if (isSelected) BluePrimary.copy(alpha = 0.1f) else Color.White

    Card(
        modifier = Modifier.weight(1f).clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = bgColor),
        border = androidx.compose.foundation.BorderStroke(1.dp, borderColor),
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 12.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(name, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = if(isSelected) BluePrimary else Color.Black)
            Text(price, fontSize = 11.sp, color = Color.Gray)
        }
    }
}

@Composable
fun PaymentOption(name: String, selectedOption: String, onSelect: (String) -> Unit) {
    val isSelected = selectedOption == name
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect(name) }
            .background(if (isSelected) BluePrimary.copy(alpha = 0.05f) else Color.Transparent)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = isSelected,
            onClick = { onSelect(name) },
            colors = RadioButtonDefaults.colors(selectedColor = BluePrimary)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(name, fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal)
    }
}

@Composable
fun SummaryRow(label: String, amount: Double) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = Color.Gray, fontSize = 14.sp)
        Text("Rp ${formatRupiah(amount)}", fontWeight = FontWeight.Medium, fontSize = 14.sp)
    }
}

// Helper Format Rupiah (Private)
private fun formatRupiah(number: Double): String {
    val format = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
    return format.format(number).replace("Rp", "").trim()
}