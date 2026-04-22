package com.taller.tiorico.model

data class Player(
    val id: String = "",
    val name: String = "",
    val balance: Double = 1000.0,
    val eliminated: Boolean = false,
    val currentRound: Int = 1,
    val transactions: List<Transaction> = emptyList(),
    val finished: Boolean = false // Nuevo campo para saber si el jugador ya terminó sus 10 rondas o perdió
)
