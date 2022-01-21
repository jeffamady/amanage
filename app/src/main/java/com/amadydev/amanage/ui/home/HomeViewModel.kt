package com.amadydev.amanage.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.amadydev.amanage.data.firebase.FirestoreDB
import com.amadydev.amanage.data.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor()  : ViewModel() {
    private val _homeState = MutableLiveData<HomeState>()
    val homeState : LiveData<HomeState> = _homeState

    fun updateNavUser(loggedUser: User) {
        _homeState.value = HomeState.NavUser(loggedUser)
    }

    fun getUser() {
        FirestoreDB().loadUserData(this)
    }

    sealed class HomeState {
        data class NavUser(val user: User): HomeState()
    }
}