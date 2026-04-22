package com.taller.tiorico.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.taller.tiorico.model.Player
import com.taller.tiorico.model.Transaction
import com.taller.tiorico.model.TransactionType
import com.taller.tiorico.repository.GameRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.random.Random

class GameViewModel : ViewModel() {
    private val repository = GameRepository()

    private val _playerState = MutableStateFlow(Player(name = repository.getCurrentUserName() ?: "Tío Rico Player"))
    val playerState: StateFlow<Player> = _playerState.asStateFlow()

    private val _gameMessage = MutableStateFlow("")
    val gameMessage: StateFlow<String> = _gameMessage.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            repository.getPlayerState()?.let { savedPlayer ->
                // Aseguramos que el nombre sea el real del usuario si el guardado es genérico
                val currentName = repository.getCurrentUserName() ?: savedPlayer.name
                _playerState.value = savedPlayer.copy(name = currentName)
            }
        }
    }

    fun save() {
        val gain = 50.0
        val newTransaction = Transaction(
            type = TransactionType.SAVE,
            amount = gain,
            description = "Ahorro seguro (Ronda ${_playerState.value.currentRound})"
        )
        updateState(gain, newTransaction)
    }

    fun invest() {
        val isSuccess = Random.nextBoolean()
        val amount = if (isSuccess) 200.0 else -150.0
        val description = if (isSuccess) "Inversión exitosa" else "Inversión fallida"
        val newTransaction = Transaction(
            type = TransactionType.INVEST,
            amount = amount,
            description = "$description (Ronda ${_playerState.value.currentRound})"
        )
        updateState(amount, newTransaction)
    }

    fun spend() {
        val loss = -100.0
        val newTransaction = Transaction(
            type = TransactionType.SPEND,
            amount = loss,
            description = "Gasto necesario (Ronda ${_playerState.value.currentRound})"
        )
        updateState(loss, newTransaction)
    }

    private fun updateState(amountChange: Double, transaction: Transaction) {
        _playerState.update { current ->
            val newBalance = current.balance + amountChange
            val nextRound = current.currentRound + 1
            
            var eventChange = 0.0
            var eventTransaction: Transaction? = null
            if (Random.nextInt(100) < 20) {
                val eventAmount = if (Random.nextBoolean()) 100.0 else -80.0
                eventChange = eventAmount
                eventTransaction = Transaction(
                    type = TransactionType.EVENT,
                    amount = eventAmount,
                    description = "¡Evento aleatorio inesperado!"
                )
            }

            val finalBalance = newBalance + eventChange
            val finalTransactions = current.transactions + transaction + (eventTransaction?.let { listOf(it) } ?: emptyList())
            val isEliminated = finalBalance <= 0
            val isFinished = nextRound > 10 || isEliminated

            current.copy(
                balance = finalBalance,
                currentRound = if (nextRound > 10) 10 else nextRound,
                eliminated = isEliminated,
                transactions = finalTransactions,
                finished = isFinished
            )
        }
        
        // Persistir en Firebase
        viewModelScope.launch {
            repository.savePlayerState(_playerState.value)
        }

        if (_playerState.value.eliminated) {
            _gameMessage.value = "¡Has quedado en bancarrota!"
        } else if (_playerState.value.finished) {
            _gameMessage.value = "Has completado tus rondas. Esperando a los demás..."
        }
    }

    fun resetGame() {
        _playerState.value = Player(name = repository.getCurrentUserName() ?: "Tío Rico Player")
        _gameMessage.value = ""
        viewModelScope.launch {
            repository.savePlayerState(_playerState.value)
        }
    }
}
