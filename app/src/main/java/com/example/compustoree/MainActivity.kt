package com.example.compustoree

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.compustoree.view.PengelolaHalaman // Pastikan ini di-import

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ✅ Aktifkan mode Full Screen (Edge-to-Edge)
        enableEdgeToEdge()

        setContent {
            // Panggil fungsi navigasi utama
            PengelolaHalaman()
        }
    }
}