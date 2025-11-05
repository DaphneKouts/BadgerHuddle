package com.cs407.badgerhuddle

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.padding

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
                        NavigationBar {
                            BottomNavItem(
                                label = "Home",
                                id = "home",
                                icon = Icons.Filled.Home,
                                current = currentScreen,
                                onClick = { currentScreen = it }
                            )
                            BottomNavItem(
                                label = "Courts",
                                id = "courts",
                                icon = Icons.Filled.Map,
                                current = currentScreen,
                                onClick = { currentScreen = it }
                            )
                            BottomNavItem(
                                label = "Games",
                                id = "games",
                                icon = Icons.Filled.CalendarMonth,
                                current = currentScreen,
                                onClick = { currentScreen = it }
                            )
                            BottomNavItem(
                                label = "Create",
                                id = "create",
                                icon = Icons.Filled.Add,
                                current = currentScreen,
                                onClick = { currentScreen = it }
                            )
                            BottomNavItem(
                                label = "Friends",
                                id = "friends",
                                icon = Icons.Filled.Group,
                                current = currentScreen,
                                onClick = { currentScreen = it }
                            )
                            BottomNavItem(
                                label = "Profile",
                                id = "profile",
                                icon = Icons.Filled.Person,
                                current = currentScreen,
                                onClick = { currentScreen = it }
                            )
                        }
                    }
                ) { innerPadding ->
                    when (currentScreen) {
                        "home" -> HomeScreen(
                            onNavigate = { currentScreen = it },
                            modifier = Modifier.padding(innerPadding)
                        )
                        "games" -> GamesScreen(
                            onNavigate = { currentScreen = it },
                            modifier = Modifier.padding(innerPadding)
                        )
                        "create" -> CreateGameScreen(
                            onNavigate = { currentScreen = it },
                            modifier = Modifier.padding(innerPadding)
                        )
                        "profile" -> ProfileScreen(
                            onNavigate = { currentScreen = it },
                            modifier = Modifier.padding(innerPadding)
                        )
                        "friends" -> FriendsScreen(
                            onNavigate = { currentScreen = it },
                            modifier = Modifier.padding(innerPadding)
                        )
                        "courts" -> CourtsScreen(
                            onNavigate = { currentScreen = it },
                            modifier = Modifier.padding(innerPadding)
                        )
                        "share" -> ShareProfileScreen(
                            onNavigate = { currentScreen = it },
                            modifier = Modifier.padding(innerPadding)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BottomNavItem(
    label: String,
    id: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    current: String,
    onClick: (String) -> Unit
) {
    NavigationBarItem(
        selected = current == id,
        onClick = { onClick(id) },
        icon = { Icon(imageVector = icon, contentDescription = label) },
        label = { Text(text = label) },
        alwaysShowLabel = true
    )
}