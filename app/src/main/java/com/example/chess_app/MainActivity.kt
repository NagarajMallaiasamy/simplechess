package com.example.chess_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.chess_app.model.ChessGame
import com.example.chess_app.ui.theme.SimpleChessTheme
import com.example.chess_app.ui.ChessBoardComposable
import com.example.chess_app.ui.GameStateDisplay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SimpleChessTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Make chessGame a state object to trigger recomposition for GameStateDisplay
                    var chessGame by remember { mutableStateOf(ChessGame()) }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        GameStateDisplay(
                            chessGame = chessGame,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                        )
                        ChessBoardComposable(
                            chessGame = chessGame,
                            modifier = Modifier.weight(1f) // Allow chessboard to take remaining space
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    SimpleChessTheme {
        val chessGame = ChessGame() // Preview uses a fresh game instance
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            GameStateDisplay(chessGame = chessGame, modifier = Modifier.padding(8.dp))
            ChessBoardComposable(chessGame = chessGame)
        }
    }
}
