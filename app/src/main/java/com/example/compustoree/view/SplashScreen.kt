package com.example.compustoree.view

import android.view.animation.OvershootInterpolator
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onTimeout: () -> Unit
) {
    // 1. STATE ANIMASI
    val scale = remember { Animatable(0f) }
    val textAlpha = remember { Animatable(0f) }

    // 2. JALANKAN ANIMASI & TIMER
    LaunchedEffect(Unit) {
        // Animasi Logo Meletup (Pop-up)
        scale.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = 800,
                easing = { OvershootInterpolator(2f).getInterpolation(it) }
            )
        )
        // Animasi Teks Muncul Perlahan
        textAlpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 800)
        )
        // Tunggu sebentar sebelum pindah
        delay(2000)
        onTimeout()
    }

    // Warna Tema
    val BluePrimary = Color(0xFF2563EB)
    val BlueDark = Color(0xFF0F172A) // Lebih gelap agar kontras

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                // Gradient Diagonal agar lebih dinamis
                brush = Brush.linearGradient(
                    colors = listOf(BluePrimary, BlueDark),
                    start = Offset(0f, 0f),
                    end = Offset(1000f, 1000f)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        // --- BACKGROUND ORNAMENTS (Lingkaran Hiasan) ---
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                color = Color.White.copy(alpha = 0.05f),
                radius = size.width * 0.4f,
                center = Offset(size.width * 0.8f, size.height * 0.15f)
            )
            drawCircle(
                color = Color.White.copy(alpha = 0.03f),
                radius = size.width * 0.6f,
                center = Offset(0f, size.height * 0.9f)
            )
        }

        // --- KONTEN UTAMA ---
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // LOGO DENGAN ANIMASI SCALE
            Box(
                modifier = Modifier
                    .scale(scale.value) // Terapkan animasi
                    .size(140.dp)
                    .background(Color.White.copy(alpha = 0.1f), CircleShape)
                    .padding(20.dp), // Padding agar icon tidak nempel pinggir
                contentAlignment = Alignment.Center
            ) {
                // Lingkaran dalam lebih solid
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Computer,
                        contentDescription = null,
                        tint = BluePrimary, // Icon warna biru
                        modifier = Modifier.size(60.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // TEKS DENGAN ANIMASI FADE IN
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.alpha(textAlpha.value) // Terapkan animasi
            ) {
                Text(
                    text = "CompuStore",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    letterSpacing = 2.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Build Your Dream PC",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color.White.copy(alpha = 0.7f),
                    letterSpacing = 1.sp
                )
            }
        }

        // --- LOADING BAR & FOOTER (DI BAWAH) ---
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 60.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Loading Bar Kecil
            LinearProgressIndicator(
                modifier = Modifier
                    .width(150.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(10.dp)),
                color = Color.White,
                trackColor = Color.White.copy(alpha = 0.2f),
                strokeCap = StrokeCap.Round
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "v1.0.0",
                color = Color.White.copy(alpha = 0.4f),
                fontSize = 12.sp
            )
        }
    }
}