package com.manoelh.task.views.activity

import androidx.lifecycle.Observer
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.ui.AppBarConfiguration
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.manoelh.task.R
import com.manoelh.task.constants.SharedPreferencesContants
import com.manoelh.task.constants.TaskConstants
import com.manoelh.task.repository.PriorityRepository
import com.manoelh.task.service.TaskJobService
import com.manoelh.task.repository.UserRepository
import com.manoelh.task.util.SecurityPreferences
import com.manoelh.task.views.fragment.TaskListFragment
import kotlinx.android.synthetic.main.app_bar_main.*
import java.util.*

private const val TAG = "MainActivity"
private const val JOB_ID = 12
private const val FIFTY_MINUTES = 60 * 15 * 1000L

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener{

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var mSecurityPreferences: SecurityPreferences
    private lateinit var mUserRepository: UserRepository
    private lateinit var mPriorityRepository: PriorityRepository
    private lateinit var imageViewProfile: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_done, R.id.nav_todo, R.id.nav_profile, R.id.nav_logout, R.id.nav_header
            ), drawerLayout
        )
        intanceMyObjectsWithContext()
        setListenersInNavigationViewComponents()
        loadPhotoToImageViewProfile()
        setupObservers()
        mPriorityRepository.searchPriorities()
        createNotificationChannel()
        scheduleJob()
    }

    private fun scheduleJob(){
        val componentName = ComponentName(this, TaskJobService::class.java)
        val jobInfo = JobInfo.Builder(JOB_ID, componentName)
            .setRequiresCharging(false)
            .setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED)
            .setPersisted(true)
            .setPeriodic(FIFTY_MINUTES)
            .build()
        val jobScheduler = getSystemService(JOB_SCHEDULER_SERVICE) as JobScheduler
        val code = jobScheduler.schedule(jobInfo)
        if (code == JobScheduler.RESULT_SUCCESS)
            Log.d(TAG, "JOB SCHEDULED")
        else
            Log.d(TAG, "JOB SCHEDULING FAILED")
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_name)
            val descriptionText = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(
                TaskConstants.CHANNEL_ID.TASK_PENDING, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onResume() {
        super.onResume()
        setWelcomeValuesFromUser()
        loadFragment(TaskListFragment.newInstance(TaskConstants.COMPLETED.NOT))
        mUserRepository.downloadPhoto(imageViewProfile)
    }

    private fun setupObservers(){
        mUserRepository.getUserName().observe(this, Observer {
            setWelcomeValuesFromUser()
        })
    }

    private fun setWelcomeValuesFromUser() {
        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        val header = navigationView.getHeaderView(0)
        val userName = header.findViewById<TextView>(R.id.textViewUserName)
        val userEmail = header.findViewById<TextView>(R.id.textViewUserEmail)
        userName.text = mSecurityPreferences.getStoreString(SharedPreferencesContants.KEYS.USER_NAME)
        textViewWelcome.text = "Hello ${userName.text}!"
        userEmail.text = mSecurityPreferences.getStoreString(SharedPreferencesContants.KEYS.USER_EMAIL)
        setCurrentDate()
    }

    private fun setCurrentDate(){
        val calendar = Calendar.getInstance()
        val days = arrayListOf("Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday")
        val months = arrayListOf("January", "February", "March", "April", "May", "June",
                                 "July", "August", "September", "November", "December")
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
        val month = calendar.get(Calendar.MONTH)

        val date = "${days[dayOfWeek - 1]}, ${months[month]} $dayOfMonth"
        textViewCurrentDate.text = date
    }

    private fun intanceMyObjectsWithContext(){
        mSecurityPreferences = SecurityPreferences(this)
        mUserRepository = UserRepository(this)
        mPriorityRepository = PriorityRepository(this)
    }

    private fun setListenersInNavigationViewComponents() {
        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)
        val header = navigationView.getHeaderView(0)
        imageViewProfile = header.findViewById(R.id.imageViewProfile)
        imageViewProfile.setOnClickListener {
            openProfileActivity()
        }
    }

    private fun loadPhotoToImageViewProfile(){
        mUserRepository.downloadPhoto(imageViewProfile)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.nav_todo -> loadFragment(TaskListFragment.newInstance(TaskConstants.COMPLETED.NOT))
            R.id.nav_done -> loadFragment(TaskListFragment.newInstance(TaskConstants.COMPLETED.YES))
            R.id.nav_profile -> openProfileActivity()
            R.id.nav_logout -> logout()
        }
        return true
    }

    private fun loadFragment(fragment: Fragment){
        supportFragmentManager.beginTransaction().replace(R.id.frameFragment, fragment).commit()
    }

    private fun openProfileActivity(){
        startActivity(Intent(this, ProfileActivity::class.java))
    }

    private fun logout(){
        mSecurityPreferences.removeStoreString(SharedPreferencesContants.KEYS.USER_ID)
        mSecurityPreferences.removeStoreString(SharedPreferencesContants.KEYS.USER_NAME)
        mSecurityPreferences.removeStoreString(SharedPreferencesContants.KEYS.USER_EMAIL)
        FirebaseAuth.getInstance().signOut()
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
        cancelScheduleJob()
    }

    private fun cancelScheduleJob(){
        val jobScheduler = getSystemService(JOB_SCHEDULER_SERVICE) as JobScheduler
        jobScheduler.cancel(JOB_ID)
        Log.d(TAG, "JOB CANCELLED")
    }
}
