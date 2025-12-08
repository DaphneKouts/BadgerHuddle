package com.cs407.badgerhuddle.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.cs407.badgerhuddle.MainActivity
import com.cs407.badgerhuddle.ui.theme.RedUW
import com.cs407.badgerhuddle.ui.viewModels.ViewModelAccount

@Composable
fun ShareProfileScreen(
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier,
    accountVM: ViewModelAccount,
    bluetoothService: MyBluetoothService
) {
    val context = LocalContext.current
    val myUid by accountVM.uiState.collectAsState()
    var lastReceived by MainActivity.incomingUidState

    LaunchedEffect(lastReceived) {
        if (lastReceived.isNotEmpty()) {
            val friendName = accountVM.getProfileName(lastReceived)
            Toast.makeText(
                context,
                "Friend added: $friendName",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Share Profile", style = MaterialTheme.typography.titleLarge)

        Button(
            onClick = { bluetoothService.send(myUid.userEmail.toByteArray()) },
            colors = ButtonDefaults.buttonColors(containerColor = RedUW),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Send Request")
        }

        OutlinedButton(
            onClick = { onNavigate("profile") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Back to Profile")
        }
    }
}
