package com.amadydev.amanage.ui.task

import android.os.Bundle
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.amadydev.amanage.R
import com.amadydev.amanage.data.model.Board
import com.amadydev.amanage.data.model.Task
import com.amadydev.amanage.databinding.ActivityTaskListBinding
import com.amadydev.amanage.ui.BaseActivity
import com.amadydev.amanage.utils.Constants.DOCUMENT_ID
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TaskListActivity : BaseActivity() {
    private lateinit var binding: ActivityTaskListBinding
    private val taskListViewModel: TaskListViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTaskListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getDocumentId()
        getTaskString()
        getBoardDetails()
        setObservers()
    }

    private fun setObservers() {
        taskListViewModel.taskListState.observe(this) {
            when (it) {
                is TaskListViewModel.TaskListState.Loading ->
                    showProgressDialog(it.isLoading)
                is TaskListViewModel.TaskListState.Success -> {
                    setupUI(it.board)
                }
                TaskListViewModel.TaskListState.Error ->
                    showErrorSnackBar(binding.root, getString(R.string.task_error))
            }
        }
    }

    private fun setupUI(board: Board) {
        setupActionBar(board.name, board.image)
        binding.rvTaskList.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.rvTaskList.setHasFixedSize(true)

        val adapter = TaskListAdapter(
            this,
            board.taskList.toList()
        )

        binding.rvTaskList.adapter = adapter
    }

    private fun getBoardDetails() {
        taskListViewModel.getBoardDetails()
    }

    private fun getDocumentId() {
        intent.getStringExtra(DOCUMENT_ID)?.let {
            taskListViewModel.getDocumentId(it)
        }
    }

    private fun setupActionBar(title: String, image: String) {
        with(binding.toolbar) {
            setSupportActionBar(this)
            val actionBar = supportActionBar
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true)
                actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
                actionBar.title = title
            }

            Glide.with(this)
                .load(image)
                .error(R.drawable.ic_task_image)
                .centerCrop()
                .into(binding.ivToolbar)

            setNavigationOnClickListener { onBackPressed() }
        }
    }

    private fun getTaskString() =
        taskListViewModel.getTaskString(getString(R.string.action_add_list))


    fun createTaskList(taskListName: String) =
        taskListViewModel.createTaskList(taskListName)

    fun updateTaskList(position: Int, listName: String, task: Task) =
        taskListViewModel.updateTaskList(position, listName, task)

    fun deleteTaskList(position: Int) =
        taskListViewModel.deleteTaskList(position)

    fun createCard(cardName: String, position: Int) =
        taskListViewModel.createCard(cardName, position)
}