package com.taller.tiorico

import com.taller.tiorico.model.GameRoom
import com.taller.tiorico.model.Player
import com.taller.tiorico.model.RoomStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun gameRoom_debe_iniciar_con_valores_por_defecto_correctos() {
        val room = GameRoom()

        assertEquals(4, room.maxPlayers)
        assertEquals(10, room.maxRounds)
        assertEquals(0, room.currentRound)
        assertEquals(RoomStatus.WAITING.name, room.status)
        assertTrue(room.players.isEmpty())
    }

    @Test
    fun player_debe_iniciar_con_balance_y_estado_correctos() {
        val player = Player(name = "Juan")

        assertEquals("Juan", player.name)
        assertEquals(1000.0, player.balance, 0.001)
        assertFalse(player.eliminated)
        assertEquals(1, player.currentRound)
        assertTrue(player.transactions.isEmpty())
        assertFalse(player.finished)
    }
}