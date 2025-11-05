package com.cs407.badgerhuddle.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.cs407.badgerhuddle.data.mockCourts
import com.cs407.badgerhuddle.data.mockFriends
import com.cs407.badgerhuddle.data.mockGames
import com.cs407.badgerhuddle.ui.theme.RedUW

@Composable
fun HomeScreen(onNavigate: (String) -> Unit, modifier: Modifier = Modifier) {
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
                Card {
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
            Text("Nearby Courts", style = MaterialTheme.typography.titleMedium)
            mockCourts.forEach {
                Card(onClick = { onNavigate("courts") }, modifier = Modifier.padding(vertical = 4.dp)) {
                    Column(Modifier.padding(12.dp)) {
                        Text(it.name)
                        Text("${it.currentPlayers}/${it.capacity} players • ${it.distance}")
                    }
                }
            }
        }

        item {
            Text("Upcoming Games", style = MaterialTheme.typography.titleMedium)
            mockGames.forEach {
                Card(onClick = { onNavigate("games") }, modifier = Modifier.padding(vertical = 4.dp)) {
                    Column(Modifier.padding(12.dp)) {
                        Text(it.sport)
                        Text("${it.date}  •  ${it.time}")
                        Text("Hosted by ${it.hostName}")
                    }
                }
            }
        }
    }
}