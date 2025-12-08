package com.cs407.badgerhuddle

import FriendsScreen
import GamesScreen
import HomeScreen
import android.bluetooth.BluetoothSocket
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.widget.Toast
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cs407.badgerhuddle.ui.screens.*
import com.cs407.badgerhuddle.ui.theme.BadgerHuddleTheme
import com.cs407.badgerhuddle.ui.theme.RedUW
import com.cs407.badgerhuddle.ui.viewModels.ViewModelAccount
import com.google.firebase.Firebase
import com.google.firebase.initialize

class MainActivity(
    private val preConnectedSocket: BluetoothSocket? = null
) : ComponentActivity() {

    lateinit var bluetoothService: MyBluetoothService

    companion object {
        val incomingUidState = mutableStateOf("")
        var latestViewModel: ViewModelAccount? = null
    }

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Firebase.initialize(this)

        val context = this

        val handler = object : Handler(Looper.getMainLooper()) {
            override fun handleMessage(msg: Message) {
                when (msg.what) {
                    MESSAGE_READ -> {
                        val bytes = msg.obj as ByteArray
                        val receivedUid = String(bytes)
                        incomingUidState.value = receivedUid

                        val accountVM = latestViewModel ?: return
                        accountVM.addFriend(receivedUid)

                        Toast.makeText(context, "Friend added: $receivedUid", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        bluetoothService = MyBluetoothService(handler)

        preConnectedSocket?.let { socket ->
            bluetoothService.setSocket(socket)
        }

        setContent {
            BadgerHuddleTheme {
                var currentScreen by remember { mutableStateOf("home") }

                val accountViewModel: ViewModelAccount = viewModel()
                latestViewModel = accountViewModel

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
                        "profile" -> ProfileScreen(
                            { currentScreen = it },
                            Modifier.padding(innerPadding),
                            viewModel = accountViewModel
                        )
                        "friends" -> FriendsScreen({ currentScreen = it }, Modifier.padding(innerPadding))
                        "share" -> ShareProfileScreen(
                            onNavigate = { currentScreen = it },
                            modifier = Modifier.padding(innerPadding),
                            accountVM = accountViewModel,
                            bluetoothService = bluetoothService
                        )
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
