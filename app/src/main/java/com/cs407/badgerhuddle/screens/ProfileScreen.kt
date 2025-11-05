package com.cs407.badgerhuddle.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.cs407.badgerhuddle.ui.theme.RedUW

@Composable
fun ProfileScreen(onNavigate: (String) -> Unit, modifier: Modifier = Modifier) {
    var name by remember { mutableStateOf("Alex Student") }
    var bio by remember { mutableStateOf("UW-Madison Athlete & Pickup Player") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Profile", style = MaterialTheme.typography.titleLarge)
        OutlinedTextField(name, { name = it }, label = { Text("Name") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(bio, { bio = it }, label = { Text("Bio") }, modifier = Modifier.fillMaxWidth())
        Button(
            onClick = { onNavigate("share") },
            colors = ButtonDefaults.buttonColors(containerColor = RedUW),
            modifier = Modifier.fillMaxWidth()
        ) { Text("Share Profile") }
    }
}