package com.cs407.badgerhuddle.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.cs407.badgerhuddle.data.mockFriends
import com.cs407.badgerhuddle.ui.theme.RedUW

@Composable
fun FriendsScreen(onNavigate: (String) -> Unit, modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(mockFriends) { f ->
            Card {
                Column(Modifier.padding(12.dp)) {
                    Text(f.name, style = MaterialTheme.typography.titleMedium)
                    Text(if (f.isActive) "Active at ${f.currentCourt}" else "Offline")
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        Button(
                            onClick = { onNavigate("courts") },
                            enabled = f.isActive,
                            colors = ButtonDefaults.buttonColors(containerColor = RedUW)
                        ) {
                            Text("Join")
                        }
                    }
                }
            }
        }
    }
}
