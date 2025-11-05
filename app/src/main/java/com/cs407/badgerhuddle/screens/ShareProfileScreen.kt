package com.cs407.badgerhuddle.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
fun ShareProfileScreen(onNavigate: (String) -> Unit, modifier: Modifier = Modifier) {
    var copied by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Share Profile", style = MaterialTheme.typography.titleLarge)
        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp)) {
                Text("Scan this mock QR code to connect with players.")
                Spacer(Modifier.height(16.dp))
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .padding(8.dp),
                    contentAlignment = androidx.compose.ui.Alignment.Center
                ) { Text("[ QR CODE ]") }
            }
        }
        Button(
            onClick = { copied = true },
            colors = ButtonDefaults.buttonColors(containerColor = RedUW),
            modifier = Modifier.fillMaxWidth()
        ) { Text(if (copied) "Copied!" else "Copy Link") }

        OutlinedButton(onClick = { onNavigate("profile") }, modifier = Modifier.fillMaxWidth()) {
            Text("Back to Profile")
        }
    }
}