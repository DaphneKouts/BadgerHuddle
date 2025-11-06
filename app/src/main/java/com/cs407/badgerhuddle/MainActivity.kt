package com.cs407.badgerhuddle

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.Group
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.cs407.badgerhuddle.screens.*
import com.cs407.badgerhuddle.ui.theme.BadgerHuddleTheme
import com.cs407.badgerhuddle.ui.theme.RedUW

class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BadgerHuddleTheme {
                var currentScreen by remember { mutableStateOf("home") }

                Scaffold(
                    topBar = {
                        CenterAlignedTopAppBar(
                            title = {
                                Text(
                                    text = "Badger Huddle",
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                            },
                            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                                containerColor = RedUW
                            )
                        )
                    },
                    bottomBar = {
                        CustomBottomNav(currentScreen) { currentScreen = it }
                    }
                ) { innerPadding ->
                    when (currentScreen) {
                        "home" -> HomeScreen({ currentScreen = it }, Modifier.padding(innerPadding))
                        "games" -> GamesScreen({ currentScreen = it }, Modifier.padding(innerPadding))
                        "create" -> CreateGameScreen({ currentScreen = it }, Modifier.padding(innerPadding))
                        "profile" -> ProfileScreen({ currentScreen = it }, Modifier.padding(innerPadding))
                        "friends" -> FriendsScreen({ currentScreen = it }, Modifier.padding(innerPadding))
                        "courts" -> CourtsScreen({ currentScreen = it }, Modifier.padding(innerPadding))
                        "share" -> ShareProfileScreen({ currentScreen = it }, Modifier.padding(innerPadding))
                    }
                }
            }
        }
    }
}

@Composable
fun CustomBottomNav(currentScreen: String, onNavigate: (String) -> Unit) {
    val items: List<Triple<String, androidx.compose.ui.graphics.vector.ImageVector, String>> = listOf(
        Triple("home", Icons.Filled.Home, "Home"),
        Triple("courts", Icons.Filled.Map, "Courts"),
        Triple("games", Icons.Filled.CalendarMonth, "Games"),
        Triple("create", Icons.Filled.Add, "Create"),
        Triple("friends", Icons.Filled.Group, "Friends"),
        Triple("profile", Icons.Filled.Person, "Profile")
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        for ((id, icon, label) in items) {
            val selected = currentScreen == id
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clickable { onNavigate(id) }
                    .padding(horizontal = 6.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = if (selected) RedUW else Color.Gray
                )
                Text(
                    text = label,
                    color = if (selected) RedUW else Color.Gray,
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}
