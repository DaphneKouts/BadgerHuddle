package com.cs407.badgerhuddle.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cs407.badgerhuddle.ui.theme.RedUW
import com.cs407.badgerhuddle.ui.viewModels.ViewModelAccount

/**
 * If you are signed in, goes to the profile screen, otherwise, prompts to sign-up/login
 */
@Composable
fun ProfileScreen(
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ViewModelAccount = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    if (uiState.isSignedIn) {
        ProfileEditor(onNavigate = onNavigate, viewModel = viewModel, modifier = modifier)
    } else {
        AuthenticationCard(viewModel = viewModel, modifier = modifier)
    }
}

@Composable
fun ErrorText(error: String?, modifier: Modifier = Modifier) {
    if (error != null)
        Text(text = error, color = Color.Red, textAlign = TextAlign.Center)
}

/**
 * Not signed in, log/sign in
 */
@Composable
fun AuthenticationCard(
    viewModel: ViewModelAccount,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()


    Box(
        modifier = modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ){
        Card(
            modifier = modifier.fillMaxSize(),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ){
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "BadgerHuddle Login",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold),
                    color = RedUW
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = uiState.userEmail,
                    onValueChange = viewModel::onEmailChange,
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    singleLine = true
                )

                OutlinedTextField(
                    value = uiState.passwordInput,
                    onValueChange = viewModel::onPasswordChange,
                    label = { Text("Password") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    visualTransformation = PasswordVisualTransformation(), // Hide password text
                    singleLine = true
                )

                ErrorText(uiState.errorMessage, modifier = Modifier.fillMaxWidth())


                Button(
                    onClick = { viewModel.signInOrSignUp() },
                    colors = ButtonDefaults.buttonColors(containerColor = RedUW),
                    modifier = Modifier.fillMaxWidth().height(50.dp)
                ) {
                    Text("Sign In/Sign Up", style = MaterialTheme.typography.titleMedium)
                }
            }
        }
    }
}


/**
 * If you are signed in, normal profile screen
 */
@Composable
fun ProfileEditor(onNavigate: (String) -> Unit, modifier: Modifier = Modifier, viewModel: ViewModelAccount) {
    val uiState by viewModel.uiState.collectAsState()
    var name by remember { mutableStateOf(uiState.userName.ifBlank {"tap to edit name"}) }
    var bio by remember { mutableStateOf(uiState.userBio.ifBlank {"tap to edit bio"}) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Profile", style = MaterialTheme.typography.titleLarge)

        OutlinedTextField(
            name,
            {
                name = it
                viewModel.updateName(it)},
            label = { Text("Name") },
            modifier = Modifier.fillMaxWidth())

        OutlinedTextField(
            bio,
            {
                bio = it
                viewModel.updateBio(it)},
            label = { Text("Bio") },
            modifier = Modifier.fillMaxWidth())

        Button(
            onClick = { onNavigate("share") },
            colors = ButtonDefaults.buttonColors(containerColor = RedUW),
            modifier = Modifier.fillMaxWidth()
        ) { Text("Share Profile") }

        Button(
            onClick = viewModel::signOut,
            colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray, contentColor = Color.Black),
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            Text("Sign Out", style = MaterialTheme.typography.titleMedium)
        }
    }
}