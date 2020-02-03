package com.manoelh.task.views.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.manoelh.task.R
import com.manoelh.task.adapter.TaskListAdapter
import com.manoelh.task.business.TaskBusiness
import com.manoelh.task.constants.TaskConstants
import com.manoelh.task.entity.TaskEntity
import com.manoelh.task.interfaces.OnTaskListFragmentInteractionListener
import com.manoelh.task.repository.TaskRepository
import com.manoelh.task.util.SecurityPreferences
import com.manoelh.task.views.activity.TaskFormActivity


class TaskListFragment : Fragment(), View.OnClickListener {

    private lateinit var mContext: Context
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mTaskBusiness: TaskBusiness
    private var taskFilterCompleted = TaskConstants.COMPLETED.NOT
    private lateinit var mTaskListFragmentInteractionListener: OnTaskListFragmentInteractionListener
    private lateinit var mSecurityPreferences: SecurityPreferences
    private lateinit var mTaskRepository: TaskRepository

    companion object {
        fun newInstance(taskFilterCompleted: Boolean) = TaskListFragment().apply {
            arguments = Bundle().apply {
                putBoolean(TaskConstants.KEY.TASK_FILTER, taskFilterCompleted)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            taskFilterCompleted = it.getBoolean(TaskConstants.KEY.TASK_FILTER)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_task_list, container, false)
        view.findViewById<FloatingActionButton>(R.id.floatButtonAddTask).setOnClickListener(this)
        mContext = view.context
        intanceMyObjectsWithContext()
        mRecyclerView = view.findViewById(R.id.recyclerViewTasks)
        mTaskListFragmentInteractionListener =  object : OnTaskListFragmentInteractionListener{
            override fun onListClick(taskId: String) {
                val bundle = Bundle()
                bundle.putString(TaskConstants.KEY.TASK_ID, taskId)
                openTaskFormActivity(bundle)
            }

            override fun onDeleteClick(task: TaskEntity) {
                observerDeleteTask(task.id)
            }

            override fun onImageCompletedClick(task: TaskEntity) {
                if (task.completed == TaskConstants.COMPLETED.YES)
                    task.completed = TaskConstants.COMPLETED.NOT
                else
                    task.completed = TaskConstants.COMPLETED.YES
                observerUpdateTaskStatus(task.completed, task.id)
            }
        }
        return view
    }

    override fun onResume() {
        super.onResume()
        observerListTasks()
    }

    private fun intanceMyObjectsWithContext() {
        mTaskBusiness = TaskBusiness(mContext)
        mSecurityPreferences = SecurityPreferences(mContext)
        mTaskRepository = TaskRepository(mContext)
    }

    private fun observerDeleteTask(id: String){
        mTaskRepository.deleteTask(id).observe(this, Observer { idTaskDeleted ->
            if (idTaskDeleted != null)
                observerListTasks()
        })
    }

    private fun observerListTasks(){
        mTaskRepository.listTasks(taskFilterCompleted).observe(this, Observer { tasks ->
            if (tasks != null)
                loadRecyclerView(tasks)
        })
    }

    private fun observerUpdateTaskStatus(taskCompleted: Boolean, taskId: String){
        mTaskRepository.updateTaskStatus(taskCompleted, taskId)
            .observe(this, Observer { idTaskUpdated ->
            if (idTaskUpdated != null)
                observerListTasks()
        })
    }

    private fun loadRecyclerView(tasksList: List<TaskEntity>) {
        mRecyclerView.adapter = TaskListAdapter(tasksList, mTaskListFragmentInteractionListener)
        mRecyclerView.layoutManager = LinearLayoutManager(mContext)
    }

    override fun onClick(view: View) {
        when(view.id){
            R.id.floatButtonAddTask -> openTaskFormActivity()
        }
    }

    private fun openTaskFormActivity(){
        startActivity(Intent(mContext, TaskFormActivity::class.java))
    }

    private fun openTaskFormActivity(bundle: Bundle){
        val intent = Intent(mContext, TaskFormActivity::class.java)
        intent.putExtras(bundle)
        startActivity(intent)
    }
}
