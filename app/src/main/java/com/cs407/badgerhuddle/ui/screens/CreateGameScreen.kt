package com.cs407.badgerhuddle.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.cs407.badgerhuddle.ui.theme.RedUW
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun CreateGameScreen(onNavigate: (String) -> Unit, modifier: Modifier = Modifier) {
    var sport by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("") }
    var maxPeople by remember { mutableStateOf("") }

    val db = FirebaseFirestore.getInstance()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Create a Game", style = MaterialTheme.typography.titleLarge)

        OutlinedTextField(
            value = sport,
            onValueChange = { sport = it },
            label = { Text("Sport") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = location,
            onValueChange = { location = it },
            label = { Text("Location") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = date,
            onValueChange = { date = it },
            label = { Text("Date") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = time,
            onValueChange = { time = it },
            label = { Text("Time") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = maxPeople,
            onValueChange = { maxPeople = it },
            label = { Text("Max People Allowed") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                val gameId = System.currentTimeMillis().toString()

                val maxValue = maxPeople.toIntOrNull() ?: 10

                val data = hashMapOf(
                    "Sport" to sport,
                    "Court" to location,
                    "Date" to date,
                    "Time" to time,
                    "NumCheckedIn" to 1,   // creator counts as checked in
                    "MaxCheckIn" to maxValue,
                    "GameId" to gameId
                )

                db.collection("Courts").document(gameId)
                    .set(data)
                    .addOnSuccessListener {
                        onNavigate("games")
                    }
                    .addOnFailureListener { e ->
                        e.printStackTrace()
                    }
            },
            colors = ButtonDefaults.buttonColors(containerColor = RedUW),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Create Game")
        }
    }
}
