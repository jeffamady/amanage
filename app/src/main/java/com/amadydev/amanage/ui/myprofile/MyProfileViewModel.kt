package com.amadydev.amanage.ui.myprofile

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.amadydev.amanage.data.firebase.FirestoreDB
import com.amadydev.amanage.data.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MyProfileViewModel @Inject constructor()  : ViewModel() {
    private val _myProfileState = MutableLiveData<MyProfileState>()
    val myProfileState : LiveData<MyProfileState> = _myProfileState

    fun updateProfileUser(loggedUser: User) {
        _myProfileState.value = MyProfileState.Loading(false)
        _myProfileState.value = MyProfileState.ProfileUser(loggedUser)
    }

    fun getUser() {
        _myProfileState.value = MyProfileState.Loading(true)
        FirestoreDB().loadUserData(this)
    }

    fun setImageUri(uri: Uri) {
        _myProfileState.value = MyProfileState.ImageUri(uri)
    }

    sealed class MyProfileState {
        data class ProfileUser(val user: User): MyProfileState()
        data class Loading(val isLoading: Boolean): MyProfileState()
        data class ImageUri(val uri: Uri): MyProfileState()
    }
}