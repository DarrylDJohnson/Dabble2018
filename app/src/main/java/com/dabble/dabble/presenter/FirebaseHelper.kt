package com.dabble.dabble.presenter

import com.dabble.dabble.models.Event
import com.dabble.dabble.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class FirebaseHelper(val notify: (String) -> Unit) {

    private var firebaseUser = FirebaseAuth.getInstance().currentUser!!

    companion object {

        /* structure:  PARENT_CHILD*/

        val EVENT_REFERENCE = FirebaseDatabase.getInstance().getReference("event")
        val REQUESTED_EVENT_USER_REFERENCE = FirebaseDatabase.getInstance().getReference("userByRequestedEvent")
        val CONFIRM_EVENT_USER_REFERENCE = FirebaseDatabase.getInstance().getReference("userByConfirmedEvent")

        val USER_REFERENCE = FirebaseDatabase.getInstance().getReference("user")
        val USER_EVENT_REFERENCE = FirebaseDatabase.getInstance().getReference("eventByUser")

        const val USER_EVENT = "eventByUser"
        const val EVENT = "event"
        const val USER = "user"
        const val REQUESTED_EVENT_USER = "userByRequestedEvent"
        const val CONFIRMED_EVENT_USER = "userByConfirmedEvent"
    }

    //[EVENTS]--------------------------------------------------------------------------------------
    fun pushEvent(eventId: String?, title: String, date: Long, guests: ArrayList<String>, onComplete: (Event) -> Unit) {

        val oid = eventId ?: EVENT_REFERENCE.push().key!!

        val photoUrl = firebaseUser.photoUrl.toString()
        val newPhotoUrl = photoUrl.replace("/s96-c/", "/s1000-c/")

        val event = Event(firebaseUser.uid, oid, title, newPhotoUrl, date)

        EVENT_REFERENCE                                                                             //event
                .child(oid)                                                                         //oid
                .setValue(event)                                                                    //value
                .addOnCompleteListener { pushObjectTask ->

                    if (pushObjectTask.isSuccessful) {                                              //[IF SUCCESSFUL]
                        USER_EVENT_REFERENCE.child(firebaseUser.uid)                             //eventByUser
                                .child(oid)                                                         //oid
                                .setValue(oid)                                                      //oid
                                .addOnCompleteListener { pushIdTask ->
                                    //onCompleteListener
                                    if (pushIdTask.isSuccessful) {
                                        onComplete.invoke(event)
                                        notify.invoke("pushEvent(): eventByUser success ${event.oid}")
                                    } else {
                                        notify.invoke("pushEvent(): eventByUser failure ${event.oid}")
                                    }
                                }

                    } else notify.invoke("pushEvent(): event success ${event.oid}")
                }
    }

    fun pullEvent(vararg eventIds: String, onComplete: (ArrayList<Event>) -> Unit) {

        val events = ArrayList<Event>()

        if (eventIds.isEmpty()) {
            pullIds(EVENT, onComplete = { ids ->
                pullRecursive(EVENT, ids, { events.add(it.getValue(Event::class.java)!!) }, { onComplete.invoke(events) })
            })

        } else {

            pullRecursive(EVENT, ArrayList(eventIds.toList()), {

                val event = it.getValue(Event::class.java)!!
                events.add(event)

            }, { onComplete.invoke(events) })
        }
    }

    fun pullMyEvents(onComplete: (ArrayList<Event>) -> Unit) {

        val events = ArrayList<Event>()

        pullIds(USER_EVENT, firebaseUser.uid, onComplete = { eventIds ->
            pullRecursive(EVENT, eventIds, { events.add(it.getValue(Event::class.java)!!) }, { onComplete.invoke(events) })
        })
    }

    fun removeEvent(vararg events: Event, onComplete: (Event) -> Unit) {

        for (event in events) {

            EVENT_REFERENCE.child(event.uid).removeValue().addOnCompleteListener { eventTask ->

                if (eventTask.isSuccessful) {

                    USER_EVENT_REFERENCE.child(event.uid).child(event.oid).removeValue().addOnCompleteListener { eventByUserTask ->
                        if (eventByUserTask.isSuccessful) {
                            onComplete.invoke(event)

                            notify.invoke("removeEvent(): eventByUser success ${event.oid}")
                        } else {
                            notify.invoke("removeEvent(): eventByUser failure ${event.oid}")
                        }
                    }
                } else {
                    notify.invoke("removeEvent(): event success ${event.oid}")
                }
            }
        }
    }

    //[REQUEST EVENT]-------------------------------------------------------------------------------
    fun pushRequestForEvent(uid: String, eventId: String) {
        REQUESTED_EVENT_USER_REFERENCE
                .child(eventId)
                .child(uid)
                .setValue(uid)
                .addOnCompleteListener {
                    notify.invoke(it.toString())
                }
    }

    fun pullRequestsForEvent(eventId: String, onComplete: (ArrayList<User>) -> Unit) {

        val users = ArrayList<User>()

        pullIds(REQUESTED_EVENT_USER, eventId, onComplete = { ids ->
            pullRecursive(USER, ids, { users.add(it.getValue(User::class.java)!!) }, { onComplete.invoke(users) })
        })
    }

    //[CONFIRM EVENT]-------------------------------------------------------------------------------
    fun pushConfirmationForEvent(uid: String, eventId: String) {
        CONFIRM_EVENT_USER_REFERENCE
                .child(eventId)
                .child(uid)
                .setValue(uid)
                .addOnCompleteListener {
                    notify.invoke(it.toString())
                }
    }

    fun pullConfirmationForEvent(eventId: String, onComplete: (ArrayList<User>) -> Unit) {

        val users = ArrayList<User>()

        pullIds(CONFIRMED_EVENT_USER, eventId, onComplete = { ids ->
            pullRecursive(USER, ids, { users.add(it.getValue(User::class.java)!!) }, { onComplete.invoke(users) })
        })
    }

    //[USER]----------------------------------------------------------------------------------------
    fun pushUser(name: String, photoUrl: String, onComplete: (User) -> Unit) {

        val uid = firebaseUser.uid

        val user = User(firebaseUser.uid, name, photoUrl)

        USER_REFERENCE                                                                              //event
                .child(uid)                                                                         //oid
                .setValue(user)                                                                     //value
                .addOnCompleteListener { pushObjectTask ->

                    if (pushObjectTask.isSuccessful) onComplete.invoke(user)
                    else notify.invoke("pushEvent(): user failure")
                }
    }

    fun pullUser(vararg uids: String, onComplete: (ArrayList<User>) -> Unit) {

        val users = ArrayList<User>()

        if (uids.isEmpty()) {

            pullIds(USER, firebaseUser.uid, onComplete = {
                pullRecursive(USER, it, { users.add(it.getValue(User::class.java)!!) }, { onComplete.invoke(users) })
            })

        } else {

            pullRecursive(USER, ArrayList(uids.toList()), { users.add(it.getValue(User::class.java)!!) }, { onComplete.invoke(users) })
        }
    }

    fun removeUser(vararg events: Event, onComplete: (Event) -> Unit) {

        for (event in events) {

            EVENT_REFERENCE.child(event.uid).removeValue().addOnCompleteListener {

                if (it.isSuccessful) {

                    USER_EVENT_REFERENCE.child(event.uid).child(event.oid).removeValue().addOnCompleteListener {
                        if (it.isSuccessful) {
                            onComplete.invoke(event)

                            notify.invoke("removeEvent(): eventByUser success ${event.oid}")
                        } else {
                            notify.invoke("removeEvent(): eventByUser failure ${event.oid}")
                        }
                    }
                } else {
                    notify.invoke("removeEvent(): event success ${event.oid}")
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
                    notify.invoke("pullRecursive($reference): onCancelled")
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

    private fun pullIds(reference: String, onComplete: (ArrayList<String>) -> Unit) {

        val ids = ArrayList<String>()

        FirebaseDatabase.getInstance().getReference(reference)
                .addListenerForSingleValueEvent(object : ValueEventListener {

                    override fun onDataChange(snapshot: DataSnapshot) {

                        for (child in snapshot.children) {

                            if (child.child("oid").getValue(String::class.java) != null) {
                                ids.add(child.child("oid").getValue(String::class.java)!!)
                            }
                        }

                        onComplete.invoke(ids)
                    }

                    override fun onCancelled(snapshot: DatabaseError) {

                    }
                })
    }

}