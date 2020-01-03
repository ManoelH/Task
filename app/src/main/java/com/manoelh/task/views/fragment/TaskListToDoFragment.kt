package com.manoelh.task.views.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.floatingactionbutton.FloatingActionButton

import com.manoelh.task.R
import com.manoelh.task.views.activity.TaskFormActivity

class TaskListToDoFragment : Fragment(), View.OnClickListener {

    private lateinit var mContext: Context

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
        return view
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
