package com.dabble.dabble.view

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.constraint.ConstraintLayout
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.dabble.dabble.R
import com.dabble.dabble.view.event.EventActivity
import com.dabble.dabble.view.profile.ProfileActivity
import com.dabble.dabblelibrary.tabtoolbar.ViewPagerToolbar
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx
import kotterknife.bindView

@SuppressLint("Registered")
open class BaseActivity : AppCompatActivity() {

    private var bottomBarPosition = -1
    private var isBackPressTwiceToExit = false
    private var exit = false

    /**
     * Home Activity Section
     **/
    private val viewPagerToolbar: ViewPagerToolbar by bindView(R.id.tab_toolbar)
    private val viewPager: ViewPager by bindView(R.id.view_pager)
    private val bottomNavigationViewEx: BottomNavigationViewEx by bindView(R.id.bottom_navigation_view)
    private val activityRootView: ConstraintLayout by bindView(R.id.activity_container)
    private val progressBarTitle: TextView by bindView(R.id.progress_bar_title)
    private val progressBarContainer: LinearLayout by bindView(R.id.progress_bar)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)

        isBackPressTwiceToExit = true
    }

    fun setTabToolbar(): ViewPagerToolbar {

        viewPagerToolbar.setUpWithViewPager(viewPager, supportFragmentManager).build()

        return viewPagerToolbar
    }

    fun setBottomNavigation(bottomBarPosition: Int) {


        this.bottomBarPosition = bottomBarPosition

        if (bottomBarPosition > 0) {
            isBackPressTwiceToExit = false
        }

        //bottomNavigationViewEx.enableAnimation(false)
        //bottomNavigationViewEx.enableItemShiftingMode(false)
        //bottomNavigationViewEx.enableShiftingMode(false)
        //bottomNavigationViewEx.setTextVisibility(false)

        val menu = bottomNavigationViewEx.menu
        val menuItem = menu.getItem(this.bottomBarPosition)
        menuItem.isChecked = true

        bottomNavigationViewEx.setOnNavigationItemSelectedListener {

            when {

                it.itemId == R.id.menu_event && bottomBarPosition != 0 -> {
                    val intent = Intent(this, EventActivity::class.java)
                    this.startActivity(intent)//activityNumber = 0
                }


                it.itemId == R.id.menu_profile && bottomBarPosition != 1 -> {
                    val intent = Intent(this, ProfileActivity::class.java)
                    this.startActivity(intent)//activityNumber = 1
                }
            }

            if (bottomBarPosition != 0) {
                finish()
            }

            false
        }
    }

    fun setKeyboardListener() {
        activityRootView.viewTreeObserver.addOnGlobalLayoutListener {
            if (window.decorView.height > (activityRootView.height + 100)) {
                bottomNavigationViewEx.visibility = View.INVISIBLE
            } else {
                bottomNavigationViewEx.visibility = View.VISIBLE
            }
        }
    }

    fun showDialog(message: String) {
        progressBarContainer.visibility = View.VISIBLE
        progressBarTitle.text = message
    }

    fun hideDialog() {
        progressBarContainer.visibility = View.GONE
    }

    private fun backPressTwiceToExit() {

        when {
            exit -> {
                Toast.makeText(applicationContext, "signing out", Toast.LENGTH_SHORT).show()

                val i = applicationContext!!.packageManager.getLaunchIntentForPackage(applicationContext!!.packageName)
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(i)
            }

            supportFragmentManager.backStackEntryCount == 0 -> {
                Toast.makeText(this, "Press back again to exit.", Toast.LENGTH_SHORT).show()
                exit = true

                Handler().postDelayed({ exit = false }, 3 * 1000)
            }

            else -> super.onBackPressed()
        }
    }

    override fun onBackPressed() {

        when {
            isBackPressTwiceToExit -> backPressTwiceToExit()

            bottomBarPosition != -1 && viewPager.currentItem != 0 -> viewPager.currentItem = 0

            else -> super.onBackPressed()
        }
    }

    override fun finish() {
        super.finish()
        hideDialog()
    }
}