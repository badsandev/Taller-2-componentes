package com.taller.tiorico.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.taller.tiorico.model.GameRoom
import com.taller.tiorico.model.Player
import com.taller.tiorico.model.RoomStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import com.google.firebase.firestore.snapshots

class GameRepository {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    // --- Player Logic ---
    suspend fun savePlayerState(player: Player) {
        val userId = auth.currentUser?.uid ?: return
        firestore.collection("players").document(userId).set(player).await()
    }

    suspend fun getPlayerState(): Player? {
        val userId = auth.currentUser?.uid ?: return null
        return try {
            firestore.collection("players").document(userId).get().await().toObject(Player::class.java)
        } catch (e: Exception) {
            null
        }
    }

    fun getCurrentUserId(): String? = auth.currentUser?.uid
    fun getCurrentUserName(): String? = auth.currentUser?.email?.substringBefore("@")

    // --- Room Logic ---
    suspend fun createRoom(roomName: String): String {
        val userId = auth.currentUser?.uid ?: throw Exception("User not logged in")
        val userName = getCurrentUserName() ?: "Player"
        
        val newRoom = GameRoom(
            hostId = userId,
            hostName = userName,
            name = roomName,
            status = RoomStatus.WAITING.name,
            players = mapOf(userId to Player(id = userId, name = userName))
        )
        
        val docRef = firestore.collection("rooms").add(newRoom).await()
        return docRef.id
    }

    suspend fun joinRoom(roomId: String) {
        val userId = auth.currentUser?.uid ?: throw Exception("User not logged in")
        val userName = getCurrentUserName() ?: "Player"
        
        val roomDoc = firestore.collection("rooms").document(roomId).get().await()
        val room = roomDoc.toObject(GameRoom::class.java) ?: throw Exception("Room not found")
        
        if (room.players.size >= room.maxPlayers) throw Exception("Room is full")
        
        val updatedPlayers = room.players.toMutableMap()
        updatedPlayers[userId] = Player(id = userId, name = userName)
        
        firestore.collection("rooms").document(roomId)
            .update("players", updatedPlayers).await()
    }

    fun listenToRoom(roomId: String): Flow<GameRoom?> {
        return firestore.collection("rooms").document(roomId)
            .snapshots()
            .map { snapshot -> snapshot.toObject(GameRoom::class.java) }
    }

    fun listenToAvailableRooms(): Flow<List<GameRoom>> {
        return firestore.collection("rooms")
            .whereEqualTo("status", RoomStatus.WAITING.name)
            .snapshots()
            .map { snapshot -> snapshot.toObjects(GameRoom::class.java) }
    }

    suspend fun startGame(roomId: String) {
        firestore.collection("rooms").document(roomId)
            .update("status", RoomStatus.STARTED.name).await()
    }

    suspend fun updatePlayerInRoom(roomId: String, player: Player) {
        val userId = auth.currentUser?.uid ?: return
        val roomDoc = firestore.collection("rooms").document(roomId).get().await()
        val room = roomDoc.toObject(GameRoom::class.java) ?: return
        
        val updatedPlayers = room.players.toMutableMap()
        updatedPlayers[userId] = player
        
        firestore.collection("rooms").document(roomId)
            .update("players", updatedPlayers).await()
    }
}
