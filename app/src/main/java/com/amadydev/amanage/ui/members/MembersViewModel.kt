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

    private lateinit var mBoard: Board
    private var mAssignedMembersList = mutableListOf<User>()

    fun getBoardDetailsAngAssignedMembersList(board: Board) {
        _membersState.value = MembersState.Loading(true)
        _membersState.value = MembersState.Success(board)
        mBoard = board
        // Call to Firebase DB
        db.getAssignedMembersList(this, board.assignedTo)
    }

    fun onGetAssignedMembersListSuccess(usersList: List<User>) {
        _membersState.value = MembersState.Loading(false)
        _membersState.value = MembersState.Users(usersList)
        usersList.forEach(mAssignedMembersList::add)
    }

    fun onFailure() {
        _membersState.value = MembersState.Loading(false)
        _membersState.value = MembersState.Error
    }

    fun memberDetails(user: User) {
        _membersState.value = MembersState.Loading(false)
        mBoard.assignedTo.add(user.id)
        _membersState.value = MembersState.Loading(true)
        db.assignMemberToBoard(this, mBoard, user)
    }

    fun getMemberDetails(email: String) {
        _membersState.value = MembersState.Loading(true)
        db.getMemberDetails(this, email)
    }

    fun memberAssignSuccess(user: User) {
        _membersState.value = MembersState.Loading(false)
        mAssignedMembersList.add(user)
        mAssignedMembersList.let {
            val userList: List<User> = it
            _membersState.value = MembersState.Users(userList)
            _membersState.value = MembersState.AnyChangesMade
        }
    }

    sealed class MembersState {
        data class Success(val board: Board) : MembersState()
        data class Users(val usersList: List<User>) : MembersState()
        data class Loading(val isLoading: Boolean) : MembersState()
        object AnyChangesMade : MembersState()
        object Error : MembersState()
    }
}