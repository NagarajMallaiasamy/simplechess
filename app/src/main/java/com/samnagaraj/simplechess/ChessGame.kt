package com.samnagaraj.simplechess

enum class Player {
    WHITE, BLACK
}

enum class PieceType {
    PAWN, ROOK, KNIGHT, BISHOP, QUEEN, KING
}

enum class GameState {
    IN_PROGRESS, CHECKMATE, STALEMATE
}

data class Piece(val type: PieceType, val player: Player)

class ChessGame {
    // Board: 8x8 array, null if empty, or Piece object
    // 0,0 is a8 (top-left for black), 7,7 is h1 (bottom-right for white)
    val board: Array<Array<Piece?>> = Array(8) { arrayOfNulls<Piece?>(8) }
    var currentPlayer: Player = Player.WHITE
        private set // Only allow changing internally
    var gameState: GameState = GameState.IN_PROGRESS
        private set

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

    fun isValidPieceMove(fromRow: Int, fromCol: Int, toRow: Int, toCol: Int): Boolean {
        val pieceToMove = board[fromRow][fromCol] ?: return false
        val rowDiff = Math.abs(toRow - fromRow)
        val colDiff = Math.abs(toCol - fromCol)

        return when (pieceToMove.type) {
            PieceType.PAWN -> {
                val direction = if (pieceToMove.player == Player.WHITE) -1 else 1
                val startRow = if (pieceToMove.player == Player.WHITE) 6 else 1

                // Move forward
                if (fromCol == toCol) {
                    // One step
                    if (toRow == fromRow + direction && board[toRow][toCol] == null) {
                        return true
                    }
                    // Two steps from start
                    if (fromRow == startRow && toRow == fromRow + 2 * direction &&
                        board[fromRow + direction][toCol] == null && board[toRow][toCol] == null) {
                        return true
                    }
                }
                // Capture diagonally
                else if (rowDiff == 1 && colDiff == 1 && toRow == fromRow + direction) {
                    if (board[toRow][toCol] != null && board[toRow][toCol]!!.player != pieceToMove.player) {
                        return true
                    }
                }
                false
            }
            PieceType.ROOK -> {
                (fromRow == toRow || fromCol == toCol) && isPathClear(fromRow, fromCol, toRow, toCol)
            }
            PieceType.KNIGHT -> {
                (rowDiff == 2 && colDiff == 1) || (rowDiff == 1 && colDiff == 2)
            }
            PieceType.BISHOP -> {
                rowDiff == colDiff && isPathClear(fromRow, fromCol, toRow, toCol)
            }
            PieceType.QUEEN -> {
                ((fromRow == toRow || fromCol == toCol) || (rowDiff == colDiff)) && isPathClear(fromRow, fromCol, toRow, toCol)
            }
            PieceType.KING -> {
                rowDiff <= 1 && colDiff <= 1
            }
        }
    }

    private fun isPathClear(fromRow: Int, fromCol: Int, toRow: Int, toCol: Int): Boolean {
        val rowStep = Integer.signum(toRow - fromRow)
        val colStep = Integer.signum(toCol - fromCol)

        var currentRow = fromRow + rowStep
        var currentCol = fromCol + colStep

        while (currentRow != toRow || currentCol != toCol) {
            if (board[currentRow][currentCol] != null) {
                return false
            }
            currentRow += rowStep
            currentCol += colStep
        }
        return true
    }

    fun isKingInCheck(player: Player): Boolean {
        // Find king
        var kingRow = -1
        var kingCol = -1
        for (r in 0..7) {
            for (c in 0..7) {
                val piece = board[r][c]
                if (piece != null && piece.type == PieceType.KING && piece.player == player) {
                    kingRow = r
                    kingCol = c
                    break
                }
            }
            if (kingRow != -1) break
        }

        if (kingRow == -1) return false // King not found (shouldn't happen in a valid game)

        // Check if any opponent piece can move to king's position
        for (r in 0..7) {
            for (c in 0..7) {
                val piece = board[r][c]
                if (piece != null && piece.player != player) {
                    if (isValidPieceMove(r, c, kingRow, kingCol)) {
                        return true
                    }
                }
            }
        }
        return false
    }

    fun isValidMove(fromRow: Int, fromCol: Int, toRow: Int, toCol: Int): Boolean {
        val pieceToMove = board[fromRow][fromCol] ?: return false

        // Cannot move to the same square
        if (fromRow == toRow && fromCol == toCol) {
            return false
        }

        // Cannot capture own piece
        val destinationPiece = board[toRow][toCol]
        if (destinationPiece != null && destinationPiece.player == pieceToMove.player) {
            return false
        }

        if (!isValidPieceMove(fromRow, fromCol, toRow, toCol)) {
            return false
        }

        // Simulate move
        board[toRow][toCol] = pieceToMove
        board[fromRow][fromCol] = null

        val inCheck = isKingInCheck(pieceToMove.player)

        // Undo move
        board[fromRow][fromCol] = pieceToMove
        board[toRow][toCol] = destinationPiece

        return !inCheck
    }

    private fun hasValidMoves(player: Player): Boolean {
        for (r1 in 0..7) {
            for (c1 in 0..7) {
                val piece = board[r1][c1]
                if (piece != null && piece.player == player) {
                    for (r2 in 0..7) {
                        for (c2 in 0..7) {
                            if (isValidMove(r1, c1, r2, c2)) {
                                return true
                            }
                        }
                    }
                }
            }
        }
        return false
    }

    private fun updateGameState() {
        val hasMoves = hasValidMoves(currentPlayer)
        val inCheck = isKingInCheck(currentPlayer)

        if (!hasMoves) {
            if (inCheck) {
                gameState = GameState.CHECKMATE
            } else {
                gameState = GameState.STALEMATE
            }
        }
    }

    fun movePiece(fromRow: Int, fromCol: Int, toRow: Int, toCol: Int): Boolean {
        if (gameState != GameState.IN_PROGRESS) {
            println("Game is over")
            return false
        }
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

        if (!isValidMove(fromRow, fromCol, toRow, toCol)) {
            println("Invalid move (piece rules or puts king in check)")
            return false
        }

        // Update board state
        board[toRow][toCol] = pieceToMove
        board[fromRow][fromCol] = null

        // Pawn Promotion (Auto-promote to Queen)
        if (pieceToMove.type == PieceType.PAWN) {
            if ((pieceToMove.player == Player.WHITE && toRow == 0) ||
                (pieceToMove.player == Player.BLACK && toRow == 7)) {
                board[toRow][toCol] = Piece(PieceType.QUEEN, pieceToMove.player)
            }
        }

        // Switch current player
        currentPlayer = if (currentPlayer == Player.WHITE) Player.BLACK else Player.WHITE

        updateGameState()

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
