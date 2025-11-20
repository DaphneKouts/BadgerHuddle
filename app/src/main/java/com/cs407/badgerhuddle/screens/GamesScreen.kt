package com.cs407.badgerhuddle.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.cs407.badgerhuddle.ui.theme.RedUW
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class CourtGame(
    val id: String = "",
    val sport: String = "",
    val courtName: String = "",
    val date: String = "",
    val time: String = "",
    val currentPlayers: Int = 0,
    val maxPlayers: Int = 0
)

@Composable
fun GamesScreen(onNavigate: (String) -> Unit, modifier: Modifier = Modifier) {
    var rsvped by remember { mutableStateOf(setOf<String>()) }
    var games by remember { mutableStateOf(listOf<CourtGame>()) }
    val scope = rememberCoroutineScope()
    val db = FirebaseFirestore.getInstance()

    LaunchedEffect(Unit) {
        val snapshot = db.collection("Courts").get().await()
        games = snapshot.documents.mapNotNull { doc ->
            val data = doc.data ?: return@mapNotNull null
            CourtGame(
                id = data["GameId"]?.toString() ?: doc.id,
                sport = data["Sport"]?.toString() ?: "",
                courtName = data["Court"]?.toString() ?: "",
                date = data["Date"]?.toString() ?: "",
                time = data["Time"]?.toString() ?: "",
                currentPlayers = (data["NumCheckedIn"] as? Long)?.toInt() ?: 0,
                maxPlayers = (data["MaxCheckIn"] as? Long)?.toInt() ?: 0
            )
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(games) { game ->
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
                                scope.launch {

                                    val gameRef = db.collection("Courts").document(game.id)
                                    val snapshot = gameRef.get().await()
                                    val current = (snapshot.getLong("NumCheckedIn") ?: 0L).toInt()
                                    val max = (snapshot.getLong("MaxCheckIn") ?: 0L).toInt()

                                    if (!joined) {
                                        if (current >= max) return@launch
                                        gameRef.update("NumCheckedIn", current + 1).await()
                                        rsvped = rsvped + game.id
                                        games = games.map {
                                            if (it.id == game.id) it.copy(currentPlayers = current + 1) else it
                                        }
                                    } else {
                                        val newValue = (current - 1).coerceAtLeast(0)
                                        gameRef.update("NumCheckedIn", newValue).await()
                                        rsvped = rsvped - game.id
                                        games = games.map {
                                            if (it.id == game.id) it.copy(currentPlayers = newValue) else it
                                        }
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (joined) MaterialTheme.colorScheme.primary else RedUW
                            ),
                            modifier = Modifier.height(40.dp)
                        ) {
                            Text(
                                text = if (joined) "RSVP’d" else "RSVP",
                                color = if (joined) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                }
            }
        }
    }
}
