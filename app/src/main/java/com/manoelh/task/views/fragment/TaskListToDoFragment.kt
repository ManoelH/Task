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
import com.manoelh.task.constants.SharedPreferencesContants
import com.manoelh.task.util.SecurityPreferences
import com.manoelh.task.util.ValidationException
import com.manoelh.task.views.activity.TaskFormActivity

class TaskListToDoFragment : Fragment(), View.OnClickListener {

    private lateinit var mContext: Context
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mTaskBusiness: TaskBusiness
    private lateinit var mSecurityPreferences: SecurityPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            /*param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)*/
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_task_list_to_do, container, false)
        view.findViewById<FloatingActionButton>(R.id.floatButtonAddTask).setOnClickListener(this)
        mContext = view.context
        intanceMyObjectsWithContext()
        recyclerViewDefinition(view)
        return view
    }

    private fun intanceMyObjectsWithContext() {
        mTaskBusiness = TaskBusiness(mContext)
        mSecurityPreferences = SecurityPreferences(mContext)
    }

    private fun recyclerViewDefinition(view: View) {
        /*Recycler view steps:
        * To get recycler view
        * To define a adapter
        * To define a layout*/
        mRecyclerView = view.findViewById(R.id.recyclerViewTasks)
        val userId = mSecurityPreferences.getStoreString(SharedPreferencesContants.KEYS.USER_ID)
        val taskList = mTaskBusiness.loadTasks(userId!!.toLong())

        for (i in 0 .. 50){
            taskList.add(taskList[i].copy(description = "description $i"))
        }

        mRecyclerView.adapter = TaskListAdapter(taskList)

        mRecyclerView.layoutManager = LinearLayoutManager(mContext)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment TaskListFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance() =
            TaskListToDoFragment().apply {
                arguments = Bundle().apply {
                    /*putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)*/
                }
            }
    }

    override fun onClick(view: View) {
        when(view.id){
            R.id.floatButtonAddTask -> openTaskFormActivity()
        }
    }

    private fun openTaskFormActivity(){
        startActivity(Intent(mContext, TaskFormActivity::class.java))

    }
}
