import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.cs407.badgerhuddle.ui.theme.RedUW
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class HomeGame(
    val sport: String = "",
    val date: String = "",
    val time: String = "",
    val courtName: String = "",
    val players: Int = 0,
    val maxPlayers: Int = 0
)

@Composable
fun HomeScreen(onNavigate: (String) -> Unit, modifier: Modifier = Modifier) {

    val db = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()
    val uid = auth.currentUser?.uid

    var games by remember { mutableStateOf(listOf<HomeGame>()) }
    var friends by remember { mutableStateOf(listOf<String>()) }

    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    // Load games
    LaunchedEffect(Unit) {
        db.collection("Courts").addSnapshotListener { snapshot, _ ->
            if (snapshot != null) {
                games = snapshot.documents.mapNotNull { doc ->
                    val data = doc.data ?: return@mapNotNull null
                    HomeGame(
                        sport = data["Sport"]?.toString() ?: "",
                        date = data["Date"]?.toString() ?: "",
                        time = data["Time"]?.toString() ?: "",
                        courtName = data["Court"]?.toString() ?: "",
                        players = (data["NumCheckedIn"] as? Long)?.toInt() ?: 0,
                        maxPlayers = (data["MaxCheckIn"] as? Long)?.toInt() ?: 0
                    )
                }
            }
        }
    }

    // Load friends
    LaunchedEffect(uid) {
        if (uid != null) {
            scope.launch {
                try {
                    val doc = db.collection("Profiles").document(uid).get().await()
                    friends = doc.get("friends") as? List<String> ?: emptyList()
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
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        item {
            Text(
                "Welcome Back!",
                color = RedUW,
                style = MaterialTheme.typography.headlineSmall
            )
            Text("Find your next game or check in nearby")
        }

        // Friends section
        item {
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text("Recently Added Friends", color = RedUW, style = MaterialTheme.typography.titleMedium)

                    if (friends.isEmpty()) {
                        Text(
                            "Add friends and they will pop up here",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    } else {
                        friends.forEach { friend ->
                            Text(
                                friend,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        }

        // Live Status section
        item {
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "Live Status",
                        color = RedUW,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Button(
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW)
                            intent.data = Uri.parse("https://www.connect2mycloud.com/Widgets/Data/locationCount?type=bar&key=7938fc89-a15c-492d-9566-12c961bc1f27")
                            context.startActivity(intent)
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("View Live Count")
                    }
                }
            }
        }

        // Upcoming games section
        item {
            Text("Upcoming Games", style = MaterialTheme.typography.titleMedium)
            games.forEach { game ->
                Card(
                    onClick = { onNavigate("games") },
                    modifier = Modifier
                        .padding(vertical = 4.dp)
                        .fillMaxWidth()
                ) {
                    Column(Modifier.padding(12.dp)) {
                        Text(game.sport)
                        Text("${game.date}  •  ${game.time}")
                        Text("${game.players}/${game.maxPlayers} players at ${game.courtName}")
                    }
                }
            }
        }
    }
}
