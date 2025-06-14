package com.samnagaraj.simplechess

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

        // Update board state
        board[toRow][toCol] = pieceToMove
        board[fromRow][fromCol] = null

        // Switch current player
        currentPlayer = if (currentPlayer == Player.WHITE) Player.BLACK else Player.WHITE

        return true
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
