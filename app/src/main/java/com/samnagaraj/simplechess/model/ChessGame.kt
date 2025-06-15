package com.samnagaraj.simplechess.model

import androidx.compose.runtime.mutableStateListOf

enum class Player {
    WHITE, BLACK
}

enum class PieceType {
    PAWN, ROOK, KNIGHT, BISHOP, QUEEN, KING
}

data class Piece(val type: PieceType, val player: Player)

class ChessGame {
    // Board: 8x8 array, null if empty, or Piece object
    // 0,0 is a8 (top-left for black), 7,7 is h1 (bottom-right for white)
    val board: Array<Array<Piece?>> = Array(8) { arrayOfNulls<Piece?>(8) }
    var currentPlayer: Player = Player.WHITE
        private set // Only allow changing internally

    val whiteCapturedPieces = mutableStateListOf<Piece>()
    val blackCapturedPieces = mutableStateListOf<Piece>()
    val moveHistory = mutableStateListOf<String>()

    init {
        setupInitialBoard()
    }

    private fun setupInitialBoard() {
        // Black pieces
        board[0][0] = Piece(PieceType.ROOK, Player.BLACK)
        board[0][1] = Piece(PieceType.KNIGHT, Player.BLACK)
        board[0][2] = Piece(PieceType.BISHOP, Player.BLACK)
        board[0][3] = Piece(PieceType.QUEEN, Player.BLACK)
        board[0][4] = Piece(PieceType.KING, Player.BLACK)
        board[0][5] = Piece(PieceType.BISHOP, Player.BLACK)
        board[0][6] = Piece(PieceType.KNIGHT, Player.BLACK)
        board[0][7] = Piece(PieceType.ROOK, Player.BLACK)
        for (col in 0..7) {
            board[1][col] = Piece(PieceType.PAWN, Player.BLACK)
        }

        // White pieces
        board[7][0] = Piece(PieceType.ROOK, Player.WHITE)
        board[7][1] = Piece(PieceType.KNIGHT, Player.WHITE)
        board[7][2] = Piece(PieceType.BISHOP, Player.WHITE)
        board[7][3] = Piece(PieceType.QUEEN, Player.WHITE)
        board[7][4] = Piece(PieceType.KING, Player.WHITE)
        board[7][5] = Piece(PieceType.BISHOP, Player.WHITE)
        board[7][6] = Piece(PieceType.KNIGHT, Player.WHITE)
        board[7][7] = Piece(PieceType.ROOK, Player.WHITE)
        for (col in 0..7) {
            board[6][col] = Piece(PieceType.PAWN, Player.WHITE)
        }
    }

    fun getPieceAt(row: Int, col: Int): Piece? {
        if (row < 0 || row > 7 || col < 0 || col > 7) {
            return null
        }
        return board[row][col]
    }

    fun movePiece(fromRow: Int, fromCol: Int, toRow: Int, toCol: Int): Boolean {
        // Basic validation
        if (fromRow !in 0..7 || fromCol !in 0..7 || toRow !in 0..7 || toCol !in 0..7) {
            println("Move out of bounds")
            return false // Move out of bounds
        }

        val pieceToMove = board[fromRow][fromCol]
        if (pieceToMove == null) {
            println("No piece at from square")
            return false // No piece at the source square
        }

        if (pieceToMove.player != currentPlayer) {
            println("Not current player's piece")
            return false // Not current player's piece
        }

        val destinationPiece = board[toRow][toCol]
        if (destinationPiece != null && destinationPiece.player == currentPlayer) {
            println("Cannot capture own piece")
            return false // Cannot capture own piece
        }

        // --- More specific piece movement rules would go here ---
        // For now, any move to an empty square or opponent's square is "legal"
        // if it's the current player's piece.

        // Capture logic
        destinationPiece?.let {
            if (it.player == Player.WHITE) {
                blackCapturedPieces.add(it)
            } else {
                whiteCapturedPieces.add(it)
            }
        }

        // Update board state
        board[toRow][toCol] = pieceToMove
        board[fromRow][fromCol] = null

        // Record move history
        val moveString = formatMove(pieceToMove, fromRow, fromCol, toRow, toCol, destinationPiece != null)
        moveHistory.add(moveString)

        // Switch current player
        currentPlayer = if (currentPlayer == Player.WHITE) Player.BLACK else Player.WHITE

        return true
    }

    private fun formatMove(piece: Piece, fromRow: Int, fromCol: Int, toRow: Int, toCol: Int, isCapture: Boolean): String {
        val fromAlg = "${'a' + fromCol}${8 - fromRow}"
        val toAlg = "${'a' + toCol}${8 - toRow}"
        val pieceChar = when (piece.type) {
            PieceType.PAWN -> "P"
            PieceType.ROOK -> "R"
            PieceType.KNIGHT -> "N"
            PieceType.BISHOP -> "B"
            PieceType.QUEEN -> "Q"
            PieceType.KING -> "K"
        }
        val captureChar = if (isCapture) "x" else "-"
        return "$pieceChar $fromAlg$captureChar$toAlg"
    }


