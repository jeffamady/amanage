package com.amadydev.amanage.ui.task

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import androidx.core.view.get
import com.amadydev.amanage.R
import com.amadydev.amanage.databinding.ActivityTaskListBinding
import com.amadydev.amanage.ui.BaseActivity
import com.amadydev.amanage.utils.Constants
import com.amadydev.amanage.utils.Constants.DOCUMENT_ID
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
        getBoardDetails()
        setObservers()
    }

    private fun setObservers() {
        taskListViewModel.taskListState.observe(this) {
            when (it) {
                is TaskListViewModel.TaskListState.Loading ->
                    showProgressDialog(it.isLoading)
                is TaskListViewModel.TaskListState.Success -> {
                    setupActionBar(it.board.name)
                }
                TaskListViewModel.TaskListState.Error ->
                    showErrorSnackBar(binding.root, getString(R.string.task_error))
            }
        }
    }

    private fun getBoardDetails() {
        taskListViewModel.getBoardDetails()
    }

    private fun getDocumentId() {
        intent.getStringExtra(DOCUMENT_ID)?.let {
            taskListViewModel.getDocumentId(it)
        }
    }

    private fun setupActionBar(title: String) {
        with(binding.appBarLayout[0] as Toolbar) {
            setSupportActionBar(this)
            val actionBar = supportActionBar
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true)
                actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
                actionBar.title = title
            }
            setNavigationOnClickListener { onBackPressed() }
        }
    }
}