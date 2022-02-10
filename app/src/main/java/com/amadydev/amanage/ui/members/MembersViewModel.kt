package com.amadydev.amanage.ui.members

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.amadydev.amanage.data.firebase.FirestoreDB
import com.amadydev.amanage.data.model.Board
import com.amadydev.amanage.data.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MembersViewModel @Inject constructor(private val db: FirestoreDB) : ViewModel() {
    private val _membersState = MutableLiveData<MembersState>()
    val membersState: LiveData<MembersState> = _membersState

    fun getBoardDetailsAngAssignedMembersList(board: Board) {
        _membersState.value = MembersState.Loading(true)
        _membersState.value = MembersState.Success(board)
        // Call to Firebase DB
        db.getAssignedMembersList(this, board.assignedTo)
    }

    fun onGetAssignedMembersListSuccess(usersList: List<User>) {
        _membersState.value = MembersState.Loading(false)
        _membersState.value = MembersState.Users(usersList)
    }

    fun onFailure() {
        _membersState.value = MembersState.Loading(false)
        _membersState.value = MembersState.Error
    }

    sealed class MembersState {
        data class Success(val board: Board) : MembersState()
        data class Users(val usersList: List<User>) : MembersState()
        data class Loading(val isLoading: Boolean) : MembersState()
        object Error : MembersState()
    }
}