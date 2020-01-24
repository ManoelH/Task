package com.manoelh.task.views.activity

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.TextView
import androidx.navigation.ui.AppBarConfiguration
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.NotificationCompat
import androidx.fragment.app.Fragment
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.manoelh.task.R
import com.manoelh.task.constants.DatabaseConstants
import com.manoelh.task.constants.SharedPreferencesContants
import com.manoelh.task.constants.TaskConstants
import com.manoelh.task.entity.PriorityEntity
import com.manoelh.task.repository.PriorityCache
import com.manoelh.task.util.SecurityPreferences
import com.manoelh.task.views.fragment.TaskListFragment
import kotlinx.android.synthetic.main.app_bar_main.*
import java.lang.Exception
import java.util.*

private const val CHANNEL_ID = "taskChannel_id"

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener{

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var mSecurityPreferences: SecurityPreferences
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_done, R.id.nav_todo, R.id.nav_logout, R.id.nav_header/*, R.id.nav_gallery, R.id.nav_slideshow,
                R.id.nav_tools, R.id.nav_share, R.id.nav_send*/
            ), drawerLayout
        )
        intanceMyObjectsWithContext()
        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)
        getUserNameFromFirebase()
        createNotificationChannel()
        buildingNotification()

    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_name)
            val descriptionText = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun buildingNotification() {
        // Create an explicit intent for an Activity in your app
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent, 0)


        var notification = NotificationCompat.Builder(this, NotificationChannel.DEFAULT_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_mail)
            .setContentTitle("Title")
            .setContentText("Content")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setChannelId(CHANNEL_ID)
            .setAutoCancel(true)
    }

    override fun onResume() {
        super.onResume()
        setWelcomeValuesFromUser()
        listPriorities()
        loadFragment(TaskListFragment.newInstance(TaskConstants.COMPLETED.NOT))
        Thread.sleep(3000)
    }

    private fun getUserNameFromFirebase(){
        var userName: String
        try {
            db.collection(DatabaseConstants.COLLECTIONS.USERS.COLLECTION_NAME)
                .whereEqualTo(DatabaseConstants.COLLECTIONS.USERS.ATTRIBUTES.AUTHENTICATION_ID,
                    mSecurityPreferences.getStoreString(SharedPreferencesContants.KEYS.USER_ID))
                .get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        Log.d(ContentValues.TAG, "${document.id} => ${document.data}")
                        userName = document.get(DatabaseConstants.COLLECTIONS.USERS.ATTRIBUTES.NAME).toString()
                        textViewWelcome.text = "Hello $userName!"
                        mSecurityPreferences.storeString(SharedPreferencesContants.KEYS.USER_NAME, userName)
                        setWelcomeValuesFromUser()
                    }
                }
                .addOnFailureListener { exception ->
                    Log.w(ContentValues.TAG, getString(R.string.error_getting_user_name), exception)
                }

        }catch (e: Exception){
        }
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

    private fun listPriorities() {
        val priorities = mutableListOf<PriorityEntity>()

        try {
            db.collection(DatabaseConstants.COLLECTIONS.PRIORITIES.COLLECTION_NAME)
                .get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        Log.d(ContentValues.TAG, "${document.id} => ${document.data}")
                        val priorityEntity = PriorityEntity(document.id,
                            document.get(DatabaseConstants.COLLECTIONS.PRIORITIES.ATTRIBUTES.DESCRIPTION).toString())
                        priorities.add(priorityEntity)
                    }
                    PriorityCache.setCache(priorities)
                }
                .addOnFailureListener { exception ->
                    Log.w(ContentValues.TAG, getString(R.string.error_getting_priorities), exception)
                }
        }catch (e: Exception){
            throw e
        }
    }

    private fun intanceMyObjectsWithContext(){
        mSecurityPreferences = SecurityPreferences(this)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.nav_todo -> loadFragment(TaskListFragment.newInstance(TaskConstants.COMPLETED.NOT))
            R.id.nav_done -> loadFragment(TaskListFragment.newInstance(TaskConstants.COMPLETED.YES))
            R.id.nav_logout -> logout()
        }
        return true
    }

    private fun loadFragment(fragment: Fragment){
        supportFragmentManager.beginTransaction().replace(R.id.frameFragment, fragment).commit()
    }

    private fun logout(){
        mSecurityPreferences.removeStoreString(SharedPreferencesContants.KEYS.USER_ID)
        mSecurityPreferences.removeStoreString(SharedPreferencesContants.KEYS.USER_NAME)
        mSecurityPreferences.removeStoreString(SharedPreferencesContants.KEYS.USER_EMAIL)
        FirebaseAuth.getInstance().signOut()
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}