    fun getValidMoves(row: Int, col: Int): List<Pair<Int, Int>> {
        val piece = getPieceAt(row, col) ?: return emptyList()
        if (piece.player != currentPlayer) return emptyList()

        val moves = mutableListOf<Pair<Int, Int>>()

        when (piece.type) {
            PieceType.PAWN -> addPawnMoves(row, col, piece.player, moves)
            PieceType.ROOK -> addSlidingMoves(row, col, piece.player, listOf(0 to 1, 0 to -1, 1 to 0, -1 to 0), moves)
            PieceType.KNIGHT -> addKnightMoves(row, col, piece.player, moves)
            PieceType.BISHOP -> addSlidingMoves(row, col, piece.player, listOf(1 to 1, 1 to -1, -1 to 1, -1 to -1), moves)
            PieceType.QUEEN -> addSlidingMoves(row, col, piece.player, listOf(0 to 1, 0 to -1, 1 to 0, -1 to 0, 1 to 1, 1 to -1, -1 to 1, -1 to -1), moves)
            PieceType.KING -> addKingMoves(row, col, piece.player, moves)
        }
        return moves
    }

    private fun isValid(row: Int, col: Int): Boolean = row in 0..7 && col in 0..7

    private fun addPawnMoves(row: Int, col: Int, player: Player, moves: MutableList<Pair<Int, Int>>) {
        val direction = if (player == Player.WHITE) -1 else 1 // White moves from row 6 to 0, Black from 1 to 7
        val startRow = if (player == Player.WHITE) 6 else 1

        // 1. One step forward
        val oneStepRow = row + direction
        if (isValid(oneStepRow, col) && getPieceAt(oneStepRow, col) == null) {
            moves.add(oneStepRow to col)
            // 2. Two steps forward (if one step was possible and on starting row)
            if (row == startRow) {
                val twoStepsRow = row + 2 * direction
                if (isValid(twoStepsRow, col) && getPieceAt(twoStepsRow, col) == null) {
                    moves.add(twoStepsRow to col)
                }
            }
        }

        // 3. Diagonal captures
        val captureCols = listOf(col - 1, col + 1)
        for (captureCol in captureCols) {
            if (isValid(oneStepRow, captureCol)) {
                getPieceAt(oneStepRow, captureCol)?.let { targetPiece ->
                    if (targetPiece.player != player) {
                        moves.add(oneStepRow to captureCol)
                    }
                }
            }
        }
    }

    private fun addSlidingMoves(row: Int, col: Int, player: Player, directions: List<Pair<Int, Int>>, moves: MutableList<Pair<Int, Int>>) {
        for ((dr, dc) in directions) {
            for (i in 1..7) {
                val nextRow = row + i * dr
                val nextCol = col + i * dc
                if (!isValid(nextRow, nextCol)) break // Off board

                val targetPiece = getPieceAt(nextRow, nextCol)
                if (targetPiece == null) {
                    moves.add(nextRow to nextCol)
                } else {
                    if (targetPiece.player != player) {
                        moves.add(nextRow to nextCol) // Capture
                    }
                    break // Blocked by friendly or enemy piece
                }
            }
        }
    }

    private fun addKnightMoves(row: Int, col: Int, player: Player, moves: MutableList<Pair<Int, Int>>) {
        val knightMoves = listOf(
            -2 to -1, -2 to 1, -1 to -2, -1 to 2,
            1 to -2, 1 to 2, 2 to -1, 2 to 1
        )
        for ((dr, dc) in knightMoves) {
            val nextRow = row + dr
            val nextCol = col + dc
            if (isValid(nextRow, nextCol)) {
                val targetPiece = getPieceAt(nextRow, nextCol)
                if (targetPiece == null || targetPiece.player != player) {
                    moves.add(nextRow to nextCol)
                }
            }
        }
    }

    private fun addKingMoves(row: Int, col: Int, player: Player, moves: MutableList<Pair<Int, Int>>) {
        val kingMoves = listOf(
            -1 to -1, -1 to 0, -1 to 1,
            0 to -1,           0 to 1,
            1 to -1, 1 to 0, 1 to 1
        )
        for ((dr, dc) in kingMoves) {
            val nextRow = row + dr
            val nextCol = col + dc
            if (isValid(nextRow, nextCol)) {
                val targetPiece = getPieceAt(nextRow, nextCol)
                if (targetPiece == null || targetPiece.player != player) {
                    moves.add(nextRow to nextCol)
                }
            }
        }
    }


    // Optional: For debugging or displaying board state
    override fun toString(): String {
        val sb = StringBuilder()
        for (r in 0..7) {
            for (c in 0..7) {
                val piece = board[r][c]
                sb.append(
                    when (piece?.type) {
                        PieceType.PAWN -> if (piece.player == Player.WHITE) "P" else "p"
                        PieceType.ROOK -> if (piece.player == Player.WHITE) "R" else "r"
                        PieceType.KNIGHT -> if (piece.player == Player.WHITE) "N" else "n"
                        PieceType.BISHOP -> if (piece.player == Player.WHITE) "B" else "b"
                        PieceType.QUEEN -> if (piece.player == Player.WHITE) "Q" else "q"
                        PieceType.KING -> if (piece.player == Player.WHITE) "K" else "k"
                        null -> "."
                    }
                )
                sb.append(" ")
            }
            sb.append("\n")
        }
        return sb.toString()
    }
}
