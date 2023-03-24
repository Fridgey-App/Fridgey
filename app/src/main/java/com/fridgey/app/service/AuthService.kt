package com.fridgey.app.service

import android.content.Context
import com.fridgey.app.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthService @Inject constructor(private val auth: FirebaseAuth, resources: ResourcesProvider, @ApplicationContext appContext: Context) {
    val googleAuth = GoogleSignIn.getClient(appContext,
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(resources.getString(R.string.default_web_client_id))
            .requestId()
            .requestProfile()
            .build())

    val userDisplayName: String?
        get() = auth.currentUser?.displayName

    val currentUserId: String
        get() = auth.currentUser?.uid.orEmpty()

    val hasUser: Boolean
        get() = auth.currentUser != null

    fun addStateChangeListener(listener: (FirebaseAuth) -> Unit) {
        auth.addAuthStateListener(listener)
    }

    suspend fun signUp(username: String, email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password).await()
        if (hasUser) {
            val changeRequest = UserProfileChangeRequest.Builder().setDisplayName(username).build()
            auth.currentUser!!.updateProfile(changeRequest).await()
        }
    }

    suspend fun authenticateWithGoogle(credential: AuthCredential) {
        auth.signInWithCredential(credential).await()
    }

    suspend fun authenticateWithEmail(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password).await()
    }

    suspend fun sendRecoveryEmail(email: String) {
        auth.sendPasswordResetEmail(email).await()
    }

    fun signOut() {
        auth.signOut()
    }

}