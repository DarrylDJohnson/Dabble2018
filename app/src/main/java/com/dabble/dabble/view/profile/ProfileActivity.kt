package com.dabble.dabble.view.profile

import android.support.v4.app.FragmentPagerAdapter
import android.view.View
import com.dabble.dabble.R
import com.dabble.dabble.utils.GlideUtil
import com.dabble.dabble.view.NavigationActivity
import com.dabble.dabble.view.event.EventFragment
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.activity_navigation.*

class ProfileActivity : NavigationActivity() {

    override var position = 1

    override var innerViewResId = R.layout.fragment_profile

    override fun onCreateNavigationActivity(view: View) {

        toolbar.text = currentUser.displayName

        firebaseHelper.pullUser(currentUser.uid, onComplete = {

            if (it[0] != null) {
                GlideUtil.loadImage(it[0].photoUrl, profile_picture)
                profile_name.text = it[0].name
            }
        })

        viewpager.adapter = object : FragmentPagerAdapter(supportFragmentManager) {

            val titles = arrayOf("my events", "requested", "confirmed")

            override fun getCount() = titles.size

            override fun getPageTitle(position: Int) = titles[position]

            override fun getItem(position: Int) = when (position) {

                0 -> EventFragment().newInstance("my", {})
                1 -> EventFragment().newInstance("requested", {})
                2 -> EventFragment().newInstance("confirmed", {})
                else -> EventFragment().newInstance("my", {})
            }
        }

        tabs.setupWithViewPager(viewpager)
    }
}