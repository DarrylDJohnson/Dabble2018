package com.dabble.dabble.view.profile

import android.view.View
import com.dabble.dabble.R
import com.dabble.dabble.presenter.FirebaseHelper
import com.dabble.dabble.utils.GlideUtil
import com.dabble.dabble.view.NavigationActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.navigation_activity.*

class ProfileNavigationActivity : NavigationActivity() {

    override var position = 1

    override var innerViewResId = R.layout.fragment_profile

    override fun onCreateNavigationActivity(view: View) {

        toolbar.text = currentUser.displayName

        firebaseHelper.pullUser(FirebaseAuth.getInstance().currentUser!!.uid, onComplete = {

            if (it[0] != null) {
                GlideUtil.loadImage(it[0].photoUrl, profile_picture)
                profile_name.text = it[0].name
            }
        })
    }

    companion object {
        private lateinit var firebaseHelper: FirebaseHelper
    }
}