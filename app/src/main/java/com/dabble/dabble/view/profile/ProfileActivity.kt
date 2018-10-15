package com.dabble.dabble.view.profile

import android.os.Bundle
import com.dabble.dabble.R
import com.dabble.dabble.view.BaseActivity
import com.dabble.dabble.view.event.EventFragment


class ProfileActivity : BaseActivity(){

    val profileFragment = ProfileFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)

        setTabToolbar()
                .setTitle("profile")
                .setCenterFragment(profileFragment)
                .build()

        setBottomNavigation(1)

        setKeyboardListener()
    }
}