package com.example.chess_app.ui

import com.example.chess_app.R
import com.example.chess_app.model.Piece
import com.example.chess_app.model.PieceType
import com.example.chess_app.model.Player

fun getDrawableResIdForPiece(piece: Piece): Int {
    val R_drawable = R.drawable
    return when (piece.type) {
        PieceType.PAWN -> if (piece.player == Player.WHITE) R_drawable.ic_chess_pawn_white else R_drawable.ic_chess_pawn_black
        PieceType.ROOK -> if (piece.player == Player.WHITE) R_drawable.ic_chess_rook_white else R_drawable.ic_chess_rook_black
        PieceType.KNIGHT -> if (piece.player == Player.WHITE) R_drawable.ic_chess_knight_white else R_drawable.ic_chess_knight_black
        PieceType.BISHOP -> if (piece.player == Player.WHITE) R_drawable.ic_chess_bishop_white else R_drawable.ic_chess_bishop_black
        PieceType.QUEEN -> if (piece.player == Player.WHITE) R_drawable.ic_chess_queen_white else R_drawable.ic_chess_queen_black
        PieceType.KING -> if (piece.player == Player.WHITE) R_drawable.ic_chess_king_white else R_drawable.ic_chess_king_black
    }
}
