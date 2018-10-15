package com.dabble.dabble.presenter

import android.util.Log
import com.dabble.dabble.models.User
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class SignInPresenter {

    companion object {
        val USER_REFERENCE = FirebaseDatabase.getInstance().getReference("user")
        const val TAG = "SignInPresenter"
    }

    //[DONE]----------------------------------------------------------------------------------------
    fun verifyUser(firebaseUser: FirebaseUser, onFound: () -> Unit, onGenerated: () -> Unit, onFail: (String) -> Unit) {

        USER_REFERENCE.child(firebaseUser.uid).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                val photoUrl = firebaseUser.photoUrl.toString()
                val newPhotoUrl = photoUrl.replace("/s96-c/", "/s1000-c/")

                val user = User(firebaseUser.uid, firebaseUser.displayName, newPhotoUrl)

                if (snapshot.exists()) {
                    USER_REFERENCE
                            .child(firebaseUser.uid)
                            .setValue(user)
                            .addOnCompleteListener { onFound.invoke() }
                } else {
                    USER_REFERENCE
                            .child(firebaseUser.uid)
                            .setValue(user)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) onGenerated.invoke()
                                else onFail(task.toString())
                            }
                }
            }

            override fun onCancelled(snapshot: DatabaseError) {
                Log.d(TAG, snapshot.toString())
            }
        })
    }
}