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
import com.example.compustoree.viewmodel.AddProductViewModel

// Warna Tema
private val BluePrimary = Color(0xFF2563EB)
private val BgColor = Color(0xFFF8FAFC)
private val TextDark = Color(0xFF1E293B)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProductScreen(
    onBackClick: () -> Unit,
    viewModel: AddProductViewModel = viewModel()
) {
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Tambah Produk Baru", fontWeight = FontWeight.Bold) },
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
        containerColor = BgColor
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(20.dp)
        ) {

            // --- 1. SECTION GAMBAR ---
            Text("Foto Produk", fontWeight = FontWeight.Bold, color = TextDark)
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
                            contentDescription = "Preview",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop,
                            error = rememberVectorPainter(Icons.Default.BrokenImage)
                        )
                    } else {
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
                onValueChange = { viewModel.gambarUrl = it.trim() },
                label = { Text("Link Gambar (URL)") },
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
            InputItem(
                value = viewModel.nama,
                onValueChange = { viewModel.nama = it },
                label = "Nama Produk",
                icon = Icons.Default.Label
            )

            // Kategori
            InputItem(
                value = viewModel.kategori,
                onValueChange = { viewModel.kategori = it },
                label = "Kategori (Laptop/Mouse)",
                icon = Icons.Default.Category
            )

            // Row: Harga & Stok (Sebelah-sebelahan)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                InputItem(
                    value = viewModel.harga,
                    onValueChange = { viewModel.harga = it },
                    label = "Harga (Rp)",
                    icon = Icons.Default.AttachMoney,
                    keyboardType = KeyboardType.Number,
                    modifier = Modifier.weight(1f)
                )

                InputItem(
                    value = viewModel.stok,
                    onValueChange = { viewModel.stok = it },
                    label = "Stok",
                    icon = Icons.Default.Inventory,
                    keyboardType = KeyboardType.Number,
                    modifier = Modifier.weight(1f)
                )
            }

            // Deskripsi
            InputItem(
                value = viewModel.deskripsi,
                onValueChange = { viewModel.deskripsi = it },
                label = "Deskripsi Lengkap",
                icon = Icons.Default.Description,
                isSingleLine = false,
                minLines = 3
            )

            Spacer(modifier = Modifier.height(32.dp))

            // --- 3. TOMBOL SIMPAN ---
            Button(
                onClick = { viewModel.saveProduct(onSuccess = onBackClick) },
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
                    Text("SIMPAN PRODUK", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }

            // Pesan Error
            if (viewModel.message.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFEE2E2)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Error, null, tint = Color.Red)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(viewModel.message, color = Color.Red, fontSize = 12.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

// --- HELPER COMPONENT (Agar kode lebih bersih) ---
@Composable
fun InputItem(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
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