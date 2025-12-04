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
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@Composable
fun FriendsScreen(onNavigate: (String) -> Unit, modifier: Modifier = Modifier) {
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    val uid = auth.currentUser?.uid

    var friends by remember { mutableStateOf(listOf<String>()) }
    val scope = rememberCoroutineScope()

    // Fetch friends when the composable is first composed
    LaunchedEffect(uid) {
        if (uid != null) {
            scope.launch {
                try {
                    val doc = db.collection("Profiles").document(uid).get().await()
                    val friendsList = doc.get("friends") as? List<String> ?: emptyList()
                    friends = friendsList
                } catch (e: Exception) {
                    println("Error fetching friends: ${e.message}")
                }
            }
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(friends) { friendName ->
            Card {
                Column(Modifier.padding(12.dp)) {
                    Text(friendName, style = MaterialTheme.typography.titleMedium)
                }
            }
        }
    }
}
