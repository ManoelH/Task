package com.manoelh.task.viewHolder

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.manoelh.task.R
import com.manoelh.task.constants.TaskConstants
import com.manoelh.task.entity.TaskEntity

class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

    private val taskDescription = itemView.findViewById<TextView>(R.id.textViewTaskDescriptionList)
    private val priority = itemView.findViewById<TextView>(R.id.textViewTaskPriorityList)
    private val dueDate = itemView.findViewById<TextView>(R.id.textViewTaskDueDateList)
    private val completed = itemView.findViewById<ImageView>(R.id.imageViewCompleted)

    fun bindTask(task: TaskEntity){
        taskDescription.text = task.description
        priority.text = "still in text..."
        dueDate.text = task.dueDate
        if (task.completed == TaskConstants.COMPLETE.YES)
            completed.setImageResource(R.drawable.ic_done)
    }
}