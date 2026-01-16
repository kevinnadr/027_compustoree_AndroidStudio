package com.example.compustoree.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.compustoree.model.RiwayatOrder
import com.example.compustoree.model.UserSession
import com.example.compustoree.service.RetrofitClient
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.IOException

sealed interface RiwayatUiState {
    data class Success(val orders: List<RiwayatOrder>) : RiwayatUiState
    object Error : RiwayatUiState
    object Loading : RiwayatUiState
}

class RiwayatViewModel : ViewModel() {

    var uiState: RiwayatUiState by mutableStateOf(RiwayatUiState.Loading)
        private set

    // Cek apakah Admin?
    val isAdmin = UserSession.currentUser?.role == "admin"

    // Load data saat pertama kali
    fun loadData() {
        viewModelScope.launch {
            uiState = RiwayatUiState.Loading
            try {
                if (isAdmin) {
                    // Jika Admin, ambil SEMUA data (dari endpoint transactions)
                    val result = RetrofitClient.instance.getAllTransactions()
                    uiState = RiwayatUiState.Success(result)
                } else {
                    // Jika User, ambil data dia sendiri (dari endpoint orders?email=...)
                    val email = UserSession.currentUser?.email
                    val result = RetrofitClient.instance.getRiwayat(email)
                    uiState = RiwayatUiState.Success(result)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                uiState = RiwayatUiState.Error
            }
        }
    }

    // Fungsi Update Status (FIXED NAME)
    fun updateStatus(id: Int, status: String) {
        viewModelScope.launch {
            try {
                // Panggil fungsi yang benar: updateStatusOrder
                RetrofitClient.instance.updateStatusOrder(id, mapOf("status" to status))
                loadData() // Refresh list
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // Fungsi Hapus Transaksi (FIXED NAME)
    fun deleteOrder(id: Int) {
        viewModelScope.launch {
            try {
                // Panggil fungsi yang benar: deleteTransaction
                RetrofitClient.instance.deleteTransaction(id)
                loadData() // Refresh list
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // Alias untuk compatibility (jika ada file lain yang memanggil)
    fun checkRoleAndLoad() {
        loadData()
    }
}