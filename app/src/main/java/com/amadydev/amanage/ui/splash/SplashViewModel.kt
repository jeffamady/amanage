package com.amadydev.amanage.ui.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.amadydev.amanage.firebase.FirestoreDB
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor() : ViewModel() {
    private val _splashState = MutableLiveData<SplashState>()
    val splashState: LiveData<SplashState> = _splashState

    fun isUserLoggedIn() {
        val currentUserId = FirestoreDB().getCurrentUserId()
        if (currentUserId.isNotEmpty()) {
            _splashState.value = SplashState.Logged(true)
        } else {
            _splashState.value = SplashState.NotLogged(true)
        }
    }

    sealed class SplashState {
        data class Logged(val isLogged: Boolean) : SplashState()
        data class NotLogged(val isNotLogged: Boolean) : SplashState()
    }
}