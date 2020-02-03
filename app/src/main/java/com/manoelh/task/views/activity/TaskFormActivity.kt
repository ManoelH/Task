package com.manoelh.task.views.activity

import android.app.DatePickerDialog
import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
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
import com.manoelh.task.service.TaskService
import com.manoelh.task.util.SecurityPreferences
import com.manoelh.task.util.ValidationException
import kotlinx.android.synthetic.main.activity_task_form.*
import java.text.SimpleDateFormat
import java.util.*


class TaskFormActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener,
    View.OnClickListener, DatePickerDialog.OnDateSetListener {

    private lateinit var mTaskBusiness: TaskBusiness
    private lateinit var mTask: TaskEntity
    private lateinit var mPrioritySelected: PriorityEntity
    private lateinit var mSecurityPreferences: SecurityPreferences
    private lateinit var mPriorities: List<PriorityEntity>
    private lateinit var mTaskService: TaskService
    private val mCalendar = Calendar.getInstance()
    private var mTaskId = ""
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
        buttonDate.setOnClickListener(this)
    }

    private fun intanceMyObjectsWithContext(){
        mTaskBusiness = TaskBusiness(this)
        mSecurityPreferences = SecurityPreferences(this)
        mTaskService = TaskService(this)
    }

    private fun loadSpinner(){
        mPriorities = PriorityCache.getCachePriorities()
        val adapter = ArrayAdapter<PriorityEntity>(this, android.R.layout.simple_spinner_dropdown_item, mPriorities)
        spinnerPriority.adapter = adapter
    }


    private fun loadTaskDataToUpdateFromActivity(){
        val bundle = intent.extras?.getString(TaskConstants.KEY.TASK_ID)
        if (bundle!=null){
            mTaskId = bundle
            observerLoadUpdateDatas()
        }
    }

    private fun observerLoadUpdateDatas(){
        mTaskService.loadTaskDataToUpdateFromActivity(mTaskId, getUserId()).observe(this, Observer { task->
            if (task != null){
                mTask = task
                setTaskValuesToActivityWhereTheUpdateWillHappen()
            }
        })
    }

    private fun setTaskValuesToActivityWhereTheUpdateWillHappen() {
        editTextDescription.setText(mTask.description)
        when (mTask.completed) {
            TaskConstants.COMPLETED.YES -> checkBoxCompleted.isChecked = true
            TaskConstants.COMPLETED.NOT -> checkBoxCompleted.isChecked = false
        }
        buttonDate.text = mTask.dueDate
        spinnerPriority.setSelection(returnIndexFromPrioritySpinner(mTask.priorityId))
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
        if(mTaskId.isNotBlank())
            buttonSaveTask.text = getString(R.string.button_edit)
        else
            buttonSaveTask.text = getString(R.string.button_register)
    }

    override fun onNothingSelected(parent: AdapterView<*>) {
        mPrioritySelected.id = PriorityConstants.DEFAULT_PRIORITY.ID
    }

    override fun onItemSelected(adapterView: AdapterView<*>, view: View?, position: Int, id: Long) {
            mPrioritySelected = spinnerPriority.selectedItem as PriorityEntity
    }

    override fun onClick(view: View) {
        if (view.id == R.id.buttonDate)
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
        changeVisibilityProgressBar()
        if (mTaskId.isNotBlank())
            setTaskAttributesToUpdate()
        else
            setTaskAttributesToInsert()
    }

    private fun changeVisibilityProgressBar(){
        if (progressBar.isVisible) {
            progressBar.visibility = ProgressBar.INVISIBLE
            window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        }
        else{
            progressBar.visibility = ProgressBar.VISIBLE
            window.setFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        }
    }

    private fun setTaskAttributesToUpdate(){
        val priority = spinnerPriority.selectedItem as PriorityEntity
        val description = editTextDescription.text.toString()
        val completed = returnCheckboxValue()
        val dueDate = buttonDate.text.toString()
        val userId = getUserId()

        mTask = TaskEntity(
            mTask.id,
            userId,
            priority.id,
            description,
            completed,
            dueDate
        )
        updateTask()
    }

    private fun updateTask(){
        try {
            mTaskBusiness.validateTask(mTask)

            db.collection(DatabaseConstants.COLLECTIONS.TASKS.COLLECTION_NAME).document(mTask.id)
                .update(mapOf(
                    DatabaseConstants.COLLECTIONS.TASKS.ATTRIBUTES.COMPLETED to mTask.completed,
                    DatabaseConstants.COLLECTIONS.TASKS.ATTRIBUTES.DESCRIPTION to mTask.description,
                    DatabaseConstants.COLLECTIONS.TASKS.ATTRIBUTES.DUE_DATE to mTask.dueDate,
                    DatabaseConstants.COLLECTIONS.TASKS.ATTRIBUTES.PRIORITY_ID to mTask.priorityId
                ))
                .addOnSuccessListener {
                    Log.d(ContentValues.TAG, getString(R.string.task_updated_log) +mTask.id)
                    Toast.makeText(this, this.getString(R.string.task_updated_message), Toast.LENGTH_LONG).show()
                    finish()
                }
                .addOnFailureListener { e ->
                    Log.w(ContentValues.TAG, getString(R.string.error_update_task), e)
                    changeVisibilityProgressBar()
                }
        }catch (ve: ValidationException){
            Toast.makeText(this, ve.message, Toast.LENGTH_LONG).show()
            changeVisibilityProgressBar()
        }
    }

    private fun setTaskAttributesToInsert() {
        val priorityId = mPrioritySelected.id
        val description = editTextDescription.text.toString()
        val completed = returnCheckboxValue()
        val dueDate = buttonDate.text.toString()
        val userId = getUserId()

        mTask = TaskEntity(
            description = description,
            priorityId = priorityId,
            completed = completed,
            dueDate = dueDate,
            userId = userId)
        observerInsertDatas()
    }

    private fun observerInsertDatas(){
        mTaskService.insertTask(mTask).observe(this, Observer { idTask ->
            if (idTask.isNotBlank())
                finish()
            else
                changeVisibilityProgressBar()
        })
    }

    override fun onDateSet(view: DatePicker, year: Int, month: Int, dayOfMonth: Int) {
        val mSimpleFormat = SimpleDateFormat(TaskConstants.DATE.PATTERN)
        mCalendar.set(year, month, dayOfMonth)
        val date = mSimpleFormat.format(mCalendar.time)
        buttonDate.text = date
    }

    private fun returnCheckboxValue() = checkBoxCompleted.isChecked


    private fun getUserId() = mSecurityPreferences.getStoreString(SharedPreferencesContants.KEYS.USER_ID)!!

}
