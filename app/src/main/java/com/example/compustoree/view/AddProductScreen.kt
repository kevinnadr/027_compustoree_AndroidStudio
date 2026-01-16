package com.example.compustoree.view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.compustoree.viewmodel.AddProductViewModel
import java.io.ByteArrayOutputStream
import java.io.InputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProductScreen(
    onBackClick: () -> Unit,
    onSuccess: () -> Unit,
    viewModel: AddProductViewModel = viewModel()
) {
    val context = LocalContext.current
    var selectedBitmap by remember { mutableStateOf<Bitmap?>(null) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            val base64String = uriToBase64(context, uri)
            if (base64String != null) {
                viewModel.gambarUrl = "data:image/jpeg;base64,$base64String"
                val inputStream = context.contentResolver.openInputStream(uri)
                selectedBitmap = BitmapFactory.decodeStream(inputStream)
            } else {
                Toast.makeText(context, "Gagal memproses gambar", Toast.LENGTH_SHORT).show()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tambah Barang Baru") },
                navigationIcon = { IconButton(onClick = onBackClick) { Icon(Icons.Default.ArrowBack, null) } }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // --- PREVIEW FOTO ---
            Box(
                modifier = Modifier
                    .size(150.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, Color.Gray, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                if (selectedBitmap != null) {
                    Image(
                        bitmap = selectedBitmap!!.asImageBitmap(),
                        contentDescription = "Preview",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else if (viewModel.gambarUrl.isNotEmpty()) {
                    Image(
                        painter = rememberAsyncImagePainter(viewModel.gambarUrl),
                        contentDescription = "URL Preview",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Icon(Icons.Default.Add, contentDescription = null, tint = Color.Gray)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = { launcher.launch("image/*") }) { Text("Pilih Foto Galeri") }
            Spacer(modifier = Modifier.height(16.dp))

            // --- FORM INPUT ---
            OutlinedTextField(value = viewModel.nama, onValueChange = { viewModel.nama = it }, label = { Text("Nama Produk") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(value = viewModel.kategori, onValueChange = { viewModel.kategori = it }, label = { Text("Kategori") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(value = viewModel.harga, onValueChange = { viewModel.harga = it }, label = { Text("Harga (Rp)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(value = viewModel.stok, onValueChange = { viewModel.stok = it }, label = { Text("Stok") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(value = viewModel.deskripsi, onValueChange = { viewModel.deskripsi = it }, label = { Text("Deskripsi") }, modifier = Modifier.fillMaxWidth(), minLines = 3)

            // ⚠️ PENTING: OutlinedTextField untuk URL SUDAH DIHAPUS DISINI AGAR TIDAK CRASH

            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = { viewModel.tambahProduk(onSuccess) },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                enabled = !viewModel.isLoading
            ) {
                if (viewModel.isLoading) CircularProgressIndicator(color = Color.White) else Text("SIMPAN PRODUK")
            }
        }
    }
}

// FUNGSI UTAMA KONVERSI GAMBAR (Dipakai juga oleh EditProductScreen)
fun uriToBase64(context: Context, uri: Uri): String? {
    try {
        val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
        val bitmap = BitmapFactory.decodeStream(inputStream)
        val byteArrayOutputStream = ByteArrayOutputStream()
        // Kompres 50% agar tidak terlalu berat
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.NO_WRAP)
    } catch (e: Exception) {
        e.printStackTrace()
        return null
    }
}