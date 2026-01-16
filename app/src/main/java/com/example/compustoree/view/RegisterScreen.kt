package com.example.compustoree.view


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.compustoree.viewmodel.RegisterViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onBackClick: () -> Unit,
    onRegisterSuccess: () -> Unit,
    viewModel: RegisterViewModel = viewModel()
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Daftar Akun Baru") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) { Icon(Icons.Default.ArrowBack, null) }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()) // Agar bisa discroll kalau keyboard muncul
        ) {
            Text("Isi data diri Anda", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))

            // 1. Nama
            OutlinedTextField(
                value = viewModel.nama,
                onValueChange = { viewModel.nama = it },
                label = { Text("Nama Lengkap") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            // 2. Email
            OutlinedTextField(
                value = viewModel.email,
                onValueChange = { viewModel.email = it },
                label = { Text("Email (Unik)") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            // 3. No HP
            OutlinedTextField(
                value = viewModel.noHp,
                onValueChange = { viewModel.noHp = it },
                label = { Text("Nomor HP") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            // 4. Alamat
            OutlinedTextField(
                value = viewModel.alamat,
                onValueChange = { viewModel.alamat = it },
                label = { Text("Alamat Lengkap") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 3
            )
            Spacer(modifier = Modifier.height(24.dp))

            // Tombol Daftar
            Button(
                onClick = { viewModel.register(onRegisterSuccess) },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                enabled = !viewModel.isLoading
            ) {
                if (viewModel.isLoading) CircularProgressIndicator(color = Color.White)
                else Text("DAFTAR SEKARANG")
            }

            if (viewModel.message.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(viewModel.message, color = if(viewModel.message.contains("Berhasil")) Color.Green else Color.Red)
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text("Info: Password default akun baru adalah 'user123'", fontSize = 12.sp, color = Color.Gray)
        }
    }
}