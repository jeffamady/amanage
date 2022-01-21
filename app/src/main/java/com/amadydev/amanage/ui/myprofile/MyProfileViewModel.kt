package com.amadydev.amanage.ui.myprofile

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
        _myProfileState.value = MyProfileState.ProfileUser(loggedUser)
    }

    fun getUser() {
        FirestoreDB().loadUserData(this)
    }

    sealed class MyProfileState {
        data class ProfileUser(val user: User): MyProfileState()
    }
}