package com.taller.tiorico.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.taller.tiorico.ui.components.SectionHeader
import com.taller.tiorico.ui.viewmodel.GameViewModel

@Composable
fun HistoryScreen(viewModel: GameViewModel) {
    val player by viewModel.playerState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
    ) {
        SectionHeader("Historial Detallado")
        
        if (player.transactions.isEmpty()) {
            Text(
                text = "Aún no hay movimientos en esta partida.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
            )
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(bottom = 20.dp)
            ) {
                items(player.transactions.reversed()) { transaction ->
                    TransactionItem(transaction)
                }
            }
        }
    }
}
