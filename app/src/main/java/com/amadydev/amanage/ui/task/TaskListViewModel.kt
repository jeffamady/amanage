package com.amadydev.amanage.ui.task

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.amadydev.amanage.data.firebase.FirestoreDB
import com.amadydev.amanage.data.model.Board
import com.amadydev.amanage.ui.board.CreateBoardViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import java.math.BigInteger
import javax.inject.Inject

@HiltViewModel
class TaskListViewModel @Inject constructor(private val db: FirestoreDB) : ViewModel() {
    private val _taskListState = MutableLiveData<TaskListState>()
    val taskListState: LiveData<TaskListState> = _taskListState

    private var mBoardDocumentId = ""

    fun getDocumentId(documentId: String) {
        mBoardDocumentId = documentId
    }

    fun getBoardDetails(){
        _taskListState.value = TaskListState.Loading(true)
        db.getBoardDetails(this, mBoardDocumentId)
    }

    fun getBoardDetailsSuccess(board: Board){
        _taskListState.value = TaskListState.Loading(false)
        _taskListState.value = TaskListState.Success(board)
    }

    fun onFailure() {
        _taskListState.value = TaskListState.Error
    }

    sealed class TaskListState {
        data class Success(val board: Board) : TaskListState()
        data class Loading(val isLoading: Boolean) : TaskListState()
        object Error : TaskListState()
    }

}