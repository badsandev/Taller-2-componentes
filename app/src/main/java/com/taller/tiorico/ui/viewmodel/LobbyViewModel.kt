package com.taller.tiorico.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.taller.tiorico.model.GameRoom
import com.taller.tiorico.model.Player
import com.taller.tiorico.model.RoomStatus
import com.taller.tiorico.repository.FirebaseGameDataSource
import com.taller.tiorico.repository.GameDataSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LobbyViewModel(
    private val dataSource: GameDataSource = FirebaseGameDataSource()
) : ViewModel() {
    private val auth = FirebaseAuth.getInstance()

    private val _availableRooms = MutableStateFlow<List<GameRoom>>(emptyList())
    val availableRooms: StateFlow<List<GameRoom>> = _availableRooms.asStateFlow()

    private val _currentRoom = MutableStateFlow<GameRoom?>(null)
    val currentRoom: StateFlow<GameRoom?> = _currentRoom.asStateFlow()

    private val _lobbyError = MutableStateFlow<String?>(null)
    val lobbyError: StateFlow<String?> = _lobbyError.asStateFlow()

    init {
        dataSource.listenAvailableGames { rooms ->
            _availableRooms.value = rooms
        }

        // Observador para finalizar el juego cuando todos terminen
        viewModelScope.launch {
            currentRoom.collect { room ->
                if (room != null && room.status == RoomStatus.STARTED.name) {
                    val allFinished = room.players.values.isNotEmpty() && room.players.values.all { it.finished }
                    if (allFinished) {
                        finishGame(room.docId)
                    }
                }
            }
        }
    }

    fun createRoom(name: String) {
        val userId = auth.currentUser?.uid ?: return
        val userName = auth.currentUser?.email?.substringBefore("@") ?: "Jugador"

        dataSource.createGame(userId, userName, name) { roomId ->
            listenToRoom(roomId)
        }
    }

    fun joinRoom(roomId: String) {
        val userId = auth.currentUser?.uid ?: return
        val userName = auth.currentUser?.email?.substringBefore("@") ?: "Jugador"

        dataSource.joinGame(roomId, userId, userName) {
            listenToRoom(roomId)
        }
    }

    fun listenToRoom(roomId: String) {
        if (roomId.isEmpty()) return
        dataSource.listenGame(roomId) { room ->
            _currentRoom.value = room
        }
    }

    fun updatePlayerState(roomId: String, player: Player) {
        val userId = auth.currentUser?.uid ?: return
        dataSource.updatePlayerState(roomId, userId, player)
    }

    fun startGame() {
        val roomId = _currentRoom.value?.docId ?: return
        if (roomId.isNotEmpty()) {
            dataSource.startGame(roomId)
        }
    }

    fun finishGame(roomId: String) {
        if (roomId.isNotEmpty()) {
            dataSource.finishGame(roomId)
        }
    }

    fun leaveRoom() {
        _currentRoom.value = null
    }

    override fun onCleared() {
        super.onCleared()
        dataSource.removeListener()
    }
}
