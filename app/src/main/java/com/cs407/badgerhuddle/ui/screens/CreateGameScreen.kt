package com.cs407.badgerhuddle.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.cs407.badgerhuddle.ui.theme.RedUW
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.time.Instant
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationDropdown(
    location: String,
    onLocationSelected: (String) -> Unit
) {
    val options = listOf(
        "Nick 3-6",
        "Bakke 5-8",
        "Near East Fields",
        "Dejope Volleyball Courts",
        "Nick 1",
        "Nick 2",
        "Near West fields",
        "Observatory Basketball Courts",
        "Witte Volleyball Courts",
        "Sellery Basketball Courts",
        "Dejope Soccer Courts"
    )

    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {

        OutlinedTextField(
            value = location,
            onValueChange = {},
            readOnly = true,
            label = { Text("Location") },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
                .clickable { expanded = true },
            enabled = true,
            interactionSource = remember { MutableInteractionSource() }
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onLocationSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerField(
    date: String,
    onDateSelected: (String) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    // BUTTON instead of a textfield
    OutlinedButton(
        onClick = { showDialog = true },
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(if (date.isEmpty()) "Select Date" else date)
    }

    if (showDialog) {
        val dateState = rememberDatePickerState()

        DatePickerDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        val millis = dateState.selectedDateMillis
                        if (millis != null) {
                            val local = Instant.ofEpochMilli(millis)
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate()
                            onDateSelected(local.format(formatter))
                        }
                        showDialog = false
                    }
                ) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = dateState)
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerField(
    time: String,
    onTimeSelected: (String) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    val formatter = DateTimeFormatter.ofPattern("HH:mm")

    OutlinedButton(
        onClick = { showDialog = true },
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(if (time.isEmpty()) "Select Time" else time)
    }

    if (showDialog) {
        val timeState = rememberTimePickerState()

        AlertDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        val t = LocalTime.of(timeState.hour, timeState.minute)
                        onTimeSelected(t.format(formatter))
                        showDialog = false
                    }
                ) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) { Text("Cancel") }
            },
            text = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Select Time")
                    Spacer(modifier = Modifier.height(12.dp))
                    TimePicker(state = timeState)
                }
            }
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateGameScreen(onNavigate: (String) -> Unit, modifier: Modifier = Modifier) {
    var sport by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("") }
    var maxPeople by remember { mutableStateOf("") }

    var errorMessage by remember { mutableStateOf<String?>(null) }

    val db = FirebaseFirestore.getInstance()
    val uid = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    val scope = rememberCoroutineScope()

    val allFieldsFilled = sport.isNotBlank()
            && location.isNotBlank()
            && date.isNotBlank()
            && time.isNotBlank()
            && maxPeople.toIntOrNull() != null
            && (maxPeople.toIntOrNull() ?: 0) > 0

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

        LocationDropdown(
            location = location,
            onLocationSelected = { location = it }
        )

        DatePickerField(
            date = date,
            onDateSelected = { date = it }
        )

        TimePickerField(
            time = time,
            onTimeSelected = { time = it }
        )

        OutlinedTextField(
            value = maxPeople,
            onValueChange = { maxPeople = it },
            label = { Text("Max People Allowed") },
            modifier = Modifier.fillMaxWidth()
        )

        errorMessage?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Button(
            onClick = {
                scope.launch {
                    if (uid.isBlank()) {
                        errorMessage = "You must be signed in to create a game."
                        return@launch
                    }

                    if (!allFieldsFilled) {
                        errorMessage = "Please fill out all fields correctly."
                        return@launch
                    }

                    val maxValue = maxPeople.toIntOrNull() ?: 10
                    val gameId = System.currentTimeMillis().toString()

                    val query = db.collection("Courts")
                        .whereEqualTo("Court", location)
                        .whereEqualTo("Date", date)
                        .whereEqualTo("Time", time)
                        .get()
                        .await()

                    if (!query.isEmpty) {
                        errorMessage = "A game already exists at this court, date, and time."
                        return@launch
                    }

                    val data = hashMapOf(
                        "Sport" to sport,
                        "Court" to location,
                        "Date" to date,
                        "Time" to time,
                        "NumCheckedIn" to 1,
                        "MaxCheckIn" to maxValue,
                        "GameId" to gameId,
                        "Players" to listOf(uid)
                    )

                    db.collection("Courts").document(gameId)
                        .set(data)
                        .addOnSuccessListener {
                            onNavigate("games")
                        }
                        .addOnFailureListener { e ->
                            errorMessage = "Error creating game: ${e.message}"
                        }
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = RedUW),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Create Game")
        }
    }
}