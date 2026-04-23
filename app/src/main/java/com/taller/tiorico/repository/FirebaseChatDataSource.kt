package com.taller.tiorico.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.taller.tiorico.model.ChatMessage

class FirebaseChatDataSource : ChatDataSource {

    private val firestore = FirebaseFirestore.getInstance()
    private var messagesListener: ListenerRegistration? = null


    override fun sendMessage(
        roomId: String,
        senderId: String,
        senderName: String,
        text: String
    ) {
        if (roomId.isEmpty() || text.isBlank()) return

        val message = hashMapOf(
            "roomId"     to roomId,
            "senderId"   to senderId,
            "senderName" to senderName,
            "text"       to text.trim(),
            "timestamp"  to FieldValue.serverTimestamp()
        )

        firestore.collection("rooms")
            .document(roomId)
            .collection("chat")
            .add(message)
    }


    override fun listenMessages(
        roomId: String,
        onUpdate: (List<ChatMessage>) -> Unit
    ) {
        if (roomId.isEmpty()) return
        messagesListener?.remove()

        messagesListener = firestore.collection("rooms")
            .document(roomId)
            .collection("chat")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) return@addSnapshotListener
                val messages = snapshot.toObjects(ChatMessage::class.java)
                onUpdate(messages)
            }
    }

    override fun removeListener() {
        messagesListener?.remove()
    }
}
