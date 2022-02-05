package com.amadydev.amanage.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.amadydev.amanage.data.firebase.FirestoreDB
import com.amadydev.amanage.data.model.Board
import com.amadydev.amanage.data.model.User
import com.amadydev.amanage.ui.board.CreateBoardViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val db: FirestoreDB) : ViewModel() {
    private val _homeState = MutableLiveData<HomeState>()
    val homeState: LiveData<HomeState> = _homeState

    fun updateNavUser(loggedUser: User, readBoardList: Boolean) {
        if (readBoardList) {
            getBoards()
        }
        _homeState.value = HomeState.NavUser(loggedUser)
        _homeState.value = HomeState.Loading(false)
    }

    fun getUser() {
        _homeState.value = HomeState.Loading(true)
        db.loadUserData(this, true)
    }

    fun getBoards() {
        db.getBoards(this)
    }

    fun updateBoards(boardList: List<Board>) {
        if (boardList.isNotEmpty())
            _homeState.value = HomeState.BoardList(boardList)
        _homeState.value = HomeState.Loading(false)
    }

    fun onFailure() {
        _homeState.value = HomeState.Error
        _homeState.value = HomeState.Loading(false)
    }

    sealed class HomeState {
        data class NavUser(val user: User) : HomeState()
        data class BoardList(val boardList: List<Board>) : HomeState()
        data class Loading(val isLoading: Boolean) : HomeState()
        object Error : HomeState()
    }
}