package com.example.chess_app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Create an instance of ChessGame
        val chessGame = ChessGame()

        // Create an instance of ChessBoardView and pass the ChessGame instance
        val chessBoardView = ChessBoardView(this, chessGame = chessGame)

        // Set the ChessBoardView as the content view
        setContentView(chessBoardView)
    }
}
