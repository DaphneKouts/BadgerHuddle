package com.cs407.badgerhuddle.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.cs407.badgerhuddle.ui.theme.RedUW
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
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
    val maxPlayers: Int = 0,
    val players: List<String> = emptyList()
)

@Composable
fun GamesScreen(onNavigate: (String) -> Unit, modifier: Modifier = Modifier) {
    val db = FirebaseFirestore.getInstance()
    val user = FirebaseAuth.getInstance().currentUser
    val uid = user?.uid ?: ""

    var games by remember { mutableStateOf(listOf<CourtGame>()) }
    val scope = rememberCoroutineScope()

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
                maxPlayers = (data["MaxCheckIn"] as? Long)?.toInt() ?: 0,
                players = (data["Players"] as? List<String>) ?: emptyList()
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

            val userJoined = game.players.contains(uid)

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
                                    val curr = (snapshot.getLong("NumCheckedIn") ?: 0L).toInt()
                                    val max = (snapshot.getLong("MaxCheckIn") ?: 0L).toInt()
                                    val currentPlayersList =
                                        (snapshot.get("Players") as? List<String>) ?: emptyList()

                                    if (!userJoined) {
                                        // Can't join if full
                                        if (curr >= max) return@launch

                                        gameRef.update(
                                            mapOf(
                                                "NumCheckedIn" to curr + 1,
                                                "Players" to FieldValue.arrayUnion(uid)
                                            )
                                        ).await()

                                        games = games.map {
                                            if (it.id == game.id)
                                                it.copy(
                                                    currentPlayers = curr + 1,
                                                    players = currentPlayersList + uid
                                                )
                                            else it
                                        }
                                    } else {
                                        val newValue = (curr - 1).coerceAtLeast(0)

                                        gameRef.update(
                                            mapOf(
                                                "NumCheckedIn" to newValue,
                                                "Players" to FieldValue.arrayRemove(uid)
                                            )
                                        ).await()

                                        games = games.map {
                                            if (it.id == game.id)
                                                it.copy(
                                                    currentPlayers = newValue,
                                                    players = currentPlayersList.filter { id -> id != uid }
                                                )
                                            else it
                                        }
                                    }
                                }
                            },
                            enabled = (!userJoined && game.currentPlayers < game.maxPlayers) || userJoined,
                            colors = ButtonDefaults.buttonColors(
                                containerColor =
                                    if (userJoined) MaterialTheme.colorScheme.primary else RedUW
                            ),
                            modifier = Modifier.height(40.dp)
                        ) {
                            Text(
                                text = if (userJoined) "Checked In" else "Check In",
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                }
            }
        }
    }
}
