package com.manoelh.task.viewHolder

import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.manoelh.task.R
import com.manoelh.task.constants.TaskConstants
import com.manoelh.task.entity.TaskEntity
import com.manoelh.task.interfaces.OnTaskListFragmentInteractionListener
import com.manoelh.task.repository.PriorityCache

class TaskViewHolder(itemView: View, val context: Context,  val onTaskListFragmentInteractionListener: OnTaskListFragmentInteractionListener)
    : RecyclerView.ViewHolder(itemView){

    private val taskDescription = itemView.findViewById<TextView>(R.id.textViewTaskDescriptionList)
    private val priority = itemView.findViewById<TextView>(R.id.textViewTaskPriorityList)
    private val dueDate = itemView.findViewById<TextView>(R.id.textViewTaskDueDateList)
    private val completed = itemView.findViewById<ImageView>(R.id.imageViewCompleted)

    fun bindTask(task: TaskEntity){
        taskDescription.text = task.description
        priority.text = PriorityCache.getCachePriorityDescription(task.priorityId)
        dueDate.text = task.dueDate

        if (task.completed == TaskConstants.COMPLETED.YES)
            completed.setImageResource(R.drawable.ic_done)

        taskDescription.setOnClickListener {
            onTaskListFragmentInteractionListener.onListClick(task.id)
        }

        taskDescription.setOnLongClickListener {
            AlertDialog.Builder(context)
                .setTitle(context.getString(R.string.title_alert_dialog_delete_task))
                .setMessage("Do you really sure that you want to delete the task ${task.description}?")
                .setIcon(R.drawable.ic_delete)
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Confirm") { _, _ ->
                    onTaskListFragmentInteractionListener.onDeleteClick(task)}.show()

            true
        }

        completed.setOnClickListener {
            onTaskListFragmentInteractionListener.onImageCompletedClick(task)
        }
    }
}