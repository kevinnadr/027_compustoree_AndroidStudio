package com.example.compustoree.view

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.compustoree.model.UserSession
import com.example.compustoree.viewmodel.ProfileViewModel

// Definisi Warna Tema (Biru Tech)
private val BluePrimary = Color(0xFF2563EB)
private val BlueDark = Color(0xFF1E3A8A)
private val BgColor = Color(0xFFF8FAFC)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onLogout: () -> Unit,
    viewModel: ProfileViewModel = viewModel()
) {
    val context = LocalContext.current
    val currentUser = UserSession.currentUser

    // Handle Toast
    LaunchedEffect(viewModel.message) {
        if (viewModel.message.isNotEmpty()) {
            Toast.makeText(context, viewModel.message, Toast.LENGTH_SHORT).show()
            viewModel.message = ""
        }
    }

    Scaffold(
        containerColor = BgColor
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()) // Agar bisa discroll di HP kecil
        ) {

            // --- 1. HEADER PROFIL MEWAH ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp) // Header lebih tinggi
            ) {
                // Background Gradient Biru Lengkung
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .clip(RoundedCornerShape(bottomStart = 40.dp, bottomEnd = 40.dp))
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(BluePrimary, BlueDark)
                            )
                        )
                )

                // Judul Halaman
                Text(
                    text = "Profil Saya",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 40.dp)
                )

                // Foto Profil (Tengah-Bawah Header)
                Column(
                    modifier = Modifier.align(Alignment.BottomCenter),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Avatar Lingkaran dengan Border Putih Tebal & Shadow
                    Surface(
                        shape = CircleShape,
                        shadowElevation = 10.dp,
                        modifier = Modifier
                            .size(120.dp)
                            .border(4.dp, Color.White, CircleShape)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.background(Color(0xFFE2E8F0)) // Abu-abu muda
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                modifier = Modifier.size(70.dp),
                                tint = Color.Gray
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Nama & Email (Tampil jika BUKAN mode edit)
                    if (!viewModel.isEditing) {
                        Text(
                            text = viewModel.nama.ifEmpty { "Pengguna Baru" },
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E293B)
                        )
                        Text(
                            text = currentUser?.email ?: "email@contoh.com",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }
                }
            }

            // --- 2. FORM DATA DIRI ---
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth()
            ) {
                // Tombol "Edit Profil" Kecil di Kanan (Jika tidak sedang edit)
                if (!viewModel.isEditing) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        FilledTonalButton(
                            onClick = { viewModel.isEditing = true },
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = Color.White,
                                contentColor = BluePrimary
                            ),
                            shape = RoundedCornerShape(12.dp),
                            elevation = ButtonDefaults.buttonElevation(2.dp)
                        ) {
                            Icon(Icons.Default.Edit, null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Edit Profil")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Kartu Putih Pembungkus Form
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(2.dp),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {

                        ProfileField(
                            label = "Nama Lengkap",
                            value = viewModel.nama,
                            onValueChange = { viewModel.nama = it },
                            isEditing = viewModel.isEditing,
                            icon = Icons.Default.Badge // Icon ID Card
                        )

                        ProfileField(
                            label = "Nomor Telepon",
                            value = viewModel.phone,
                            onValueChange = { viewModel.phone = it },
                            isEditing = viewModel.isEditing,
                            icon = Icons.Default.Phone
                        )

                        ProfileField(
                            label = "Alamat Pengiriman",
                            value = viewModel.alamat,
                            onValueChange = { viewModel.alamat = it },
                            isEditing = viewModel.isEditing,
                            icon = Icons.Default.LocationOn,
                            isMultiLine = true
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // --- 3. TOMBOL AKSI (SIMPAN / LOGOUT) ---
                if (viewModel.isEditing) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Tombol Batal
                        OutlinedButton(
                            onClick = { viewModel.cancelEdit() },
                            modifier = Modifier.weight(1f).height(50.dp),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, Color.Gray)
                        ) {
                            Text("Batal", color = Color.Gray)
                        }

                        // Tombol Simpan
                        Button(
                            onClick = { viewModel.saveProfile() },
                            modifier = Modifier.weight(1f).height(50.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = BluePrimary),
                            enabled = !viewModel.isLoading
                        ) {
                            if (viewModel.isLoading) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                            } else {
                                Text("Simpan", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                } else {
                    // Tombol LOGOUT Besar
                    Button(
                        onClick = onLogout,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFEE2E2), // Merah Muda soft
                            contentColor = Color(0xFFDC2626)    // Merah Teks
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp)
                            .shadow(0.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Icon(Icons.Outlined.Logout, contentDescription = null)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Keluar Aplikasi", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(40.dp)) // Ruang ekstra bawah
            }
        }
    }
}

// --- KOMPONEN CUSTOM FIELD ---
@Composable
fun ProfileField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    isEditing: Boolean,
    icon: ImageVector,
    isMultiLine: Boolean = false
) {
    Column(modifier = Modifier.padding(bottom = 20.dp)) {
        if (isEditing) {
            // Mode Edit: Tampilkan TextField Modern
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                label = { Text(label) },
                leadingIcon = { Icon(icon, null, tint = BluePrimary) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                maxLines = if (isMultiLine) 4 else 1,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = BluePrimary,
                    unfocusedBorderColor = Color.LightGray
                )
            )
        } else {
            // Mode Baca: Tampilan List Rapi
            Row(
                verticalAlignment = Alignment.Top
            ) {
                // Icon Bulat Kecil
                Surface(
                    shape = CircleShape,
                    color = BluePrimary.copy(alpha = 0.1f),
                    modifier = Modifier.size(40.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(icon, null, tint = BluePrimary, modifier = Modifier.size(20.dp))
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = label,
                        fontSize = 12.sp,
                        color = Color.Gray,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (value.isNotEmpty()) value else "-",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF1E293B),
                        lineHeight = 22.sp
                    )
                }
            }
            // Garis pembatas tipis
            Divider(
                color = Color.Gray.copy(alpha = 0.1f),
                modifier = Modifier.padding(top = 16.dp, start = 56.dp) // Indent garis
            )
        }
    }
}