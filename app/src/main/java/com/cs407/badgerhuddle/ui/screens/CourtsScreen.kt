package com.cs407.badgerhuddle.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.cs407.badgerhuddle.data.mockCourts
import com.cs407.badgerhuddle.ui.theme.RedUW

@Composable
fun CourtsScreen(onNavigate: (String) -> Unit, modifier: Modifier = Modifier) {
    var checkedIn by remember { mutableStateOf<String?>(null) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(mockCourts) { c ->
            val isHere = checkedIn == c.id
            Card {
                Column(Modifier.padding(12.dp)) {
                    Text("${c.name} • ${c.distance}")
                    Text("${c.currentPlayers}/${c.capacity} players")
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = { checkedIn = if (isHere) null else c.id },
                            colors = ButtonDefaults.buttonColors(containerColor = if (isHere) Color.Gray else RedUW),
                            modifier = Modifier.weight(1f)
                        ) { Text(if (isHere) "Check Out" else "Check In") }
                        Button(
                            onClick = { },
                            modifier = Modifier.weight(1f)
                        ) { Text("Directions") }
                    }
                }
            }
        }
    }
}