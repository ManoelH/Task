package com.manoelh.task.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.manoelh.task.R
import com.manoelh.task.entity.TaskEntity
import com.manoelh.task.viewHolder.TaskViewHolder

class TaskListAdapter(val taskList: List<TaskEntity>): RecyclerView.Adapter<TaskViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val inflate = LayoutInflater.from(parent.context)
        val view = inflate.inflate(R.layout.tasks_row_list , parent, false)
        return TaskViewHolder(view)
    }

    override fun getItemCount(): Int {
        return taskList.count()
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bindTask(taskList[position])
    }
}