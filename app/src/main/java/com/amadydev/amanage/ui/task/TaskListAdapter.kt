package com.amadydev.amanage.ui.task

import android.content.Context
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.core.view.setMargins
import androidx.recyclerview.widget.RecyclerView
import com.amadydev.amanage.data.model.Task
import com.amadydev.amanage.databinding.ItemTaskBinding

class TaskListAdapter(
    private val context: Context,
    private val taskList: List<Task>,
    private val onTaskClickListener: OnTaskClickListener
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

    inner class TaskViewHolder(private val binding: ItemTaskBinding, private val parent: ViewGroup) :
        RecyclerView.ViewHolder(binding.root) {

        fun render(position: Int) {
            val task = taskList[position]
            with(binding) {
                LinearLayout.LayoutParams(
                    (parent.width * 0.75 ).toInt(),
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

                root.setOnClickListener {
                    onTaskClickListener.onTaskClicked(task)
                }
            }
        }
    }

    interface OnTaskClickListener {
        fun onTaskClicked(task: Task)
    }

    private fun Int.toDp(): Int =
        (this / Resources.getSystem().displayMetrics.density).toInt()

    private fun Int.toPx(): Int =
        (this * Resources.getSystem().displayMetrics.density).toInt()
}