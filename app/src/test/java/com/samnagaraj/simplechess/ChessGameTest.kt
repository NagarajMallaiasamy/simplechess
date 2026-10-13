package com.samnagaraj.simplechess

import org.junit.Test
import org.junit.Assert.*

class ChessGameTest {
    @Test
    fun testPawnMove() {
        val game = ChessGame()
        // White pawn moves 1 step
        assertTrue(game.movePiece(6, 0, 5, 0))
        // Black pawn moves 2 steps
        assertTrue(game.movePiece(1, 0, 3, 0))
    }

    @Test
    fun testKnightMove() {
        val game = ChessGame()
        assertTrue(game.movePiece(7, 1, 5, 2)) // White knight
        assertTrue(game.movePiece(0, 1, 2, 2)) // Black knight
    }

    @Test
    fun testInvalidMove() {
        val game = ChessGame()
        assertFalse(game.movePiece(7, 0, 5, 0)) // Rook blocked by pawn
        assertFalse(game.movePiece(6, 0, 4, 1)) // Pawn invalid diagonal
    }

    @Test
    fun testFoolsMate() {
        val game = ChessGame()
        assertTrue(game.movePiece(6, 5, 5, 5)) // f3
        assertTrue(game.movePiece(1, 4, 3, 4)) // e5
        assertTrue(game.movePiece(6, 6, 4, 6)) // g4
        assertTrue(game.movePiece(0, 3, 4, 7)) // Qh4#

        assertEquals(GameState.CHECKMATE, game.gameState)
    }
}
