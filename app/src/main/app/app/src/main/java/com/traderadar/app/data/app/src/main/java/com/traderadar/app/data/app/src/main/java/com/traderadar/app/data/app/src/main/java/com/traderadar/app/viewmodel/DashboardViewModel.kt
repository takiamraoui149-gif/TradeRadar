package com.traderadar.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.traderadar.app.data.CoinPrice
import com.traderadar.app.data.NetworkModule
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class DashboardUiState {
    object Loading : DashboardUiState()
    data class Success(val coins: List<CoinPrice>) : DashboardUiState()
    data class Error(val message: String) : DashboardUiState()
}

class DashboardViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<DashboardUiState>(DashboardUiState.Loading)
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    private val refreshIntervalMillis = 30_000L

    init {
        startAutoRefresh()
    }

    private fun startAutoRefresh() {
        viewModelScope.launch {
            while (true) {
                fetchCoins()
                delay(refreshIntervalMillis)
            }
        }
    }

    private suspend fun fetchCoins() {
        try {
            val coins = NetworkModule.coinGeckoApi.getTopCoins()
            _uiState.value = DashboardUiState.Success(coins)
        } catch (e: Exception) {
            _uiState.value = DashboardUiState.Error(
                e.localizedMessage ?: "حدث خطأ أثناء جلب البيانات"
            )
        }
    }

    fun refreshNow() {
        viewModelScope.launch {
            _uiState.value = DashboardUiState.Loading
            fetchCoins()
        }
    }
}
