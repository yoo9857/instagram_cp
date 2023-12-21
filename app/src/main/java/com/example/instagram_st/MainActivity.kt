package com.example.instagram_st
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.example.instagram_st.navigation.*
import com.google.android.material.bottomnavigation.BottomNavigationView
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.action_home -> {
                    val detailViewFragment = DetailViewFragment()
                    supportFragmentManager.beginTransaction().replace(R.id.main_content, detailViewFragment)
                        .commit()
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.action_search -> {
                    val gridFragment = GridFragment()
                    supportFragmentManager.beginTransaction().replace(R.id.main_content, gridFragment)
                        .commit()
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.action_add_photo -> {
                    // Add your action here
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.action_favorite_alarm -> {
                    val alarmFragment = AlarmFragment()
                    supportFragmentManager.beginTransaction().replace(R.id.main_content, alarmFragment)
                        .commit()
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.action_account -> {
                    val userFragment = UserFragment()
                    supportFragmentManager.beginTransaction().replace(R.id.main_content, userFragment)
                        .commit()
                    return@setOnNavigationItemSelectedListener true
                }
                else -> return@setOnNavigationItemSelectedListener false
            }
        }
    }
}
