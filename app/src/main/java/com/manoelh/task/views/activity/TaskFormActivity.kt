package com.manoelh.task.views.activity

import android.app.DatePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.Toast
import com.manoelh.task.R
import com.manoelh.task.business.TaskBusiness
import com.manoelh.task.constants.TaskConstants
import com.manoelh.task.entity.PriorityEntity
import com.manoelh.task.entity.TaskEntity
import kotlinx.android.synthetic.main.activity_task_form.*
import java.text.SimpleDateFormat
import java.util.*

class TaskFormActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener,
    View.OnClickListener, DatePickerDialog.OnDateSetListener {

    private lateinit var taskBusiness: TaskBusiness
    private lateinit var task: TaskEntity
    private val calendar = Calendar.getInstance()
    private lateinit var prioritySelected: PriorityEntity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_form)
        taskBusiness = TaskBusiness(this)
        setListeners()
        loadSpinner()
    }

    private fun loadSpinner(){
        val priorities = taskBusiness.loadPriorities()
        val adapter = ArrayAdapter<PriorityEntity>(this, android.R.layout.simple_spinner_dropdown_item, priorities)
        spinnerPriority.adapter = adapter
    }

    private fun setListeners(){
        spinnerPriority.onItemSelectedListener = this
        buttonRegisterTask.setOnClickListener(this)
        editTextDate.setOnClickListener(this)
    }

    override fun onNothingSelected(parent: AdapterView<*>) {

    }

    override fun onItemSelected(adapterView: AdapterView<*>, view: View, position: Int, id: Long) {
        prioritySelected = spinnerPriority.selectedItem as PriorityEntity
    }

    override fun onClick(view: View) {
        if (view.id == R.id.editTextDate)
            openDatePicker()
        else if (view.id == R.id.buttonRegisterTask)
            registerTask()
    }

    private fun openDatePicker(){
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_WEEK)
        DatePickerDialog(this, this, year, month, day).show()
    }

    private fun registerTask(){
        val priorityId = prioritySelected.id
        val description = editTextDescription.text.toString()
        val complete = returnCheckboxValue()
        val date = editTextDate.text.toString()
        task = TaskEntity(description = description, priority_id = priorityId, complete = complete, date = date, user_id = 0)
        Toast.makeText(this, "priority id: $priorityId", Toast.LENGTH_LONG).show()
    }

    override fun onDateSet(view: DatePicker, year: Int, month: Int, dayOfMonth: Int) {
        val mSimpleFormat = SimpleDateFormat("MM/dd/yyyy")
        calendar.set(year, month, dayOfMonth)
        val date = mSimpleFormat.format(calendar.time)
        editTextDate.setText(date)
    }

    private fun returnCheckboxValue(): Int {
        var isChecked: Int
        if (checkBoxCompleted.isChecked)
            isChecked = TaskConstants.COMPLETE.YES
        else
            isChecked = TaskConstants.COMPLETE.NOT
        return isChecked
    }
}
