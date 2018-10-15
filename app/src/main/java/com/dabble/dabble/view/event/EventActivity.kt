package com.dabble.dabble.view.event

import android.os.Bundle
import com.dabble.dabble.R
import com.dabble.dabble.view.BaseActivity

class EventActivity : BaseActivity() {

    val eventFragment = EventFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)


        setTabToolbar()
                .setTitle("dabble")
                .setCenterFragment(eventFragment)
                .build()

        setBottomNavigation(0)

        setKeyboardListener()
    }
}
