package com.cs407.badgerhuddle.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.cs407.badgerhuddle.data.mockGames
import com.cs407.badgerhuddle.ui.theme.RedUW

@Composable
fun GamesScreen(onNavigate: (String) -> Unit, modifier: Modifier = Modifier) {
    var rsvped by remember { mutableStateOf(setOf<String>()) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(mockGames) { game ->
            val joined = rsvped.contains(game.id)
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text("${game.sport} at ${game.courtName}", color = RedUW)
                    Text("${game.date} • ${game.time}")
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                    ) {
                        Text("${game.currentPlayers}/${game.maxPlayers} players")
                        Button(
                            onClick = {
                                rsvped = if (joined) rsvped - game.id else rsvped + game.id
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (joined) MaterialTheme.colorScheme.secondary else RedUW
                            )
                        ) {
                            Text(if (joined) "Cancel RSVP" else "RSVP")
                        }
                    }
                }
            }
        }
    }
}