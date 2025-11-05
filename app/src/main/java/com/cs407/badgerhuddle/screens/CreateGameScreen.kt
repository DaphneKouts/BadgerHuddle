package com.cs407.badgerhuddle.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.cs407.badgerhuddle.ui.theme.RedUW

@Composable
fun CreateGameScreen(onNavigate: (String) -> Unit, modifier: Modifier = Modifier) {
    var sport by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Create a Game", style = MaterialTheme.typography.titleLarge)
        OutlinedTextField(sport, { sport = it }, label = { Text("Sport") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(location, { location = it }, label = { Text("Location") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(date, { date = it }, label = { Text("Date") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(time, { time = it }, label = { Text("Time") }, modifier = Modifier.fillMaxWidth())
        Button(
            onClick = { onNavigate("games") },
            colors = ButtonDefaults.buttonColors(containerColor = RedUW),
            modifier = Modifier.fillMaxWidth()
        ) { Text("Create Game") }
    }
}
