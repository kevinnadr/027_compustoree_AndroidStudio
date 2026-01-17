package com.example.compustoree.view

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.compustoree.viewmodel.RegisterViewModel

// Warna Tema Konsisten
private val BluePrimary = Color(0xFF2563EB)
private val BlueDark = Color(0xFF1E3A8A)

@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    onLoginClick: () -> Unit,
    viewModel: RegisterViewModel = viewModel()
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    // State untuk intip password
    var passwordVisible by remember { mutableStateOf(false) }

    // Handle Toast
    LaunchedEffect(viewModel.message) {
        if (viewModel.message.isNotEmpty()) {
            Toast.makeText(context, viewModel.message, Toast.LENGTH_SHORT).show()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                // Gradient Biru Modern
                brush = Brush.verticalGradient(
                    colors = listOf(BluePrimary, BlueDark)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        // --- KARTU FORM ---
        Card(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth()
                .shadow(16.dp, RoundedCornerShape(24.dp)),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .padding(32.dp)
                    .verticalScroll(scrollState), // Agar bisa discroll di HP kecil
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 1. Icon Header
                Box(
                    modifier = Modifier
                        .size(70.dp)
                        .background(BluePrimary.copy(alpha = 0.1f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PersonAdd,
                        contentDescription = null,
                        modifier = Modifier.size(35.dp),
                        tint = BluePrimary
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // 2. Judul
                Text(
                    text = "Buat Akun Baru",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B)
                )
                Text(
                    text = "Lengkapi data diri Anda",
                    fontSize = 14.sp,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(24.dp))

                // 3. Form Input
                // Nama
                RegisterInput(
                    value = viewModel.nama,
                    onValueChange = { viewModel.nama = it },
                    label = "Nama Lengkap",
                    icon = Icons.Default.Badge
                )

                // Email
                RegisterInput(
                    value = viewModel.email,
                    onValueChange = { viewModel.email = it },
                    label = "Alamat Email",
                    icon = Icons.Default.Email,
                    keyboardType = KeyboardType.Email
                )

                // Password
                OutlinedTextField(
                    value = viewModel.password,
                    onValueChange = { viewModel.password = it },
                    label = { Text("Password") },
                    leadingIcon = { Icon(Icons.Default.Lock, null, tint = BluePrimary) },
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = null,
                                tint = Color.Gray
                            )
                        }
                    },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BluePrimary,
                        unfocusedBorderColor = Color.LightGray,
                        focusedLabelColor = BluePrimary
                    )
                )

                // No HP
                RegisterInput(
                    value = viewModel.noHp,
                    onValueChange = { viewModel.noHp = it },
                    label = "Nomor WhatsApp/HP",
                    icon = Icons.Default.Phone,
                    keyboardType = KeyboardType.Phone
                )

                Spacer(modifier = Modifier.height(24.dp))

                // 4. Tombol Daftar
                Button(
                    onClick = { viewModel.register(onSuccess = onRegisterSuccess) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BluePrimary),
                    enabled = !viewModel.isLoading
                ) {
                    if (viewModel.isLoading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Text("DAFTAR SEKARANG", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 5. Link Login
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Sudah punya akun?", fontSize = 14.sp, color = Color.Gray)
                    TextButton(onClick = onLoginClick) {
                        Text("Login disini", fontWeight = FontWeight.Bold, color = BluePrimary)
                    }
                }
            }
        }
    }
}

// Helper Composable untuk Input Field Biasa
@Composable
fun RegisterInput(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        leadingIcon = { Icon(icon, null, tint = BluePrimary) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp),
        shape = RoundedCornerShape(12.dp),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = BluePrimary,
            unfocusedBorderColor = Color.LightGray,
            focusedLabelColor = BluePrimary
        )
    )
}