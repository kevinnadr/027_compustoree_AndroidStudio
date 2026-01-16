package com.example.compustoree

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.compustoree.view.AppNavigation // Import file navigasi yang baru kita buat

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Panggil Navigasi Utama di sini
            AppNavigation()
        }
    }
}