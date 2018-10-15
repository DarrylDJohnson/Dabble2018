package com.dabble.dabble.view.signin

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import com.dabble.dabble.R
import com.dabble.dabble.presenter.SignInPresenter
import com.dabble.dabble.view.event.EventActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.activity_sign_in.*

class GoogleSignInActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var gso: GoogleSignInOptions
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var presenter: SignInPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        sign_in_button.setOnClickListener(this)
        continue_button.setOnClickListener(this)
        sign_out_button.setOnClickListener(this)

        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestProfile()
                .requestEmail()
                .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        mAuth = FirebaseAuth.getInstance()

        presenter = SignInPresenter()
    }

    override fun onStart() {
        super.onStart()

        if (mAuth.currentUser != null) {

            continue_button.text = "continue as ${mAuth.currentUser!!.displayName}"

            sign_in_button.visibility = View.GONE
            continue_button.visibility = View.VISIBLE
            sign_out_button.visibility = View.VISIBLE
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {

            val task = GoogleSignIn.getSignedInAccountFromIntent(data)

            try {
                val account = task.getResult(ApiException::class.java)

                firebaseAuthWithGoogle(account!!)

            } catch (e: ApiException) {
                Snackbar.make(activity_sign_in, "Google sign in failed", Snackbar.LENGTH_SHORT).show()

                updateUI(null)
            }
        }
    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount) {

        Log.d(TAG, "firebaseAuthWithGoogle: " + account.id)

        val credential = GoogleAuthProvider.getCredential(account.idToken, null)

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener {
                    if (it.isSuccessful) {

                        val user = it.result!!.user

                        presenter.verifyUser(
                                user,
                                onFound = { updateUI(user) },
                                onGenerated = { updateUI(user) },
                                onFail = { Snackbar.make(activity_sign_in, it, Snackbar.LENGTH_SHORT).show() }
                        )

                        Log.d(TAG, "signInWithCredential: success")

                        updateUI(mAuth.currentUser)

                    } else {

                        Log.w(TAG, "signInWithCredential: failure", it.exception)

                        Snackbar.make(activity_sign_in, "Authentication failed", Snackbar.LENGTH_SHORT).show()

                        updateUI(null)
                    }
                }
    }

    private fun signIn() {
        startActivityForResult(mGoogleSignInClient.signInIntent, RC_SIGN_IN)
    }

    private fun signOut() {
        mAuth.signOut()

        mGoogleSignInClient.signOut().addOnCompleteListener {
            updateUI(null)
        }
    }

    private fun updateUI(user: FirebaseUser?) {

        if (user != null) {

            Snackbar.make(activity_sign_in, "${user.displayName} signed in", Snackbar.LENGTH_SHORT)
                    .addCallback(object : Snackbar.Callback() {
                        override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                            super.onDismissed(transientBottomBar, event)
                            val intent = Intent(baseContext, EventActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                    })
                    .show()

            sign_in_button.visibility = View.GONE
            continue_button.visibility = View.VISIBLE
            sign_out_button.visibility = View.VISIBLE
        } else {
            sign_in_button.visibility = View.VISIBLE
            continue_button.visibility = View.GONE
            sign_out_button.visibility = View.GONE
        }

    }

    override fun onClick(v: View) {
        when (v.id) {

            R.id.sign_in_button -> signIn()

            R.id.continue_button -> signIn()

            R.id.sign_out_button -> signOut()
        }
    }

    companion object {
        const val TAG = "GoogleSignInActivity"
        const val RC_SIGN_IN = 9001
    }
}