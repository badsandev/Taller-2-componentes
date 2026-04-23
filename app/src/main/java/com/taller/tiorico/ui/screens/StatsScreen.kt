package com.taller.tiorico.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.taller.tiorico.model.TransactionType
import com.taller.tiorico.ui.components.RicoCard
import com.taller.tiorico.ui.components.SectionHeader
import com.taller.tiorico.ui.viewmodel.GameViewModel

@Composable
fun StatsScreen(viewModel: GameViewModel) {
    val player by viewModel.playerState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
    ) {
        SectionHeader("Estadísticas de Juego")

        val totalSaved = player.transactions.filter { it.type == TransactionType.SAVE }.sumOf { it.amount }
        val totalInvested = player.transactions.filter { it.type == TransactionType.INVEST }.sumOf { it.amount }
        val totalSpent = player.transactions.filter { it.type == TransactionType.SPEND }.sumOf { it.amount }.let { if (it < 0) -it else it }

        RicoCard {
            Text(text = "Distribución de Capital", fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))

            StatBar("Ahorros", totalSaved, Color(0xFF2ECC71))
            StatBar("Inversiones", totalInvested, MaterialTheme.colorScheme.primary)
            StatBar("Gastos", totalSpent, Color(0xFFE74C3C))
        }

        Spacer(modifier = Modifier.height(16.dp))

        RicoCard {
            Text(text = "Rendimiento por Ronda", fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(40.dp))

            // Simple visual representation of progress
            Row(
                modifier = Modifier.fillMaxWidth().height(100.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Bottom
            ) {
                val rounds = player.transactions.groupBy { it.description.contains("Ronda") }
                // Mockup bars for rounds
                repeat(5) { i ->
                    Box(
                        modifier = Modifier
                            .width(20.dp)
                            .height((40 + (i * 10)).dp)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.5f), RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                    )
                }
            }
            Text(
                text = "Progreso de las últimas 5 rondas",
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 8.dp)
            )
        }
    }
}

@Composable
fun StatBar(label: String, value: Double, color: Color) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = label, style = MaterialTheme.typography.bodyMedium)
            Text(text = "$$value", fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { (value / 2000f).toFloat().coerceIn(0.1f, 1f) },
            modifier = Modifier.fillMaxWidth().height(8.dp),
            color = color,
            trackColor = color.copy(alpha = 0.1f),
            strokeCap = StrokeCap.Round
        )
    }
}
