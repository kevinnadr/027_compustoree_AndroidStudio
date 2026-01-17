package com.example.compustoree.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.compustoree.viewmodel.AddProductViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProductScreen(
    onBackClick: () -> Unit,
    viewModel: AddProductViewModel = viewModel()
) {
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tambah Produk Baru") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(scrollState)
        ) {

            // --- 1. INPUT LINK GAMBAR (DIPERBAIKI) ---
            OutlinedTextField(
                value = viewModel.gambarUrl,
                onValueChange = {
                    // ✅ PENTING: .trim() menghapus spasi/enter yang tidak sengaja ter-copy
                    viewModel.gambarUrl = it.trim()
                },
                label = { Text("Link Gambar (URL)") },
                placeholder = { Text("Paste link gambar di sini...") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true, // ✅ Agar tidak bisa di-enter (mencegah link putus)
                maxLines = 1
            )

            Spacer(modifier = Modifier.height(12.dp))

            // --- 2. PREVIEW GAMBAR CANGGIH ---
            Text("Preview Gambar:", style = MaterialTheme.typography.bodySmall)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFEEEEEE))
            ) {
                if (viewModel.gambarUrl.isNotEmpty()) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(viewModel.gambarUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = "Preview",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                        // Jika ERROR (Link rusak), tampilkan ikon Peringatan Segitiga
                        error = rememberVectorPainter(Icons.Default.Warning)
                    )
                } else {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Masukkan Link untuk melihat preview", color = Color.Gray)
                    }
                }
            }

            // Info jika error
            if (viewModel.gambarUrl.isNotEmpty()) {
                Text(
                    text = "Jika muncul ikon Segitiga (!), berarti link rusak atau tidak bisa diakses.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- 3. INPUT DATA LAINNYA ---
            OutlinedTextField(
                value = viewModel.nama, onValueChange = { viewModel.nama = it },
                label = { Text("Nama Produk") }, modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = viewModel.kategori, onValueChange = { viewModel.kategori = it },
                label = { Text("Kategori (Laptop/Mouse/Monitor)") }, modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = viewModel.harga, onValueChange = { viewModel.harga = it },
                label = { Text("Harga (Rp)") }, modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = viewModel.stok, onValueChange = { viewModel.stok = it },
                label = { Text("Stok") }, modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = viewModel.deskripsi, onValueChange = { viewModel.deskripsi = it },
                label = { Text("Deskripsi Produk") }, modifier = Modifier.fillMaxWidth(),
                minLines = 3, maxLines = 5
            )

            Spacer(modifier = Modifier.height(24.dp))

            // --- 4. TOMBOL SIMPAN ---
            Button(
                onClick = {
                    viewModel.saveProduct(onSuccess = onBackClick)
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                enabled = !viewModel.isLoading
            ) {
                if (viewModel.isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text("SIMPAN PRODUK")
                }
            }

            if (viewModel.message.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(viewModel.message, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}