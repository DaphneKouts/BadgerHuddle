package com.cs407.badgerhuddle.ui.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import com.google.firebase.firestore.FieldValue


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
    private val db = FirebaseFirestore.getInstance()

    private val _uiState = MutableStateFlow(AccountUiState())
    val uiState: StateFlow<AccountUiState> = _uiState.asStateFlow()

    private val authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
        val user = firebaseAuth.currentUser
        if (user != null) {
            loadProfile(user.uid)
        }

        _uiState.update {
            it.copy(
                isSignedIn = user != null,
                isLoading = false,
                errorMessage = null,
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

        if (!email.endsWith("@wisc.edu")) {
            _uiState.update { it.copy(errorMessage = "Only wisc.edu emails are allowed.") }
            return
        }

        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                _uiState.update { it.copy(isLoading = false, passwordInput = "") }
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
                    val uid = auth.currentUser!!.uid

                    // Create Firestore profile
                    val profile = mapOf(
                        "id" to uid,
                        "name" to "",
                        "bio" to "",
                        "friends" to emptyList<String>()
                    )

                    db.collection("Profiles").document(uid).set(profile)

                    _uiState.update { it.copy(userEmail = "", passwordInput = "") }
                } else {
                    _uiState.update {
                        it.copy(errorMessage = task.exception?.message ?: "Account creation failed.")
                    }
                }
            }
    }

    private fun loadProfile(uid: String) {
        viewModelScope.launch {
            try {
                val snap = db.collection("Profiles").document(uid).get().await()
                if (snap.exists()) {
                    val name = snap.getString("name") ?: ""
                    val bio = snap.getString("bio") ?: ""

                    _uiState.update {
                        it.copy(
                            userName = name,
                            userBio = bio
                        )
                    }
                }
            } catch (_: Exception) {}
        }
    }

    fun updateName(newName: String) {
        val user = auth.currentUser ?: return

        // update Firebase Auth displayName
        val profileUpdates = userProfileChangeRequest { displayName = newName }
        user.updateProfile(profileUpdates)

        // update Firestore profile
        db.collection("Profiles").document(user.uid)
            .update("name", newName)

        _uiState.update { it.copy(userName = newName) }
    }

    fun updateBio(newBio: String) {
        val user = auth.currentUser ?: return

        db.collection("Profiles").document(user.uid)
            .update("bio", newBio)

        _uiState.update { it.copy(userBio = newBio) }
    }

    fun signOut() {
        auth.signOut()
        _uiState.update { AccountUiState() }
    }

    fun addFakeFriend() {
        val uid = auth.currentUser?.uid ?: return

        val newFriend = "fake friend ${System.currentTimeMillis()}"
        db.collection("Profiles")
            .document(uid)
            .update("friends", FieldValue.arrayUnion(newFriend))
    }

    fun addFriend(uidToAdd: String) {
        val uid = auth.currentUser?.uid ?: return

        FirebaseFirestore.getInstance()
            .collection("Profiles")
            .document(uid)
            .update("friends", FieldValue.arrayUnion(uidToAdd))
    }

    suspend fun getProfileName(uid: String): String {
        return try {
            val snap = db.collection("Profiles").document(uid).get().await()
            snap.getString("name") ?: uid
        } catch (_: Exception) {
            uid
        }
    }

}