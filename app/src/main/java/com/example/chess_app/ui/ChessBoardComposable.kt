package com.example.chess_app.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt
// R import is not directly needed here if getDrawableResIdForPiece handles it
import com.example.chess_app.model.ChessGame
import com.example.chess_app.model.Piece
import com.example.chess_app.model.Player
import com.example.chess_app.model.PieceType
import com.example.chess_app.ui.theme.DarkBrown
import com.example.chess_app.ui.theme.LightBrown
// getDrawableResIdForPiece will be imported from DrawableMappings.kt


@Composable
fun ChessBoardComposable(chessGame: ChessGame, modifier: Modifier = Modifier) {
    var selectedRow by remember { mutableStateOf(-1) }
    var selectedCol by remember { mutableStateOf(-1) }
    var validMoves by remember { mutableStateOf<List<Pair<Int, Int>>>(emptyList()) }

    // Drag state
    var draggingPieceInfo by remember { mutableStateOf<Triple<Int, Int, Piece?>?>(null) } // startRow, startCol, Piece
    var dragOffsetX by remember { mutableStateOf(0f) }
    var dragOffsetY by remember { mutableStateOf(0f) }
    var hoveredDropTargetRow by remember { mutableStateOf(-1) }
    var hoveredDropTargetCol by remember { mutableStateOf(-1) }

    val density = LocalDensity.current

    BoxWithConstraints(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        val boardSize = minOf(maxWidth, maxHeight)
        val squareSize = boardSize / 8

        Column {
            for (row in 0..7) {
                Row {
                    for (col in 0..7) {
                        val squareColor = if ((row + col) % 2 == 0) LightBrown else DarkBrown
                        val currentSquareRow = row
                        val currentSquareCol = col
                        val isSelectedSquare = currentSquareRow == selectedRow && currentSquareCol == selectedCol
                        val pieceOnSquare = chessGame.getPieceAt(currentSquareRow, currentSquareCol)
                        val isDraggingThisPiece = draggingPieceInfo?.first == currentSquareRow && draggingPieceInfo?.second == currentSquareCol

                        val squareModifier = Modifier
                            .size(squareSize)
                            .background(squareColor)
                            .border(
                                width = if (isSelectedSquare && draggingPieceInfo == null) 2.dp else 0.dp, // Highlight selected only if not dragging
                                color = if (isSelectedSquare && draggingPieceInfo == null) Color.Red else Color.Transparent
                            )
                            .clickable {
                                if (draggingPieceInfo != null) return@clickable // Don't handle clicks if a drag is active from another square

                                if (selectedRow == -1) { // No piece selected
                                    if (pieceOnSquare != null && pieceOnSquare.player == chessGame.currentPlayer) {
                                        selectedRow = currentSquareRow
                                        selectedCol = currentSquareCol
                                        validMoves = chessGame.getValidMoves(currentSquareRow, currentSquareCol)
                                    }
                                } else { // A piece is selected
                                    if (selectedRow == currentSquareRow && selectedCol == currentSquareCol) { // Tapped same selected square
                                        selectedRow = -1
                                        selectedCol = -1
                                        validMoves = emptyList()
                                    } else { // Tapped a new square (target or select another piece)
                                        if (validMoves.contains(currentSquareRow to currentSquareCol)) {
                                            chessGame.movePiece(selectedRow, selectedCol, currentSquareRow, currentSquareCol)
                                        }
                                        // Reset selection and valid moves after any action on a target square
                                        selectedRow = -1
                                        selectedCol = -1
                                        validMoves = emptyList()
                                    }
                                }
                            }

                        Box(
                            modifier = squareModifier,
                            contentAlignment = Alignment.Center
                        ) {
                            // Valid move indicator (small circle)
                            if (validMoves.contains(currentSquareRow to currentSquareCol) && !isDraggingThisPiece) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize(0.3f)
                                        .background(Color.Green.copy(alpha = 0.4f))
                                )
                            }
                            // Hovered drop target indicator
                            if (draggingPieceInfo != null && hoveredDropTargetRow == currentSquareRow && hoveredDropTargetCol == currentSquareCol && validMoves.contains(currentSquareRow to currentSquareCol)) {
                                 Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(Color.Blue.copy(alpha = 0.3f))
                                )
                            }


                            pieceOnSquare?.let { piece ->
                                val drawableResId = getDrawableResIdForPiece(piece)
                                val pieceModifier = if (isDraggingThisPiece) {
                                    Modifier
                                        .offset { IntOffset(dragOffsetX.roundToInt(), dragOffsetY.roundToInt()) }
                                        .fillMaxSize(0.9f) // Slightly larger when dragging
                                        .zIndex(1f) // Bring dragged piece to front
                                } else {
                                    Modifier.fillMaxSize(0.8f)
                                }

                                Image(
                                    painter = painterResource(id = drawableResId),
                                    contentDescription = "Piece: ${piece.type} ${piece.player}",
                                    modifier = pieceModifier
                                        .pointerInput(Unit) { // Use Unit or piece key if issues with re-triggering
                                            if (piece.player == chessGame.currentPlayer) {
                                                detectDragGestures(
                                                    onDragStart = {
                                                        if (chessGame.getPieceAt(currentSquareRow, currentSquareCol)?.player == chessGame.currentPlayer) {
                                                            selectedRow = currentSquareRow
                                                            selectedCol = currentSquareCol
                                                            validMoves = chessGame.getValidMoves(currentSquareRow, currentSquareCol)
                                                            draggingPieceInfo = Triple(currentSquareRow, currentSquareCol, piece)
                                                            dragOffsetX = 0f
                                                            dragOffsetY = 0f
                                                            hoveredDropTargetRow = currentSquareRow
                                                            hoveredDropTargetCol = currentSquareCol
                                                        }
                                                    },
                                                    onDrag = { change, dragAmount ->
                                                        if (draggingPieceInfo != null) {
                                                            change.consume()
                                                            dragOffsetX += dragAmount.x
                                                            dragOffsetY += dragAmount.y

                                                            val squareSizePx = density.run { squareSize.toPx() }
                                                            hoveredDropTargetRow = (currentSquareRow + (dragOffsetY / squareSizePx)).roundToInt().coerceIn(0, 7)
                                                            hoveredDropTargetCol = (currentSquareCol + (dragOffsetX / squareSizePx)).roundToInt().coerceIn(0, 7)
                                                        }
                                                    },
                                                    onDragEnd = {
                                                        draggingPieceInfo?.let { (startR, startC, _) ->
                                                            if (validMoves.contains(hoveredDropTargetRow to hoveredDropTargetCol)) {
                                                                chessGame.movePiece(startR, startC, hoveredDropTargetRow, hoveredDropTargetCol)
                                                            }
                                                        }
                                                        draggingPieceInfo = null
                                                        selectedRow = -1
                                                        selectedCol = -1
                                                        validMoves = emptyList()
                                                        dragOffsetX = 0f
                                                        dragOffsetY = 0f
                                                        hoveredDropTargetRow = -1
                                                        hoveredDropTargetCol = -1
                                                    },
                                                    onDragCancel = {
                                                        draggingPieceInfo = null
                                                        selectedRow = -1
                                                        selectedCol = -1
                                                        validMoves = emptyList()
                                                        dragOffsetX = 0f
                                                        dragOffsetY = 0f
                                                        hoveredDropTargetRow = -1
                                                        hoveredDropTargetCol = -1
                                                    }
                                                )
                                            }
                                        }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// private fun getDrawableResIdForPiece(piece: Piece): Int { ... } // Removed
