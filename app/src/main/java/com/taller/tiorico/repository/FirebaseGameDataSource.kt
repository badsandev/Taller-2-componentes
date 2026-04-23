package com.taller.tiorico.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.FieldValue
import com.taller.tiorico.model.GameRoom
import com.taller.tiorico.model.Player
import com.taller.tiorico.model.RoomStatus

class FirebaseGameDataSource : GameDataSource {
    private val firestore = FirebaseFirestore.getInstance()
    private var gameListener: ListenerRegistration? = null
    private var roomsListener: ListenerRegistration? = null

    override fun createGame(userId: String, userName: String, roomName: String, onComplete: (String) -> Unit) {
        val roomsRef = firestore.collection("rooms").document()
        val generatedId = roomsRef.id

        val roomData = hashMapOf(
            "hostId" to userId,
            "hostName" to userName,
            "name" to roomName,
            "status" to RoomStatus.WAITING.name,
            "maxPlayers" to 4,
            "maxRounds" to 10,
            "currentRound" to 1,
            "players" to mapOf(userId to mapOf(
                "id" to userId,
                "name" to userName,
                "balance" to 1000.0,
                "eliminated" to false,
                "currentRound" to 1,
                "transactions" to emptyList<Any>()
            )),
            "createdAt" to FieldValue.serverTimestamp()
        )

        roomsRef.set(roomData)
            .addOnSuccessListener {
                Log.d("FirebaseGameDataSource", "Sala creada con éxito: $generatedId")
                onComplete(generatedId)
            }
            .addOnFailureListener { e ->
                Log.e("FirebaseGameDataSource", "Error al crear sala", e)
            }
    }

    override fun joinGame(roomId: String, userId: String, userName: String, onComplete: () -> Unit) {
        if (roomId.isEmpty()) return
        val roomRef = firestore.collection("rooms").document(roomId)

        val newPlayerData = hashMapOf(
            "id" to userId,
            "name" to userName,
            "balance" to 1000.0,
            "eliminated" to false,
            "currentRound" to 1,
            "transactions" to emptyList<Any>()
        )

        roomRef.update("players.$userId", newPlayerData)
            .addOnSuccessListener { onComplete() }
    }

    override fun listenGame(roomId: String, onUpdate: (GameRoom?) -> Unit) {
        if (roomId.isEmpty()) return
        gameListener?.remove()
        gameListener = firestore.collection("rooms").document(roomId)
            .addSnapshotListener { snapshot, e ->
                if (snapshot != null && snapshot.exists()) {
                    onUpdate(snapshot.toObject(GameRoom::class.java))
                }
            }
    }

    override fun listenAvailableGames(onUpdate: (List<GameRoom>) -> Unit) {
        roomsListener?.remove()
        roomsListener = firestore.collection("rooms")
            .whereEqualTo("status", RoomStatus.WAITING.name)
            .addSnapshotListener { snapshot, _ ->
                val rooms = snapshot?.toObjects(GameRoom::class.java) ?: emptyList()
                onUpdate(rooms)
            }
    }

    override fun startGame(roomId: String) {
        if (roomId.isEmpty()) return
        firestore.collection("rooms").document(roomId).update("status", RoomStatus.STARTED.name)
    }

    override fun finishGame(roomId: String) {
        if (roomId.isEmpty()) return
        firestore.collection("rooms").document(roomId).update("status", RoomStatus.FINISHED.name)
    }

    override fun updatePlayerState(roomId: String, userId: String, player: Player) {
        if (roomId.isEmpty()) return
        firestore.collection("rooms").document(roomId).update("players.$userId", player)
    }

    override fun removeListener() {
        gameListener?.remove()
        roomsListener?.remove()
    }
}
