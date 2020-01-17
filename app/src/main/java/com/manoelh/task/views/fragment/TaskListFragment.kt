package com.manoelh.task.views.fragment

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FirebaseFirestore
import com.manoelh.task.R
import com.manoelh.task.adapter.TaskListAdapter
import com.manoelh.task.business.TaskBusiness
import com.manoelh.task.constants.DatabaseConstants
import com.manoelh.task.constants.SharedPreferencesContants
import com.manoelh.task.constants.TaskConstants
import com.manoelh.task.entity.TaskEntity
import com.manoelh.task.interfaces.OnTaskListFragmentInteractionListener
import com.manoelh.task.util.SecurityPreferences
import com.manoelh.task.views.activity.TaskFormActivity


class TaskListFragment : Fragment(), View.OnClickListener {

    private lateinit var mContext: Context
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mTaskBusiness: TaskBusiness
    private var taskFilterCompleted = TaskConstants.COMPLETED.NOT
    private lateinit var mTaskListFragmentInteractionListener: OnTaskListFragmentInteractionListener
    private val db = FirebaseFirestore.getInstance()
    private lateinit var mSecurityPreferences: SecurityPreferences

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
                deleteTask(task.id)
            }

            override fun onImageCompletedClick(task: TaskEntity) {
                if (task.completed == TaskConstants.COMPLETED.YES)
                    task.completed = TaskConstants.COMPLETED.NOT
                else
                    task.completed = TaskConstants.COMPLETED.YES
                updateTask(task.completed, task.id)
            }
        }
        return view
    }

    override fun onResume() {
        super.onResume()
        listTasks(taskFilterCompleted)
    }

    private fun intanceMyObjectsWithContext() {
        mTaskBusiness = TaskBusiness(mContext)
        mSecurityPreferences = SecurityPreferences(mContext)
    }

    private fun deleteTask(id: String){
        db.collection(DatabaseConstants.COLLECTIONS.TASKS.COLLECTION_NAME).document(id)
            .delete()
            .addOnSuccessListener {
                Log.d(TAG, "DocumentSnapshot successfully deleted!")
                Toast.makeText(mContext, mContext.getText(R.string.taskDeleted), Toast.LENGTH_LONG)
                listTasks(taskFilterCompleted)
            }
            .addOnFailureListener {
                    e -> Log.w(TAG, "Error deleting document", e)
            }
    }

    private fun listTasks(taskFilterCompleted: Boolean) {
        val tasksList = mutableListOf<TaskEntity>()
        val userId = mSecurityPreferences.getStoreString(SharedPreferencesContants.KEYS.USER_ID)!!

        db.collection(DatabaseConstants.COLLECTIONS.TASKS.COLLECTION_NAME)
            .whereEqualTo(DatabaseConstants.COLLECTIONS.TASKS.ATTRIBUTES.AUTHENTICATION_ID, userId)
            .whereEqualTo(DatabaseConstants.COLLECTIONS.TASKS.ATTRIBUTES.COMPLETED, taskFilterCompleted).get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    Log.d(TAG, "${document.id} => ${document.data}")

                    val taskEntity = TaskEntity(
                        document.id,
                        userId,
                        document.get(DatabaseConstants.COLLECTIONS.TASKS.ATTRIBUTES.PRIORITY_ID).toString(),
                        document.get(DatabaseConstants.COLLECTIONS.TASKS.ATTRIBUTES.DESCRIPTION).toString(),
                        document.getBoolean(DatabaseConstants.COLLECTIONS.TASKS.ATTRIBUTES.COMPLETED)!!,
                        document.get(DatabaseConstants.COLLECTIONS.TASKS.ATTRIBUTES.DUE_DATE).toString())
                    tasksList.add(taskEntity)
                }
                loadRecyclerView(tasksList)
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents.", exception)
            }
    }

    private fun updateTask(taskCompleted: Boolean, id: String) {
        db.collection(DatabaseConstants.COLLECTIONS.TASKS.COLLECTION_NAME).document(id)
            .update(
                mapOf(
                    DatabaseConstants.COLLECTIONS.TASKS.ATTRIBUTES.COMPLETED to taskCompleted
                )
            )
            .addOnSuccessListener {
                Log.d(TAG, "DocumentSnapshot updated with ID: $id")
                Toast.makeText(mContext, mContext.getString(R.string.taskUpdated), Toast.LENGTH_LONG)
                    .show()
                listTasks(taskFilterCompleted)
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error updating document", e)
            }

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
