package com.cs407.badgerhuddle.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.cs407.badgerhuddle.data.mockFriends
import com.cs407.badgerhuddle.ui.theme.RedUW
import com.google.firebase.firestore.FirebaseFirestore

data class HomeGame(
    val sport: String = "",
    val date: String = "",
    val time: String = "",
    val courtName: String = "",
    val players: Int = 0,
    val maxPlayers: Int = 0
)

@Composable
fun HomeScreen(onNavigate: (String) -> Unit, modifier: Modifier = Modifier) {

    val db = FirebaseFirestore.getInstance()
    var games by remember { mutableStateOf(listOf<HomeGame>()) }

    LaunchedEffect(Unit) {
        db.collection("Courts").addSnapshotListener { snapshot, _ ->
            if (snapshot != null) {
                games = snapshot.documents.mapNotNull { doc ->
                    val data = doc.data ?: return@mapNotNull null
                    HomeGame(
                        sport = data["Sport"]?.toString() ?: "",
                        date = data["Date"]?.toString() ?: "",
                        time = data["Time"]?.toString() ?: "",
                        courtName = data["Court"]?.toString() ?: "",
                        players = (data["NumCheckedIn"] as? Long)?.toInt() ?: 0,
                        maxPlayers = (data["MaxCheckIn"] as? Long)?.toInt() ?: 0
                    )
                }
            }
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        item {
            Text("Welcome Back!", color = RedUW, style = MaterialTheme.typography.headlineSmall)
            Text("Find your next game or check in nearby")
        }

        if (mockFriends.any { it.isActive }) {
            item {
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp)) {
                        Text("Friends Playing Now", color = RedUW)
                        mockFriends.filter { it.isActive }.forEach {
                            Text("${it.name} at ${it.currentCourt}", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
        }

        item {
            Text("Upcoming Games", style = MaterialTheme.typography.titleMedium)
            games.forEach { game ->
                Card(
                    onClick = { onNavigate("games") },
                    modifier = Modifier
                        .padding(vertical = 4.dp)
                        .fillMaxWidth()
                ) {
                    Column(Modifier.padding(12.dp)) {
                        Text(game.sport)
                        Text("${game.date}  •  ${game.time}")
                        Text("${game.players}/${game.maxPlayers} players at ${game.courtName}")
                    }
                }
            }
        }
    }
}
