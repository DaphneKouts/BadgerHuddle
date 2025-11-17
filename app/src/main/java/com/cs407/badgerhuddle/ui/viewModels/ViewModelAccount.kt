package com.cs407.badgerhuddle.ui.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.auth.userProfileChangeRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AccountUiState(
    val isSignedIn: Boolean = false,
    val userEmail: String = "",
    val passwordInput: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val userName: String = "",
    val userBio: String = ""
)
class ViewModelAccount : ViewModel() {

    private val auth: FirebaseAuth = Firebase.auth
    private val _uiState = MutableStateFlow(AccountUiState())
    val uiState: StateFlow<AccountUiState> = _uiState.asStateFlow()

    private val authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
        val user = firebaseAuth.currentUser
        _uiState.update {
            it.copy(
                isSignedIn = user != null,
                isLoading = false,
                errorMessage = null,
                userName = user?.displayName ?: "",
                userEmail = user?.email ?: ""
            )
        }
    }

    init {
        auth.addAuthStateListener(authStateListener)
    }

    override fun onCleared() {
        super.onCleared()
        auth.removeAuthStateListener(authStateListener)
    }

    fun onEmailChange(newEmail: String) {
        _uiState.update { it.copy(userEmail = newEmail, errorMessage = null) }
    }

    fun onPasswordChange(newPassword: String) {
        _uiState.update { it.copy(passwordInput = newPassword, errorMessage = null) }
    }

    fun signInOrSignUp() {
        val email = _uiState.value.userEmail
        val password = _uiState.value.passwordInput
        if (email.isBlank() || password.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Email and password cannot be empty.") }
            return
        }

        if(!email.endsWith("@wisc.edu")){
            _uiState.update { it.copy(errorMessage = "Only wisc.edu emails are allowed.") }
            return
        }

        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                _uiState.update { it.copy(isLoading = false, userEmail = "", passwordInput = "") }
            } else {
                createAccount(email, password)
            }
        }
    }

    private fun createAccount(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                _uiState.update { it.copy(isLoading = false) }
                if (task.isSuccessful) {
                    _uiState.update { it.copy(userEmail = "", passwordInput = "") }
                } else {
                    _uiState.update { it.copy(errorMessage = task.exception?.message ?: "Authentication failed.") }
                }
            }
    }

    fun signOut() {
        auth.signOut()
        _uiState.update { it.copy(isSignedIn = false, userName = "", userEmail = "") }
    }

    fun updateName(newName: String) {
        val user = auth.currentUser ?: return
        val profileUpdates = userProfileChangeRequest { displayName = newName }
        user.updateProfile(profileUpdates).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                _uiState.update { it.copy(userName = newName) }
            }
        }
    }

    fun updateBio(newBio: String) {
        _uiState.update { it.copy(userBio = newBio) }
    }
}