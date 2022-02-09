package com.amadydev.amanage.ui.task

import android.content.Context
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.amadydev.amanage.R
import com.amadydev.amanage.data.model.Task
import com.amadydev.amanage.databinding.ItemTaskBinding
import com.amadydev.amanage.utils.Constants.showToast

class TaskListAdapter(
    private val context: Context,
    private val taskList: List<Task>
) : RecyclerView.Adapter<TaskListAdapter.TaskViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder =
        TaskViewHolder(
            ItemTaskBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            parent
        )

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) =
        holder.render(position)

    override fun getItemCount() =
        taskList.size

    inner class TaskViewHolder(
        private val binding: ItemTaskBinding,
        private val parent: ViewGroup
    ) :
        RecyclerView.ViewHolder(binding.root) {

        fun render(position: Int) {
            val task = taskList[position]
            with(binding) {
                LinearLayout.LayoutParams(
                    (parent.width * 0.75).toInt(),
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(15.toDp().toPx(), 0, 40.toDp().toPx(), 0)
                    root.layoutParams = this
                }

                if (position == taskList.size - 1) {
                    tvAddTaskList.isVisible = true
                    llTaskItem.isVisible = false
                } else {
                    tvAddTaskList.isVisible = false
                    llTaskItem.isVisible = true
                }

                tvTaskListTitle.text = task.title

                tvAddTaskList.setOnClickListener {
                    tvAddTaskList.isVisible = false
                    cvAddTaskListName.isVisible = true
                }

                ibCloseListName.setOnClickListener {
                    tvAddTaskList.isVisible = true
                    cvAddTaskListName.isVisible = false
                }

                // Create Task
                ibDoneListName.setOnClickListener {
                    val listName = etTaskListName.text.toString()

                    if (listName.isNotEmpty()) {
                        if (context is TaskListActivity) {
                            context.createTaskList(listName)
                        }
                    } else
                        showToast(context, context.getString(R.string.list_name_error))
                }

                ibEditListName.setOnClickListener {
                    etEditTaskListName.setText(task.title)
                    llTitleView.isVisible = false
                    cvEditTaskListName.isVisible = true
                }

                ibCloseEditableView.setOnClickListener {
                    llTitleView.isVisible = true
                    cvEditTaskListName.isVisible = false
                }

                // Edit Task
                ibDoneEditListName.setOnClickListener {
                    val listName = etEditTaskListName.text.toString()

                    if (listName.isNotEmpty()) {
                        if (listName != task.title) {
                            if (context is TaskListActivity)
                                context.updateTaskList(position, listName, task)
                        } else
                            showToast(context, context.getString(R.string.no_new_change))
                    } else
                        showToast(context, context.getString(R.string.list_name_error))
                }

                ibDeleteList.setOnClickListener {
                    if (context is TaskListActivity)
                        context.showDialogForDeleteList(
                            task.title
                        ) { context.deleteTaskList(position) }
                }

                tvAddCard.setOnClickListener {
                    tvAddCard.isVisible = false
                    cvAddCard.isVisible = true
                }

                ibCloseCardName.setOnClickListener {
                    tvAddCard.isVisible = true
                    cvAddCard.isVisible = false
                }

                // Create Card
                ibDoneCardName.setOnClickListener {
                    val cardName = etCardName.text.toString()

                    if (cardName.isNotEmpty()) {
                        if (context is TaskListActivity)
                            context.createCard(cardName, position)
                    } else
                        showToast(context, context.getString(R.string.card_name_error))
                }

            }
        }

        private fun Int.toDp(): Int =
            (this / Resources.getSystem().displayMetrics.density).toInt()

        private fun Int.toPx(): Int =
            (this * Resources.getSystem().displayMetrics.density).toInt()
    }
}