import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    val friendNames = remember { mutableStateMapOf<String, String>() }
    val scope = rememberCoroutineScope()

    // Fetch friends list
    LaunchedEffect(uid) {
        if (uid != null) {
            scope.launch {
                try {
                    val doc = db.collection("Profiles").document(uid).get().await()
                    val friendsList = doc.get("friends") as? List<String> ?: emptyList()
                    friends = friendsList

                    friendsList.forEach { fUid ->
                        val friendDoc = db.collection("Profiles").document(fUid).get().await()
                        val name = friendDoc.getString("name") ?: "Unknown"
                        friendNames[fUid] = name
                    }
                } catch (e: Exception) {
                    println("Error fetching friends: ${e.message}")
                }
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Text(
            text = "Friends",
            fontSize = 28.sp,
            color = RedUW,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (friends.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    "You have no friends yet",
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(friends) { friendUid ->
                    val displayName = friendNames[friendUid] ?: friendUid
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = displayName,
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }
    }
}
