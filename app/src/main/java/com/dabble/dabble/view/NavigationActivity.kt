package com.dabble.dabble.view

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.FrameLayout
import android.widget.Toast
import com.dabble.dabble.R
import com.dabble.dabble.presenter.FirebaseHelper
import com.dabble.dabble.view.event.EventActivity
import com.dabble.dabble.view.profile.ProfileActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_navigation.*
import kotterknife.bindView

abstract class NavigationActivity : AppCompatActivity() {

    var exit = false
    val currentUser = FirebaseAuth.getInstance().currentUser!!
    val container: FrameLayout by bindView(R.id.container)
    val firebaseHelper = FirebaseHelper{ Snackbar.make(container, it, Snackbar.LENGTH_SHORT).show() }

    abstract var innerViewResId: Int
    abstract var position: Int

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_navigation)

        /* keyboard listener */
        navigation_activity.viewTreeObserver.addOnGlobalLayoutListener {
            if (window.decorView.height > (navigation_activity.height + 100))
                bottom_navigation_view.visibility = View.INVISIBLE
            else
                bottom_navigation_view.visibility = View.VISIBLE
        }

        /* bottom navigation*/
        initNavigation(position)

        /* inflate "fragment"*/

        val child = layoutInflater.inflate(innerViewResId, container, false)

        container.addView(child)

        onCreateNavigationActivity(child)
        //val innerView = LayoutInflater.from(this).inflate(innerViewResId, container, false)

    }

    override fun onBackPressed() {

        when {
            exit -> {
                Snackbar.make(navigation_activity, "signing out", Snackbar.LENGTH_SHORT).show()

                val i = applicationContext!!.packageManager.getLaunchIntentForPackage(applicationContext!!.packageName)
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(i)
            }

            supportFragmentManager.backStackEntryCount == 0 -> {
                Toast.makeText(this, "Press back twice to exit.", Toast.LENGTH_SHORT).show()

                exit = true

                Handler().postDelayed({ exit = false }, 3 * 1000)
            }

            else -> super.onBackPressed()
        }
    }

    fun initNavigation(position: Int) {

        this.position = position

        val menuItem = bottom_navigation_view.menu.getItem(this.position)

        menuItem.isChecked = true

        bottom_navigation_view.setOnNavigationItemSelectedListener {

            when {
                it.itemId == R.id.menu_event && position != 0 -> {
                    val intent = Intent(this, EventActivity::class.java)
                    this.startActivity(intent)//activityNumber = 0
                }

                it.itemId == R.id.menu_profile && position != 1 -> {
                    val intent = Intent(this, ProfileActivity::class.java)
                    this.startActivity(intent)//activityNumber = 1
                }
            }

            if (position != 0) finish()

            false
        }
    }

    fun addFragment(fragment: Fragment){
        supportFragmentManager.beginTransaction().add(R.id.navigation_activity, fragment, null).commit()
    }

    abstract fun onCreateNavigationActivity(view: View)
}