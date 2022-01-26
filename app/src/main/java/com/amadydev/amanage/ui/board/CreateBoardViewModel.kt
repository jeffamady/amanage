package com.amadydev.amanage.ui.board

import android.net.Uri
import android.text.TextUtils
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.amadydev.amanage.R
import com.amadydev.amanage.data.firebase.FirestoreDB
import com.amadydev.amanage.data.model.Board
import com.amadydev.amanage.data.model.User
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CreateBoardViewModel @Inject constructor(
    private val db: FirestoreDB
) : ViewModel() {
    private val _createBoardState = MutableLiveData<CreateBoardState>()
    val createBoardState: LiveData<CreateBoardState> = _createBoardState

    private var mCurrentUser: User = User()
    private lateinit var mUri: Uri

    fun setImageUri(uri: Uri) {
        mUri = uri
        _createBoardState.value = CreateBoardState.ImageUri(uri)
    }

    fun createBoard(name: String, imageUrl: String) {
        _createBoardState.value = CreateBoardState.Loading(true)
        val assignedUserArrayList: ArrayList<String> = ArrayList()
        assignedUserArrayList.add(getCurrentUserId())

        val board = Board(
            name,
            imageUrl,
            mCurrentUser.name,
            assignedUserArrayList
        )

        db.createBoard(this, board)
    }

    private fun getCurrentUserId() =
        db.getCurrentUserId()

    fun updateUser(loggedUser: User) {
        _createBoardState.value = CreateBoardState.Loading(false)
        _createBoardState.value = CreateBoardState.CurrentUser(loggedUser)
        mCurrentUser = loggedUser
    }

    fun getUser() {
        _createBoardState.value = CreateBoardState.Loading(true)
        db.loadUserData(this)
    }


    fun boardCreatedSuccess(isCreated: Boolean, message: String = "") {
        _createBoardState.value = CreateBoardState.Loading(false)
        when {
            isCreated -> _createBoardState.value =
                CreateBoardState.Success(R.string.board_created)
            else -> {
                _createBoardState.value = CreateBoardState.Error(message)
            }
        }
    }

    fun uploadBoardImage(path: String) {
        _createBoardState.value = CreateBoardState.Loading(true)

        val sRef: StorageReference = Firebase.storage.reference.child(path)
        sRef.putFile(mUri).addOnSuccessListener { taskSnapshot ->
            taskSnapshot.metadata!!.reference!!.downloadUrl.addOnSuccessListener { uri ->
                _createBoardState.value = CreateBoardState.BoardImageUrl(uri.toString())
                Log.i(
                    "Downloadable Image Url",
                    uri.toString()
                )
            }
        }.addOnFailureListener {
            it.message?.let { message -> CreateBoardState.NonSuccess(message) }
        }
        _createBoardState.value = CreateBoardState.Loading(false)
    }

    fun validateBoardName(name: String) {
        when {
            TextUtils.isEmpty(name) -> {
                _createBoardState.value = CreateBoardState.NameError(R.string.invalid_name)
                _createBoardState.value = CreateBoardState.IsNameValid(false)
            }
            else -> {
                _createBoardState.value = CreateBoardState.IsNameValid(true)
            }
        }
    }

    sealed class CreateBoardState {
        data class Success(val resourceId: Int) : CreateBoardState()
        data class CurrentUser(val user: User) : CreateBoardState()
        data class ImageUri(val uri: Uri) : CreateBoardState()
        data class Loading(val isLoading: Boolean) : CreateBoardState()
        data class BoardImageUrl(val url: String) : CreateBoardState()
        data class NonSuccess(val message: String) : CreateBoardState()
        data class Error(val message: String) : CreateBoardState()
        data class NameError(val resourceId: Int) : CreateBoardState()
        data class IsNameValid(val isNameValid: Boolean) : CreateBoardState()
    }
}