package com.traderadar.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.traderadar.app.data.CoinPrice
import com.traderadar.app.viewmodel.DashboardUiState
import com.traderadar.app.viewmodel.DashboardViewModel
import java.text.NumberFormat
import java.util.Locale

class MainActivity : ComponentActivity() {

    private val viewModel: DashboardViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    DashboardScreen(viewModel)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(viewModel: DashboardViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Trade Radar 📡") },
                actions = {
                    IconButton(onClick = { viewModel.refreshNow() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "تحديث")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            when (val state = uiState) {
                is DashboardUiState.Loading -> LoadingView()
                is DashboardUiState.Error -> ErrorView(state.message) { viewModel.refreshNow() }
                is DashboardUiState.Success -> CoinList(state.coins)
            }
        }
    }
}

@Composable
fun LoadingView() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
fun ErrorView(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("⚠️ تعذر تحميل البيانات", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Text(message, fontSize = 14.sp, color = Color.Gray)
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onRetry) { Text("إعادة المحاولة") }
    }
}

@Composable
fun CoinList(coins: List<CoinPrice>) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(coins, key = { it.id }) { coin ->
            CoinRow(coin)
            Divider()
        }
    }
}

@Composable
fun CoinRow(coin: CoinPrice) {
    val currencyFormat = remember { NumberFormat.getCurrencyInstance(Locale.US) }
    val changePercent = coin.priceChangePercentage24h ?: 0.0
    val isPositive = changePercent >= 0

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(coin.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text(coin.symbol.uppercase(), fontSize = 13.sp, color = Color.Gray)
        }

        Column(horizontalAlignment = Alignment.End) {
            Text(currencyFormat.format(coin.currentPrice), fontWeight = FontWeight.Medium)
            Text(
                text = "${if (isPositive) "▲" else "▼"} ${"%.2f".format(changePercent)}%",
                color = if (isPositive) Color(0xFF16A34A) else Color(0xFFDC2626),
                fontSize = 13.sp
            )
        }
    }
}
