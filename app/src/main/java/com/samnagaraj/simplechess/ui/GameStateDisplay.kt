package com.samnagaraj.simplechess.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.samnagaraj.simplechess.model.ChessGame
import com.samnagaraj.simplechess.model.Piece

@Composable
fun GameStateDisplay(chessGame: ChessGame, modifier: Modifier = Modifier) {
    Column(modifier = modifier.padding(8.dp)) {
        Text(
            text = "Turn: ${chessGame.currentPlayer.name}",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        CapturedPiecesRow("Captured by White:", chessGame.blackCapturedPieces)
        Spacer(modifier = Modifier.height(8.dp))
        CapturedPiecesRow("Captured by Black:", chessGame.whiteCapturedPieces)
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Move History:",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        LazyColumn(modifier = Modifier.heightIn(max = 100.dp) .fillMaxWidth()) { // Limit height and make scrollable
            items(chessGame.moveHistory) { move ->
                Text(move, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
private fun CapturedPiecesRow(label: String, capturedPieces: List<Piece>) {
    Column {
        Text(text = label, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
        if (capturedPieces.isEmpty()) {
            Text("None", style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(start = 8.dp))
        } else {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                capturedPieces.forEach { piece ->
                    Image(
                        painter = painterResource(id = getDrawableResIdForPiece(piece)),
                        contentDescription = "Captured ${piece.type.name} ${piece.player.name}",
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}
