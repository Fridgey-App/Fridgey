package com.alp.fridgeyapp

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseAuthManager @Inject constructor() {

    private val _firebaseAuth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    fun getFirebaseAuth(): FirebaseAuth {
        return _firebaseAuth
    }

    fun getCurrentUser(): FirebaseUser? {
        return _firebaseAuth.currentUser
    }

    fun signOut() {
        _firebaseAuth.signOut()
    }
}