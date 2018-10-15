package com.dabble.dabble.presenter

import com.dabble.dabble.models.Event
import com.dabble.dabble.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.*

class Presenter(val view: PresenterInterface) {

    private var firebaseUser = FirebaseAuth.getInstance().currentUser!!

    companion object {

        val EVENT_REFERENCE = FirebaseDatabase.getInstance().getReference("event")
        val USER_REFERENCE = FirebaseDatabase.getInstance().getReference("user")
        val EVENT_BY_USER_REFERENCE = FirebaseDatabase.getInstance().getReference("eventByUser")

        const val EVENT = "event"
        const val USER = "user"
        const val EVENT_BY_USER = "eventByUser"

        const val PUSH = "push"
        const val PULL = "pull"
        const val REMOVE = "remove"
        const val CANCELLED = "cancelled"
        const val FAIL = "failed"
        const val SUCCESS = "succeeded"
    }

    //[DONE]----------------------------------------------------------------------------------------
    fun pullUser(onUserFound: (User) -> Unit, onExit: () -> Unit, vararg ids: String) {

        pullRecursive(USER, ArrayList(ids.toList()), { dataSnapshot -> onUserFound.invoke(dataSnapshot.getValue(User::class.java)!!) }, onExit)
    }

    fun removeUser(vararg ids: String) {
        for (id in ids) {
            USER_REFERENCE
                    .child(id)
                    .removeValue()
                    .addOnCompleteListener { view.notify("$USER $REMOVE $SUCCESS", id) }
        }
    }

    //[DONE]----------------------------------------------------------------------------------------
    fun pushEvent(id: String?, title: String, date: Long, onComplete: (Event) -> Unit) {

        val oid = id ?: EVENT_REFERENCE.push().key!!
        val photoUrl = firebaseUser.photoUrl.toString().replace("/s96-c/", "/s1000-c/")

        val event = Event(firebaseUser.uid, oid, title, photoUrl, date, ArrayList<String>())

        EVENT_REFERENCE                                                                             //event
                .child(oid)                                                                         //oid
                .setValue(event)                                                                    //value
                .addOnCompleteListener { pushObjectTask ->

                    if (pushObjectTask.isSuccessful) {                                              //[IF SUCCESSFUL]
                        EVENT_BY_USER_REFERENCE.child(firebaseUser.uid)                                       //eventByUser
                                .child(oid)                                                         //oid
                                .setValue(oid)                                                      //oid
                                .addOnCompleteListener { pushIdTask ->
                                    //onCompleteListener
                                    if (pushIdTask.isSuccessful) {
                                        onComplete.invoke(event)
                                    } else {
                                        view.notify("$EVENT $PUSH $FAIL", oid)
                                    }
                                }
                    } else view.notify("$EVENT $PUSH $FAIL", oid)
                }
    }

    fun pullEvent(vararg eventIds: String, onComplete: (ArrayList<Event>) -> Unit) {

        val events = ArrayList<Event>()

        if (eventIds.isEmpty()) {

            pullIds(EVENT_BY_USER, firebaseUser.uid, onComplete = {
                pullRecursive(EVENT, it, { events.add(it.getValue(Event::class.java)!!) }, { onComplete.invoke(events) })
            })

        } else {

            pullRecursive(EVENT, ArrayList(eventIds.toList()), { events.add(it.getValue(Event::class.java)!!) }, { onComplete.invoke(events) })
        }
    }

    fun removeWorkout(vararg events: Event, onComplete: (Event) -> Unit) {

        for (event in events) {

            EVENT_REFERENCE.child(event.uid).removeValue().addOnCompleteListener {

                if (it.isSuccessful) {

                    EVENT_BY_USER_REFERENCE.child(event.uid).child(event.oid).removeValue().addOnCompleteListener {
                        if (it.isSuccessful) {
                            onComplete.invoke(event)
                            view.notify("$EVENT $REMOVE: $SUCCESS", event.oid)
                        } else {
                            view.notify("$EVENT $REMOVE: $FAIL", event.oid)
                        }
                    }
                } else {
                    view.notify("$EVENT $REMOVE: $FAIL", event.oid)
                }
            }
        }
    }

    //----------------------------------------------------------------------------------------------
    fun pullRecursive(reference: String, ids: ArrayList<String>, unit: (DataSnapshot) -> Unit, onComplete: () -> Unit) {

        if (ids.isEmpty()) {
            onComplete.invoke()
        } else {

            FirebaseDatabase.getInstance().getReference(reference).child(ids.last()).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    //REMOVE QUERIED ID
                    ids.removeAt(ids.lastIndex)

                    if (snapshot.exists())
                        unit.invoke(snapshot)

                    pullRecursive(reference, ids, unit, onComplete)
                }

                override fun onCancelled(snapshot: DatabaseError) {
                    view.notify("$reference $PULL $CANCELLED", ids.last())
                }
            })
        }
    }

    private fun pullIds(reference: String, child: String, onComplete: (ArrayList<String>) -> Unit) {

        val ids = ArrayList<String>()

        FirebaseDatabase.getInstance().getReference(reference).child(child)
                .addListenerForSingleValueEvent(object : ValueEventListener {

                    override fun onDataChange(snapshot: DataSnapshot) {

                        for (id in snapshot.children) {
                            if (id.getValue(String::class.java) != null) {
                                ids.add(id.getValue(String::class.java)!!)
                            }
                        }

                        onComplete.invoke(ids)
                    }

                    override fun onCancelled(snapshot: DatabaseError) {

                    }
                })
    }
}