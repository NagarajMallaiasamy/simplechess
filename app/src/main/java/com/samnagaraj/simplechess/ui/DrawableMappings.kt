package com.samnagaraj.simplechess.ui

import com.samnagaraj.simplechess.R
import com.samnagaraj.simplechess.model.Piece
import com.samnagaraj.simplechess.model.PieceType
import com.samnagaraj.simplechess.model.Player

fun getDrawableResIdForPiece(piece: Piece): Int {
    return when (piece.type) {
        PieceType.PAWN -> if (piece.player == Player.WHITE) R.drawable.ic_chess_pawn_white else R.drawable.ic_chess_pawn_black
        PieceType.ROOK -> if (piece.player == Player.WHITE) R.drawable.ic_chess_rook_white else R.drawable.ic_chess_rook_black
        PieceType.KNIGHT -> if (piece.player == Player.WHITE) R.drawable.ic_chess_knight_white else R.drawable.ic_chess_knight_black
        PieceType.BISHOP -> if (piece.player == Player.WHITE) R.drawable.ic_chess_bishop_white else R.drawable.ic_chess_bishop_black
        PieceType.QUEEN -> if (piece.player == Player.WHITE) R.drawable.ic_chess_queen_white else R.drawable.ic_chess_queen_black
        PieceType.KING -> if (piece.player == Player.WHITE) R.drawable.ic_chess_king_white else R.drawable.ic_chess_king_black
    }
}
