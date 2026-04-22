package com.taller.tiorico.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.taller.tiorico.model.GameRoom
import com.taller.tiorico.ui.components.RicoCard
import com.taller.tiorico.ui.components.SectionHeader
import com.taller.tiorico.ui.viewmodel.LobbyViewModel

@Composable
fun LobbyScreen(
    onRoomJoined: (String) -> Unit,
    viewModel: LobbyViewModel = viewModel()
) {
    val rooms by viewModel.availableRooms.collectAsState()
    val currentRoom by viewModel.currentRoom.collectAsState()
    var showCreateDialog by remember { mutableStateOf(false) }
    var roomName by remember { mutableStateOf("") }

    if (currentRoom != null) {
        WaitingRoomScreen(
            room = currentRoom!!,
            onStartGame = { viewModel.startGame() },
            onRoomStarted = { onRoomJoined(currentRoom!!.docId) }
        )
    } else {
        Scaffold(
            floatingActionButton = {
                FloatingActionButton(onClick = { showCreateDialog = true }) {
                    Icon(Icons.Default.Add, contentDescription = "Crear Sala")
                }
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 20.dp)
            ) {
                SectionHeader("Salas Disponibles")
                
                if (rooms.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No hay salas activas. ¡Crea una!", color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f))
                    }
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(rooms) { room ->
                            RoomItem(room, onJoin = { viewModel.joinRoom(room.docId) })
                        }
                    }
                }
            }
        }
    }

    if (showCreateDialog) {
        AlertDialog(
            onDismissRequest = { showCreateDialog = false },
            title = { Text("Nueva Sala") },
            text = {
                OutlinedTextField(
                    value = roomName,
                    onValueChange = { roomName = it },
                    label = { Text("Nombre de la sala") }
                )
            },
            confirmButton = {
                Button(onClick = {
                    if (roomName.isNotBlank()) {
                        viewModel.createRoom(roomName)
                        showCreateDialog = false
                    }
                }) { Text("Crear") }
            }
        )
    }
}

@Composable
fun RoomItem(room: GameRoom, onJoin: () -> Unit) {
    RicoCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = room.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                Text(text = "Host: ${room.hostName}", style = MaterialTheme.typography.bodySmall)
            }
            Button(onClick = onJoin) {
                Text("Unirse (${room.players.size}/${room.maxPlayers})")
            }
        }
    }
}

@Composable
fun WaitingRoomScreen(
    room: GameRoom,
    onStartGame: () -> Unit,
    onRoomStarted: () -> Unit
) {
    LaunchedEffect(room.status) {
        if (room.status == "STARTED") {
            onRoomStarted()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SectionHeader("Sala: ${room.name}")
        Text(text = "Esperando jugadores...", style = MaterialTheme.typography.bodyMedium)
        
        Spacer(modifier = Modifier.height(24.dp))
        
        RicoCard {
            Text(text = "Jugadores conectados:", fontWeight = FontWeight.Bold)
            room.players.values.forEach { player ->
                Text(text = "• ${player.name}", modifier = Modifier.padding(vertical = 4.dp))
            }
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        Button(
            onClick = onStartGame,
            modifier = Modifier.fillMaxWidth(),
            enabled = room.players.size >= 1
        ) {
            Text("Iniciar Juego")
        }
    }
}
