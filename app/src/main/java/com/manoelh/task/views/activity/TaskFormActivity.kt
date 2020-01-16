package com.manoelh.task.views.activity

import android.app.DatePickerDialog
import android.content.ContentValues
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import com.manoelh.task.R
import com.manoelh.task.business.TaskBusiness
import com.manoelh.task.constants.DatabaseConstants
import com.manoelh.task.constants.PriorityConstants
import com.manoelh.task.constants.SharedPreferencesContants
import com.manoelh.task.constants.TaskConstants
import com.manoelh.task.entity.PriorityEntity
import com.manoelh.task.entity.TaskEntity
import com.manoelh.task.repository.PriorityCache
import com.manoelh.task.util.SecurityPreferences
import com.manoelh.task.util.ValidationException
import kotlinx.android.synthetic.main.activity_task_form.*
import java.text.SimpleDateFormat
import java.util.*

class TaskFormActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener,
    View.OnClickListener, DatePickerDialog.OnDateSetListener {

    private lateinit var mTaskBusiness: TaskBusiness
    private lateinit var mTask: TaskEntity
    private val mCalendar = Calendar.getInstance()
    private lateinit var mPrioritySelected: PriorityEntity
    private lateinit var mSecurityPreferences: SecurityPreferences
    private lateinit var mPriorities: List<PriorityEntity>
    private var mTaskId = 0L
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_form)
        setListeners()
        intanceMyObjectsWithContext()
        loadSpinner()
        loadTaskDataToUpdateFromActivity()
        loadTextButtonSaveTask()
    }

    private fun setListeners(){
        spinnerPriority.onItemSelectedListener = this
        buttonSaveTask.setOnClickListener(this)
        editTextDate.setOnClickListener(this)
    }

    private fun intanceMyObjectsWithContext(){
        mTaskBusiness = TaskBusiness(this)
        mSecurityPreferences = SecurityPreferences(this)
    }

    private fun loadSpinner(){
        mPriorities = PriorityCache.getCachePriorities()
        val adapter = ArrayAdapter<PriorityEntity>(this, android.R.layout.simple_spinner_dropdown_item, mPriorities)
        spinnerPriority.adapter = adapter
    }


    private fun loadTaskDataToUpdateFromActivity(){
        val bundle = intent.extras?.getLong(TaskConstants.KEY.TASK_ID)
        if (bundle!=null){
            mTaskId = bundle
            mTask = mTaskBusiness.loadTaskById(mTaskId)!!
            editTextDescription.setText(mTask.description)
            when(mTask.completed) {
                TaskConstants.COMPLETED.YES -> checkBoxCompleted.isChecked = true
                TaskConstants.COMPLETED.NOT -> checkBoxCompleted.isChecked = false
            }
            editTextDate.setText(mTask.dueDate.toString())

            spinnerPriority.setSelection(returnIndexFromPrioritySpinner(mTask.priorityId))
        }
    }

    private fun returnIndexFromPrioritySpinner(priorityId: String): Int{

        var index = 0
        for (i in 0 .. mPriorities.size ){
            if (mPriorities[i].id == priorityId){
                index = i
                break
            }
        }
        return index
    }

    private fun loadTextButtonSaveTask(){
        if(mTaskId > 0)
            buttonSaveTask.text = getString(R.string.buttonEdit)
        else
            buttonSaveTask.text = getString(R.string.buttonRegister)
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
        else if (view.id == R.id.buttonSaveTask)
            verifyActionRegisterTaskButton()
    }

    private fun openDatePicker(){
        val year = mCalendar.get(Calendar.YEAR)
        val month = mCalendar.get(Calendar.MONTH)
        val day = mCalendar.get(Calendar.DAY_OF_WEEK)
        DatePickerDialog(this, this, year, month, day).show()
    }

    private fun verifyActionRegisterTaskButton(){
        if (mTaskId > 0)
            updateTask()
        else
            setTaskAtributes()
    }

    private fun updateTask(){
        val priority = spinnerPriority.selectedItem as PriorityEntity
        val description = editTextDescription.text.toString()
        val completed = returnCheckboxValue()
        val dueDate = editTextDate.text.toString()
        val userId = getUserId()

        val date = Calendar.getInstance()
        mTask = TaskEntity(
            mTask.id,
            userId,
            priority.id,
            description,
            completed,
            date.time
            //dueDate
        )
        mTaskBusiness.updateTask(mTask)
        finish()
    }

    private fun setTaskAtributes() {
        val priorityId = mPrioritySelected.id
        val description = editTextDescription.text.toString()
        val completed = returnCheckboxValue()
        val dueDate = SimpleDateFormat("MM/dd/yyyy").parse(editTextDate.text.toString())
        val userId = getUserId()

        mTask = TaskEntity(
            description = description,
            priorityId = priorityId,
            completed = completed,
            dueDate = dueDate,
            userId = userId
        )
        insertTask(mTask)
    }

    private fun insertTask(taskEntity: TaskEntity){
        try {
            mTaskBusiness.validateTask(taskEntity)
            val taskDatabase =
                hashMapOf(
                    DatabaseConstants.FIREBASE_TABLES.TASKS.COLUMNS.AUTHENTICATION_ID to taskEntity.userId,
                    DatabaseConstants.FIREBASE_TABLES.TASKS.COLUMNS.COMPLETED to taskEntity.completed,
                    DatabaseConstants.FIREBASE_TABLES.TASKS.COLUMNS.DESCRIPTION to taskEntity.description,
                    DatabaseConstants.FIREBASE_TABLES.TASKS.COLUMNS.DUE_DATE to taskEntity.dueDate,
                    DatabaseConstants.FIREBASE_TABLES.TASKS.COLUMNS.PRIORITY_ID to taskEntity.priorityId)
            db.collection("tasks")
                .add(taskDatabase)
                .addOnSuccessListener { documentReference ->
                    Log.d(
                        ContentValues.TAG,
                        "DocumentSnapshot added with ID: ${documentReference.id}")
                    Toast.makeText(this, this.getString(R.string.taskSaved), Toast.LENGTH_LONG)
                        .show()
                    finish()
                }
                .addOnFailureListener { e ->
                    Log.w(ContentValues.TAG, "Error adding document", e)
                }
        }catch (ve: ValidationException){
            Toast.makeText(this, ve.message, Toast.LENGTH_LONG).show()
        }
    }

    override fun onDateSet(view: DatePicker, year: Int, month: Int, dayOfMonth: Int) {
        val mSimpleFormat = SimpleDateFormat("MM/dd/yyyy")
        mCalendar.set(year, month, dayOfMonth)
        val date = mSimpleFormat.format(mCalendar.time)
        editTextDate.setText(date)
    }

    private fun returnCheckboxValue() = checkBoxCompleted.isChecked


    private fun getUserId() = mSecurityPreferences.getStoreString(SharedPreferencesContants.KEYS.USER_ID)!!

}
