package com.manoelh.task.views.activity

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.TextView
import androidx.navigation.ui.AppBarConfiguration
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.manoelh.task.R
import com.manoelh.task.business.PriorityBusiness
import com.manoelh.task.constants.SharedPreferencesContants
import com.manoelh.task.constants.TaskConstants
import com.manoelh.task.repository.PriorityCache
import com.manoelh.task.util.SecurityPreferences
import com.manoelh.task.views.fragment.TaskListFragment
import kotlinx.android.synthetic.main.app_bar_main.*
import java.lang.Exception
import java.util.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener{

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var mSecurityPreferences: SecurityPreferences
    private  lateinit var mPriorityBusiness: PriorityBusiness
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        //val navView: NavigationView = findViewById(R.id.nav_view)

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
    }

    override fun onResume() {
        super.onResume()
        setWelcomeValuesFromUser()
        loadPriorities()
        loadFragment(TaskListFragment.newInstance(TaskConstants.COMPLETED.NOT))
        Thread.sleep(3000)
    }

    private fun getUserNameFromFirebase(){
        var userName: String
        try {
            db.collection("users")
                .whereEqualTo("authentication_id", mSecurityPreferences.getStoreString(SharedPreferencesContants.KEYS.USER_ID))
                .get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        Log.d(ContentValues.TAG, "${document.id} => ${document.data}")
                        userName = document.get("name").toString()
                        textViewWelcome.text = "Hello $userName!"
                        mSecurityPreferences.storeString(SharedPreferencesContants.KEYS.USER_NAME, userName)
                        setWelcomeValuesFromUser()
                    }
                }
                .addOnFailureListener { exception ->
                    Log.w(ContentValues.TAG, "Error getting documents.", exception)
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
        textViewWelcome.text = "Hello ${userName.text}"
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

        var date = "${days[dayOfWeek - 1]}, ${months[month]} $dayOfMonth"
        textViewCurrentDate.text = date
    }

    private fun intanceMyObjectsWithContext(){
        mSecurityPreferences = SecurityPreferences(this)
        mPriorityBusiness = PriorityBusiness(this)
    }

    private fun loadPriorities(){
        PriorityCache.setCache(mPriorityBusiness.loadPriorities())
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
