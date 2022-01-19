package com.amadydev.amanage.firebase

import com.amadydev.amanage.firebase.model.User
import com.amadydev.amanage.ui.signin.SignInViewModel
import com.amadydev.amanage.ui.signup.SignUpViewModel
import com.amadydev.amanage.utils.Constants.USERS
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class FirestoreDB {
    private val db = Firebase.firestore

    fun registerUser(viewModel: SignUpViewModel, user: User) {
        db.collection(USERS)
            .document(getCurrentUserId()).set(user, SetOptions.merge())
            .addOnSuccessListener {
                viewModel.userRegisteredSuccess(true)
            }
            .addOnFailureListener {
                it.message?.let { message -> viewModel.userRegisteredSuccess(false, message) }
            }
    }

    fun loginUser(viewModel: SignInViewModel) {
        db.collection(USERS)
            .document(getCurrentUserId())
            .get()
            .addOnSuccessListener { document ->
                document.toObject(User::class.java)?.let { loggedUser ->
                    viewModel.loginSuccess(true, loggedUser)
                }
            }
            .addOnFailureListener {
                viewModel.loginSuccess(false)
            }
    }

    private fun getCurrentUserId(): String =
        Firebase.auth.currentUser!!.uid
}