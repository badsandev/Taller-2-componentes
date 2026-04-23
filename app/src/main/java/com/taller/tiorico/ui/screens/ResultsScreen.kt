package com.taller.tiorico.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.taller.tiorico.model.GameRoom
import com.taller.tiorico.ui.components.RicoCard
import com.taller.tiorico.ui.components.SectionHeader
import com.taller.tiorico.ui.theme.GoldPrimary
import java.text.NumberFormat
import java.util.*

@Composable
fun ResultsScreen(
    room: GameRoom?,
    onBackToLobby: () -> Unit
) {
    val players = room?.players?.values?.toList()?.sortedByDescending { it.balance } ?: emptyList()
    val winner = players.firstOrNull()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        Icon(
            Icons.Default.EmojiEvents,
            contentDescription = null,
            modifier = Modifier.size(100.dp),
            tint = GoldPrimary
        )

        Text(
            text = "¡Fin de la Partida!",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Black
        )

        Text(
            text = "Ranking de Magnates",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
        )

        Spacer(modifier = Modifier.height(32.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            itemsIndexed(players) { index, player ->
                val isWinner = index == 0
                RicoCard(
                    modifier = if (isWinner) Modifier.padding(bottom = 8.dp) else Modifier
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "${index + 1}.",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = if (isWinner) GoldPrimary else MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = player.name,
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                if (player.eliminated) {
                                    Text(
                                        text = "Bancarrota",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color(0xFFE74C3C)
                                    )
                                }
                            }
                        }
                        Text(
                            text = NumberFormat.getCurrencyInstance(Locale.US).format(player.balance),
                            fontWeight = FontWeight.Black,
                            color = if (isWinner) Color(0xFF2ECC71) else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }

        Button(
            onClick = onBackToLobby,
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.large
        ) {
            Text("Volver al Lobby", fontWeight = FontWeight.Bold)
        }
    }
}
