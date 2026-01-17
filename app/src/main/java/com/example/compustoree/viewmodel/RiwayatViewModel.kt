package com.example.compustoree.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.compustoree.model.RiwayatOrder
import com.example.compustoree.model.UserSession
import com.example.compustoree.service.RetrofitClient
import kotlinx.coroutines.launch

// State Pattern UI
sealed interface RiwayatUiState {
    data class Success(val orders: List<RiwayatOrder>) : RiwayatUiState
    object Error : RiwayatUiState
    object Loading : RiwayatUiState
}

class RiwayatViewModel : ViewModel() {

    var uiState: RiwayatUiState by mutableStateOf(RiwayatUiState.Loading)
        private set

    val isAdmin = UserSession.currentUser?.role == "admin"

    fun loadData() {
        viewModelScope.launch {
            uiState = RiwayatUiState.Loading
            try {
                val result = if (isAdmin) {
                    RetrofitClient.instance.getAllTransactions()
                } else {
                    val email = UserSession.currentUser?.email
                    RetrofitClient.instance.getRiwayat(email)
                }
                uiState = RiwayatUiState.Success(result)
            } catch (e: Exception) {
                e.printStackTrace()
                uiState = RiwayatUiState.Error
            }
        }
    }

    fun updateStatus(idTransaksi: Int, statusBaru: String) {
        viewModelScope.launch {
            try {
                val body = mapOf("status" to statusBaru)
                RetrofitClient.instance.updateStatusOrder(idTransaksi, body)
                loadData() // Refresh UI
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun deleteOrder(idTransaksi: Int) {
        viewModelScope.launch {
            try {
                RetrofitClient.instance.deleteTransaction(idTransaksi)
                loadData() // Refresh UI
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}