package com.dabble.dabble.view.signin

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.dabble.dabble.R
import com.dabble.dabble.view.BaseActivity
import com.dabble.dabble.view.event.EventActivity
import kotlinx.android.synthetic.main.activity_sign_in.*

class SignInActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_sign_in)

        sign_in_button.setOnClickListener {
            val intent = Intent(baseContext, EventActivity::class.java)
            startActivity(intent)
            finish()
        }
    }


}