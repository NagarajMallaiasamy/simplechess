package com.samnagaraj.simplechess

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import com.samnagaraj.simplechess.model.ChessGame
import com.samnagaraj.simplechess.model.Piece
import com.samnagaraj.simplechess.model.PieceType
import com.samnagaraj.simplechess.model.Player

class ChessBoardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    private val chessGame: ChessGame
) : View(context, attrs, defStyleAttr) {

    private val lightColor = ContextCompat.getColor(context, R.color.light_brown)
    private val darkColor = ContextCompat.getColor(context, R.color.dark_brown)
    private val highlightColor = Color.argb(100, 255, 255, 0) // Semi-transparent yellow

    private val lightPaint = Paint().apply { color = lightColor }
    private val darkPaint = Paint().apply { color = darkColor }
    private val highlightPaint = Paint().apply {
        color = highlightColor
        style = Paint.Style.STROKE // Draw a border
        strokeWidth = 8f // Border width
    }


    private var squareSize = 0f
    private var selectedRow = -1
    private var selectedCol = -1


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val height = MeasureSpec.getSize(heightMeasureSpec)
        val size = if (width < height) width else height
        setMeasuredDimension(size, size)
        squareSize = size / 8f
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawBoard(canvas)
        drawHighlight(canvas) // Draw highlight before pieces
        drawPieces(canvas)
    }

    private fun drawBoard(canvas: Canvas) {
        for (row in 0..7) {
            for (col in 0..7) {
                val paint = if ((row + col) % 2 == 0) lightPaint else darkPaint
                val left = col * squareSize
                val top = row * squareSize
                val right = left + squareSize
                val bottom = top + squareSize
                canvas.drawRect(left, top, right, bottom, paint)
            }
        }
    }

    private fun drawHighlight(canvas: Canvas) {
        if (selectedRow != -1 && selectedCol != -1) {
            val left = selectedCol * squareSize
            val top = selectedRow * squareSize
            val right = left + squareSize
            val bottom = top + squareSize
            // canvas.drawRect(left, top, right, bottom, highlightPaint) // Fills the square
            // Draw a border instead for better visibility
            val rect = RectF(left + highlightPaint.strokeWidth / 2,
                             top + highlightPaint.strokeWidth / 2,
                             right - highlightPaint.strokeWidth / 2,
                             bottom - highlightPaint.strokeWidth / 2)
            canvas.drawRect(rect, highlightPaint)
        }
    }

    private fun drawPieces(canvas: Canvas) {
        for (row in 0..7) {
            for (col in 0..7) {
                chessGame.getPieceAt(row, col)?.let { piece ->
                    val drawableResId = getDrawableResIdForPiece(piece)
                    val drawable = ContextCompat.getDrawable(context, drawableResId)
                    drawable?.let {
                        val pieceSize = (squareSize * 0.8).toInt() // Piece is 80% of square size
                        val offset = ((squareSize - pieceSize) / 2).toInt()
                        val left = (col * squareSize).toInt() + offset
                        val top = (row * squareSize).toInt() + offset
                        it.setBounds(left, top, left + pieceSize, top + pieceSize)
                        it.draw(canvas)
                    }
                }
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val col = (event.x / squareSize).toInt()
            val row = (event.y / squareSize).toInt()

            if (row < 0 || row > 7 || col < 0 || col > 7) return true // Click outside board

            if (selectedRow == -1) { // No piece selected yet
                val piece = chessGame.getPieceAt(row, col)
                if (piece != null && piece.player == chessGame.currentPlayer) {
                    selectedRow = row
                    selectedCol = col
                    invalidate() // Redraw to show selection
                }
            } else { // A piece is selected, this is the target square
                val moveSuccessful = chessGame.movePiece(selectedRow, selectedCol, row, col)
                if (!moveSuccessful) {
                    // Optionally, provide feedback for illegal move
                    // Toast.makeText(context, "Illegal move", Toast.LENGTH_SHORT).show()
                }
                // Reset selection regardless of move success
                selectedRow = -1
                selectedCol = -1
                invalidate() // Redraw to reflect move and remove highlight
            }
            return true // Event handled
        }
        return super.onTouchEvent(event)
    }

    private fun getDrawableResIdForPiece(piece: Piece): Int {
        return when (piece.type) {
            PieceType.PAWN -> if (piece.player == Player.WHITE) R.drawable.ic_chess_pawn_white else R.drawable.ic_chess_pawn_black
            PieceType.ROOK -> if (piece.player == Player.WHITE) R.drawable.ic_chess_rook_white else R.drawable.ic_chess_rook_black
            PieceType.KNIGHT -> if (piece.player == Player.WHITE) R.drawable.ic_chess_knight_white else R.drawable.ic_chess_knight_black
            PieceType.BISHOP -> if (piece.player == Player.WHITE) R.drawable.ic_chess_bishop_white else R.drawable.ic_chess_bishop_black
            PieceType.QUEEN -> if (piece.player == Player.WHITE) R.drawable.ic_chess_queen_white else R.drawable.ic_chess_queen_black
            PieceType.KING -> if (piece.player == Player.WHITE) R.drawable.ic_chess_king_white else R.drawable.ic_chess_king_black
        }
    }
}
