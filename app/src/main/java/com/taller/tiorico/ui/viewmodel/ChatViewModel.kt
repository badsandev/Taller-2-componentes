package com.taller.tiorico.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.taller.tiorico.model.ChatMessage
import com.taller.tiorico.repository.ChatDataSource
import com.taller.tiorico.repository.FirebaseChatDataSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ChatViewModel(
    private val dataSource: ChatDataSource = FirebaseChatDataSource()
) : ViewModel() {

    private val auth = FirebaseAuth.getInstance()

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    /** UID del usuario actual, para distinguir mensajes propios de los ajenos. */
    val currentUserId: String
        get() = auth.currentUser?.uid ?: ""


    fun startListening(roomId: String) {
        dataSource.listenMessages(roomId) { msgs ->
            _messages.value = msgs
        }
    }


    fun sendMessage(roomId: String, text: String) {
        val uid  = auth.currentUser?.uid  ?: return
        val name = auth.currentUser?.email?.substringBefore("@") ?: "Jugador"
        dataSource.sendMessage(roomId, uid, name, text)
    }

    override fun onCleared() {
        super.onCleared()
        dataSource.removeListener()
    }
}
