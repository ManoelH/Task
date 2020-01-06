package com.manoelh.task.views.activity

import android.app.DatePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.DatePicker
import com.manoelh.task.R
import com.manoelh.task.business.PriorityBusiness
import com.manoelh.task.business.TaskBusiness
import com.manoelh.task.constants.PriorityConstants
import com.manoelh.task.constants.SharedPreferencesContants
import com.manoelh.task.constants.TaskConstants
import com.manoelh.task.entity.PriorityEntity
import com.manoelh.task.entity.TaskEntity
import com.manoelh.task.util.SecurityPreferences
import kotlinx.android.synthetic.main.activity_task_form.*
import java.text.SimpleDateFormat
import java.util.*

class TaskFormActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener,
    View.OnClickListener, DatePickerDialog.OnDateSetListener {

    private lateinit var mTaskBusiness: TaskBusiness
    private lateinit var mPriorityBusiness: PriorityBusiness
    private lateinit var mTask: TaskEntity
    private val mCalendar = Calendar.getInstance()
    private lateinit var mPrioritySelected: PriorityEntity
    private lateinit var mSecurityPreferences: SecurityPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_form)
        setListeners()
        intanceMyObjectsWithContext()
        loadSpinner()
    }

    private fun loadSpinner(){
        val priorities = mPriorityBusiness.loadPriorities()
        val adapter = ArrayAdapter<PriorityEntity>(this, android.R.layout.simple_spinner_dropdown_item, priorities)
        spinnerPriority.adapter = adapter
    }

    private fun setListeners(){
        spinnerPriority.onItemSelectedListener = this
        buttonRegisterTask.setOnClickListener(this)
        editTextDate.setOnClickListener(this)
    }

    private fun intanceMyObjectsWithContext(){
        mTaskBusiness = TaskBusiness(this)
        mSecurityPreferences = SecurityPreferences(this)
        mPriorityBusiness = PriorityBusiness(this)
    }

    override fun onNothingSelected(parent: AdapterView<*>) {
        mPrioritySelected.id = PriorityConstants.DEFAULT_PRIORITY.ID
    }

    override fun onItemSelected(adapterView: AdapterView<*>, view: View, position: Int, id: Long) {
        mPrioritySelected = spinnerPriority.selectedItem as PriorityEntity
    }

    override fun onClick(view: View) {
        if (view.id == R.id.editTextDate)
            openDatePicker()
        else if (view.id == R.id.buttonRegisterTask)
            registerTask()
    }

    private fun openDatePicker(){
        val year = mCalendar.get(Calendar.YEAR)
        val month = mCalendar.get(Calendar.MONTH)
        val day = mCalendar.get(Calendar.DAY_OF_WEEK)
        DatePickerDialog(this, this, year, month, day).show()
    }

    private fun registerTask(){
        val priorityId = mPrioritySelected.id
        val description = editTextDescription.text.toString()
        val completed = returnCheckboxValue()
        val dueDate = editTextDate.text.toString()
        val userId = getUserId()
        mTask = TaskEntity(description = description, priorityId = priorityId, completed = completed, dueDate = dueDate, userId = userId)
        mTask.id = mTaskBusiness.insertTask(mTask)
        finish()
    }

    override fun onDateSet(view: DatePicker, year: Int, month: Int, dayOfMonth: Int) {
        val mSimpleFormat = SimpleDateFormat("MM/dd/yyyy")
        mCalendar.set(year, month, dayOfMonth)
        val date = mSimpleFormat.format(mCalendar.time)
        editTextDate.setText(date)
    }

    private fun returnCheckboxValue(): Int {
        var isChecked: Int
        if (checkBoxCompleted.isChecked)
            isChecked = TaskConstants.COMPLETED.YES
        else
            isChecked = TaskConstants.COMPLETED.NOT
        return isChecked
    }

    private fun getUserId() = mSecurityPreferences.getStoreString(SharedPreferencesContants.KEYS.USER_ID)!!.toLong()

}
