package com.taller.tiorico.repository

import com.taller.tiorico.model.ChatMessage

interface ChatDataSource {
    fun sendMessage(roomId: String, senderId: String, senderName: String, text: String)
    fun listenMessages(roomId: String, onUpdate: (List<ChatMessage>) -> Unit)
    fun removeListener()
}
