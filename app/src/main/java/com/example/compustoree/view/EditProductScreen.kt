package com.example.compustoree.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.compustoree.viewmodel.EditProductViewModel

// Definisi Warna Tema (Konsisten)
private val BluePrimary = Color(0xFF2563EB)
private val BgColor = Color(0xFFF8FAFC)
private val TextDark = Color(0xFF1E293B)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProductScreen(
    produkId: Int,
    onBackClick: () -> Unit,
    viewModel: EditProductViewModel = viewModel()
) {
    // Load Data Otomatis
    LaunchedEffect(produkId) {
        viewModel.loadProduct(produkId)
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Edit Produk", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        containerColor = BgColor // Background abu-abu bersih
    ) { innerPadding ->
        // Cek Loading
        if (viewModel.isLoading && viewModel.nama.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = BluePrimary)
            }
        } else {
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp)
            ) {
                // --- 1. SECTION GAMBAR ---
                Text("Gambar Produk", fontWeight = FontWeight.Bold, color = TextDark)
                Spacer(modifier = Modifier.height(8.dp))

                // Card Preview
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        if (viewModel.gambarUrl.isNotEmpty()) {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(viewModel.gambarUrl)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop,
                                error = rememberVectorPainter(Icons.Default.BrokenImage)
                            )
                        } else {
                            // Tampilan jika gambar kosong/rusak
                            Column(
                                modifier = Modifier.align(Alignment.Center),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(Icons.Default.Image, null, tint = Color.LightGray, modifier = Modifier.size(48.dp))
                                Text("Preview Gambar", color = Color.Gray)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Input URL
                OutlinedTextField(
                    value = viewModel.gambarUrl,
                    onValueChange = { viewModel.gambarUrl = it },
                    label = { Text("URL Gambar") },
                    placeholder = { Text("https://...") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    leadingIcon = { Icon(Icons.Default.Link, null) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BluePrimary,
                        unfocusedBorderColor = Color.LightGray,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    )
                )

                Spacer(modifier = Modifier.height(24.dp))

                // --- 2. INFORMASI PRODUK ---
                Text("Detail Produk", fontWeight = FontWeight.Bold, color = TextDark)
                Spacer(modifier = Modifier.height(12.dp))

                // Nama Produk
                EditField(
                    label = "Nama Produk",
                    value = viewModel.nama,
                    onValueChange = { viewModel.nama = it },
                    icon = Icons.Default.Label
                )

                // Kategori
                EditField(
                    label = "Kategori (Laptop/Mouse/dll)",
                    value = viewModel.kategori,
                    onValueChange = { viewModel.kategori = it },
                    icon = Icons.Default.Category
                )

                // Row: Harga & Stok (Sebelah-sebelahan biar rapi)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    EditField(
                        label = "Harga (Rp)",
                        value = viewModel.harga,
                        onValueChange = { viewModel.harga = it },
                        icon = Icons.Default.AttachMoney,
                        keyboardType = KeyboardType.Number,
                        modifier = Modifier.weight(1f)
                    )

                    EditField(
                        label = "Stok",
                        value = viewModel.stok,
                        onValueChange = { viewModel.stok = it },
                        icon = Icons.Default.Inventory,
                        keyboardType = KeyboardType.Number,
                        modifier = Modifier.weight(1f)
                    )
                }

                // Deskripsi
                EditField(
                    label = "Deskripsi Lengkap",
                    value = viewModel.deskripsi,
                    onValueChange = { viewModel.deskripsi = it },
                    icon = Icons.Default.Description,
                    isSingleLine = false,
                    minLines = 4
                )

                Spacer(modifier = Modifier.height(32.dp))

                // --- 3. TOMBOL SIMPAN ---
                Button(
                    onClick = {
                        viewModel.updateProduct(produkId, onSuccess = onBackClick)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BluePrimary),
                    enabled = !viewModel.isLoading
                ) {
                    if (viewModel.isLoading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Icon(Icons.Default.Save, null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("SIMPAN PERUBAHAN", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

// --- HELPER COMPONENT (Agar kodingan utama bersih) ---
@Composable
fun EditField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    icon: ImageVector,
    keyboardType: KeyboardType = KeyboardType.Text,
    isSingleLine: Boolean = true,
    minLines: Int = 1,
    modifier: Modifier = Modifier.fillMaxWidth()
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        leadingIcon = { Icon(icon, null, tint = Color.Gray) },
        modifier = modifier.padding(bottom = 12.dp),
        shape = RoundedCornerShape(12.dp),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        singleLine = isSingleLine,
        minLines = minLines,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = BluePrimary,
            unfocusedBorderColor = Color.LightGray,
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White
        )
    )
}