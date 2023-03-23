package com.alp.fridgeyapp

import android.content.Context
import android.util.Log
import android.view.View
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.common.io.Resources
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

class FirebaseAuthStateListener : FirebaseAuth.AuthStateListener {
    override fun onAuthStateChanged(auth: FirebaseAuth) {
        Log.d("[AUTH]", "Auth state has changed, current user: ${auth.currentUser?.email}")
    }
}

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