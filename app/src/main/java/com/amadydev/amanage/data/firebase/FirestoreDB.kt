package com.amadydev.amanage.data.firebase

import androidx.lifecycle.ViewModel
import com.amadydev.amanage.data.model.User
import com.amadydev.amanage.ui.home.HomeViewModel
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

    fun loginUser(viewModel: ViewModel) {
        db.collection(USERS)
            .document(getCurrentUserId())
            .get()
            .addOnSuccessListener { document ->
                document.toObject(User::class.java)?.let { loggedUser ->
                    when (viewModel) {
                        is SignInViewModel -> {
                            viewModel.loginSuccess(true, loggedUser)
                        }
                        is HomeViewModel -> {
                            viewModel.updateNavUser(loggedUser)
                        }
                    }

                }
            }
            .addOnFailureListener {
                val vm = viewModel as SignInViewModel
                vm.loginSuccess(false)
            }
    }

    fun getCurrentUserId(): String {
        var currentUserId = ""
        Firebase.auth.currentUser?.let {
            currentUserId = it.uid
        }
        return currentUserId
    }
}