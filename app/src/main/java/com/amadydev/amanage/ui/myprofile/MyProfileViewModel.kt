package com.amadydev.amanage.ui.myprofile

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.amadydev.amanage.R
import com.amadydev.amanage.data.firebase.FirestoreDB
import com.amadydev.amanage.data.model.User
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MyProfileViewModel @Inject constructor() : ViewModel() {
    private val _myProfileState = MutableLiveData<MyProfileState>()
    val myProfileState: LiveData<MyProfileState> = _myProfileState

    fun getUser() {
        _myProfileState.value = MyProfileState.Loading(true)
        FirestoreDB().loadUserData(this)
    }

    fun updateProfileUser(loggedUser: User) {
        _myProfileState.value = MyProfileState.Loading(false)
        _myProfileState.value = MyProfileState.ProfileUser(loggedUser)
    }

    fun setImageUri(uri: Uri) {
        _myProfileState.value = MyProfileState.ImageUri(uri)
    }

    fun uploadUserImage(path: String, uri: Uri) {
        _myProfileState.value = MyProfileState.Loading(true)

        val sRef: StorageReference = Firebase.storage.reference.child(path)
        sRef.putFile(uri).addOnSuccessListener { taskSnapshot ->
            taskSnapshot.metadata!!.reference!!.downloadUrl.addOnSuccessListener { uri ->
                _myProfileState.value = MyProfileState.ProfileImageUrl(uri.toString())
                Log.i(
                    "Downloadable Image Url",
                    uri.toString()
                )
                _myProfileState.value =
                    MyProfileState.Success(R.string.success)
                _myProfileState.value =
                    MyProfileState.ImageChanged(false)
                // Todo updateUserProfileData
            }
        }.addOnFailureListener {
            it.message?.let { message -> MyProfileState.Error(message) }
        }
        _myProfileState.value = MyProfileState.Loading(false)
    }

    fun changeImage() {
        _myProfileState.value = MyProfileState.ImageChanged(true)
    }

    sealed class MyProfileState {
        data class Success(val resourceId: Int) : MyProfileState()
        data class ProfileUser(val user: User) : MyProfileState()
        data class Loading(val isLoading: Boolean) : MyProfileState()
        data class ImageChanged(val isChanged: Boolean) : MyProfileState()
        data class ImageUri(val uri: Uri) : MyProfileState()
        data class ProfileImageUrl(val url: String) : MyProfileState()
        data class Error(val message: String) : MyProfileState()
    }
}