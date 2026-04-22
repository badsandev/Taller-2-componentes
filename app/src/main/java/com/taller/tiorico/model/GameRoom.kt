package com.taller.tiorico.model

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

enum class RoomStatus {
    WAITING, STARTED, FINISHED
}

data class GameRoom(
    @DocumentId val docId: String = "", // ID automático de Firebase
    val hostId: String = "",
    val hostName: String = "",
    val name: String = "",
    val maxPlayers: Int = 4,
    val maxRounds: Int = 10,
    val currentRound: Int = 0,
    val status: String = RoomStatus.WAITING.name,
    val players: Map<String, Player> = emptyMap(),
    @ServerTimestamp val createdAt: Date? = null
)
