package com.manoelh.task.viewHolder

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.manoelh.task.R
import com.manoelh.task.entity.TaskEntity

class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

    fun bindTask(task: TaskEntity){
        var taskDescription = itemView.findViewById<TextView>(R.id.textViewTaskDescriptionList)
        taskDescription.text = task.description
    }
}