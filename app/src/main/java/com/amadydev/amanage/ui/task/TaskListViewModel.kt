package com.amadydev.amanage.ui.task

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.amadydev.amanage.data.firebase.FirestoreDB
import com.amadydev.amanage.data.model.Board
import com.amadydev.amanage.data.model.Card
import com.amadydev.amanage.data.model.Task
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TaskListViewModel @Inject constructor(private val db: FirestoreDB) : ViewModel() {
    private val _taskListState = MutableLiveData<TaskListState>()
    val taskListState: LiveData<TaskListState> = _taskListState

    private lateinit var mBoardDocumentId: String
    private lateinit var mAddTaskList: String
    private lateinit var mBoard: Board

    fun getDocumentId(documentId: String) {
        mBoardDocumentId = documentId
    }

    fun getBoardDetails() {
        _taskListState.value = TaskListState.Loading(true)
        db.getBoardDetails(this, mBoardDocumentId)
    }

    fun getBoardDetailsSuccess(board: Board) {
        _taskListState.value = TaskListState.Loading(false)
        val addTaskList = Task(mAddTaskList)
        board.taskList.add(addTaskList)
        mBoard = board
        _taskListState.value = TaskListState.Success(board)
    }

    fun addUpdateTaskListSuccess() {
        _taskListState.value = TaskListState.Loading(false)

        db.getBoardDetails(this, mBoardDocumentId)
    }

    fun createTaskList(taskListName: String) {
        val task = Task(taskListName, db.getCurrentUserId())

        mBoard.taskList.add(0, task)
        mBoard.taskList.removeAt(mBoard.taskList.size - 1)

        _taskListState.value = TaskListState.Loading(true)
        db.addUpdateTaskList(this, mBoard)
    }

    fun updateTaskList(position: Int, listName: String, task: Task) {
        val newTask = Task(listName, task.createdBy, task.cardsList)

        mBoard.taskList[position] = newTask
        mBoard.taskList.removeAt(mBoard.taskList.size - 1)

        _taskListState.value = TaskListState.Loading(true)
        db.addUpdateTaskList(this, mBoard)
    }

    fun deleteTaskList(position: Int) {
        mBoard.taskList.removeAt(position)
        mBoard.taskList.removeAt(mBoard.taskList.size - 1)

        _taskListState.value = TaskListState.Loading(true)
        db.addUpdateTaskList(this, mBoard)
    }

    // Create Card in Task
    fun createCard(cardName: String, position: Int) {
        mBoard.taskList.removeAt(mBoard.taskList.size - 1)
        val currentUserId = db.getCurrentUserId()
        val cardAssignedUserList = ArrayList<String>()
        cardAssignedUserList.add(currentUserId)

        val card = Card(cardName, currentUserId, cardAssignedUserList)

        val cardsList = mBoard.taskList[position].cardsList
        cardsList.add(card)

        val task = Task(
            mBoard.taskList[position].title,
            mBoard.taskList[position].createdBy,
            cardsList
        )

        mBoard.taskList[position] = task

        _taskListState.value = TaskListState.Loading(true)
        db.addUpdateTaskList(this, mBoard)
    }

    fun getTaskString(taskString: String) {
        mAddTaskList = taskString
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