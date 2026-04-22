package com.taller.tiorico.repository

import com.taller.tiorico.model.GameRoom

interface GameDataSource {
    fun createGame(userId: String, userName: String, roomName: String, onComplete: (String) -> Unit)
    fun joinGame(roomId: String, userId: String, userName: String, onComplete: () -> Unit)
    fun listenGame(roomId: String, onUpdate: (GameRoom?) -> Unit)
    fun listenAvailableGames(onUpdate: (List<GameRoom>) -> Unit)
    fun startGame(roomId: String)
    fun finishGame(roomId: String)
    fun updatePlayerState(roomId: String, userId: String, player: com.taller.tiorico.model.Player)
    fun removeListener()
}
