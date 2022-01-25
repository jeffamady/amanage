package com.amadydev.amanage.data.firebase

import android.util.Log
import androidx.lifecycle.ViewModel
import com.amadydev.amanage.data.model.Board
import com.amadydev.amanage.data.model.User
import com.amadydev.amanage.ui.board.CreateBoardViewModel
import com.amadydev.amanage.ui.home.HomeViewModel
import com.amadydev.amanage.ui.myprofile.MyProfileViewModel
import com.amadydev.amanage.ui.signin.SignInViewModel
import com.amadydev.amanage.ui.signup.SignUpViewModel
import com.amadydev.amanage.utils.Constants.BOARDS
import com.amadydev.amanage.utils.Constants.USERS
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirestoreDB @Inject constructor() {
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

    fun loadUserData(viewModel: ViewModel) {
        db.collection(USERS)
            .document(getCurrentUserId())
            .get()
            .addOnSuccessListener { document ->
                document.toObject(User::class.java)?.let { loggedUser ->
                    when (viewModel) {
                        is SignInViewModel -> {
                            viewModel.loginSuccess(true)
                        }
                        is HomeViewModel -> {
                            viewModel.updateNavUser(loggedUser)
                        }
                        is MyProfileViewModel -> {
                            viewModel.updateProfileUser(loggedUser)
                        }
                        is CreateBoardViewModel -> {
                            viewModel.updateProfileUser(loggedUser)
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

    fun updateUserProfileData(
        viewModel: MyProfileViewModel,
        userHashMap: HashMap<String, Any>
    ) {
        db.collection(USERS)
            .document(getCurrentUserId())
            .update(userHashMap)
            .addOnSuccessListener {
                Log.w(viewModel.javaClass.simpleName, "Profile data updated with success")
                viewModel.profileUpdateSuccess(true)
            }
            .addOnFailureListener {
                viewModel.profileUpdateSuccess(false)
            }
    }

    // Create Boards
    fun createBoard(viewModel: CreateBoardViewModel, board: Board) {
        db.collection(BOARDS)
            .document(getCurrentUserId()).set(board, SetOptions.merge())
            .addOnSuccessListener {
                viewModel.boardCreatedSuccess(true)
            }
            .addOnFailureListener {
                it.message?.let { message -> viewModel.boardCreatedSuccess(false, message) }
            }
    }
}