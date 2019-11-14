package com.gaurav.walllpaperhub.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.gaurav.walllpaperhub.R
import com.gaurav.walllpaperhub.fragmets.FavoritesFragment
import com.gaurav.walllpaperhub.fragmets.HomeFragment
import com.gaurav.walllpaperhub.fragmets.SettingsFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity() {

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.nav_home -> {
                val fragment = HomeFragment()
                displayFragment(fragment)
                return@OnNavigationItemSelectedListener true
            }
            R.id.nav_fav -> {
                val fragment = FavoritesFragment()
                displayFragment(fragment)
                return@OnNavigationItemSelectedListener true
            }
            R.id.nav_settings -> {
                val fragment = SettingsFragment()
                displayFragment(fragment)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        bottom_navigation_view
            .setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        displayFragment(HomeFragment())
    }

    private fun displayFragment(fragment: Fragment){
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.content_area, fragment)
            .commit()
    }
}
