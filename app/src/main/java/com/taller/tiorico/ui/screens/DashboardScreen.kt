package com.taller.tiorico.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.taller.tiorico.model.Transaction
import com.taller.tiorico.model.TransactionType
import com.taller.tiorico.ui.components.ActionButton
import com.taller.tiorico.ui.components.RicoCard
import com.taller.tiorico.ui.components.SectionHeader
import com.taller.tiorico.ui.theme.GoldDark
import com.taller.tiorico.ui.viewmodel.ChatViewModel
import com.taller.tiorico.ui.viewmodel.GameViewModel
import com.taller.tiorico.ui.viewmodel.LobbyViewModel
import java.text.NumberFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    gameViewModel: GameViewModel,
    lobbyViewModel: LobbyViewModel
) {
    val player by gameViewModel.playerState.collectAsState()
    val gameMessage by gameViewModel.gameMessage.collectAsState()
    val currentRoom by lobbyViewModel.currentRoom.collectAsState()

    val chatViewModel: ChatViewModel = viewModel()
    var showChat by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // Sincronizar el estado del jugador con la sala en Firebase
    LaunchedEffect(player) {
        currentRoom?.docId?.let { roomId ->
            lobbyViewModel.updatePlayerState(roomId, player)
        }
    }

    Scaffold(
        floatingActionButton = {
            if (player.eliminated || player.finished) {
                FloatingActionButton(
                    onClick = {
                        gameViewModel.resetGame()
                    },
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = "Reiniciar")
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(24.dp))
                HeaderSection(
                    name = player.name,
                    round = player.currentRound,
                    onChatClick = { showChat = true }
                )
            }

            item {
                BalanceCard(player.balance)
            }

            if (gameMessage.isNotEmpty()) {
                item {
                    GameStatusCard(gameMessage)
                }
            }

            if (!player.eliminated && !player.finished) {
                item {
                    ActionsSection(gameViewModel)
                }
            }

            item {
                SectionHeader("Historial de Movimientos")
            }

            items(player.transactions.reversed()) { transaction ->
                TransactionItem(transaction)
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }

        if (showChat) {
            ModalBottomSheet(
                onDismissRequest = { showChat = false },
                sheetState = sheetState,
                containerColor = MaterialTheme.colorScheme.background,
                dragHandle = { BottomSheetDefaults.DragHandle() }
            ) {
                ChatScreen(
                    roomId = currentRoom?.docId ?: "",
                    viewModel = chatViewModel
                )
            }
        }
    }
}

@Composable
fun HeaderSection(name: String, round: Int, onChatClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Hola, $name",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Bienvenido de nuevo",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
            )
        }

        IconButton(onClick = onChatClick) {
            BadgedBox(
                badge = {
                    // Aquí podrías poner un punto si hay mensajes nuevos
                }
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Chat,
                    contentDescription = "Chat",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        Surface(
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
            shape = MaterialTheme.shapes.medium
        ) {
            Text(
                text = "Ronda ${if (round > 10) 10 else round}/10",
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun BalanceCard(balance: Double) {
    val format = NumberFormat.getCurrencyInstance(Locale.US)
    RicoCard {
        Text(
            text = "Balance Total",
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            style = MaterialTheme.typography.labelLarge
        )
        Text(
            text = format.format(balance),
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Black,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(12.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                Icons.Default.PlayArrow,
                contentDescription = null,
                tint = Color(0xFF2ECC71),
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = " Salud financiera estable",
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFF2ECC71)
            )
        }
    }
}

@Composable
fun ActionsSection(viewModel: GameViewModel) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            ActionButton(
                text = "Ahorrar",
                icon = Icons.Default.Home,
                onClick = { viewModel.save() },
                modifier = Modifier.weight(1f),
                color = MaterialTheme.colorScheme.secondary
            )
            ActionButton(
                text = "Invertir",
                icon = Icons.Default.Star,
                onClick = { viewModel.invest() },
                modifier = Modifier.weight(1f),
                color = MaterialTheme.colorScheme.primary
            )
        }
        ActionButton(
            text = "Gastar",
            icon = Icons.Default.ShoppingCart,
            onClick = { viewModel.spend() },
            modifier = Modifier.fillMaxWidth(),
            color = Color(0xFFE74C3C)
        )
    }
}

@Composable
fun GameStatusCard(message: String) {
    Surface(
        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
        shape = MaterialTheme.shapes.large,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Info, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun TransactionItem(transaction: Transaction) {
    val icon: ImageVector
    val color: Color

    when (transaction.type) {
        TransactionType.SAVE -> {
            icon = Icons.Default.Add
            color = Color(0xFF2ECC71)
        }
        TransactionType.INVEST -> {
            if (transaction.amount > 0) {
                icon = Icons.Default.ThumbUp
                color = Color(0xFF2ECC71)
            } else {
                icon = Icons.Default.Warning
                color = Color(0xFFE74C3C)
            }
        }
        TransactionType.SPEND -> {
            icon = Icons.Default.Close
            color = Color(0xFFE74C3C)
        }
        TransactionType.EVENT -> {
            icon = Icons.Default.Favorite
            color = GoldDark
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = color.copy(alpha = 0.1f),
            modifier = Modifier.size(48.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(icon, contentDescription = null, tint = color)
            }
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = transaction.description, fontWeight = FontWeight.Bold)
            Text(
                text = java.text.SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date(transaction.timestamp)),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
            )
        }
        val format = NumberFormat.getCurrencyInstance(Locale.US)
        Text(
            text = "${if (transaction.amount > 0) "+" else ""}${format.format(transaction.amount)}",
            fontWeight = FontWeight.Black,
            color = if (transaction.amount >= 0) Color(0xFF2ECC71) else Color(0xFFE74C3C)
        )
    }
}
