package com.manoelh.task.views.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

import com.manoelh.task.R
import com.manoelh.task.adapter.TaskListAdapter
import com.manoelh.task.business.TaskBusiness
import com.manoelh.task.constants.TaskConstants
import com.manoelh.task.interfaces.OnTaskListFragmentInteractionListener
import com.manoelh.task.views.activity.MainActivity
import com.manoelh.task.views.activity.TaskFormActivity

class TaskListFragment : Fragment(), View.OnClickListener {

    private lateinit var mContext: Context
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mTaskBusiness: TaskBusiness
    private var taskFilterCompleted = TaskConstants.COMPLETED.NOT
    private lateinit var mTaskListFragmentInteractionListener: OnTaskListFragmentInteractionListener

    companion object {
        fun newInstance(taskFilterCompleted: Int) = TaskListFragment().apply {
            arguments = Bundle().apply {
                putInt(TaskConstants.KEY.TASK_FILTER, taskFilterCompleted)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            taskFilterCompleted = it.getInt(TaskConstants.KEY.TASK_FILTER)
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
            override fun onListClick(taskId: Long) {
                val bundle = Bundle()
                bundle.putLong(TaskConstants.KEY.TASK_ID, taskId)
                openTaskFormActivity(bundle)
            }
        }
        return view
    }

    override fun onResume() {
        super.onResume()
        loadRecyclerView()
    }

    private fun intanceMyObjectsWithContext() {
        mTaskBusiness = TaskBusiness(mContext)
    }

    private fun loadRecyclerView() {
        mRecyclerView.adapter = TaskListAdapter(mTaskBusiness.loadTasks(taskFilterCompleted), mTaskListFragmentInteractionListener)
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
