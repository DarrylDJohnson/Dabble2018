package com.dabble.dabble.view.event

import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import com.dabble.dabble.R
import com.dabble.dabble.adapters.EventAdapter
import com.dabble.dabble.adapters.GuestAdapter
import com.dabble.dabble.view.NavigationActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_event.*
import kotlinx.android.synthetic.main.navigation_activity.*
import org.joda.time.DateTime

class EventNavigationActivity : NavigationActivity(), View.OnClickListener {

    private lateinit var eventAdapter: EventAdapter
    private lateinit var guestAdapter: GuestAdapter

    override var position = 0

    override var innerViewResId = R.layout.fragment_event

    override fun onCreateNavigationActivity(view: View) {

        init()

        updateEvents()
    }

    override fun onClick(v: View?) {

        when (v?.id) {

            /* add event */
            R.id.toolbar_right -> {
                firebaseHelper.pushEvent(null, "Networking Event", DateTime.now().millis, ArrayList(), onComplete = {
                    updateEvents()
                })
            }

            /* request event */
            R.id.mb_request -> {

                if (recycler_event.currentItem != -1) {
                    firebaseHelper.pushRequestForEvent(currentUser.uid, eventAdapter.events[recycler_event.currentItem].oid)

                    updateGuests()
                }

                Snackbar.make(container, "requested", Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    fun init() {
        /* Toolbar */
        toolbar.text = "dabble"
        toolbar_right.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_add))
        toolbar_right.setOnClickListener(this)

        /* Material Button */
        mb_request.setOnClickListener(this)

        /* Discrete Scroll View - Event */
        eventAdapter = EventAdapter(ArrayList())
        recycler_event.adapter = eventAdapter

        /* RecyclerView - Guests */
        recycler_guest.layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.HORIZONTAL, false)
        guestAdapter = GuestAdapter(ArrayList())
        recycler_guest.adapter = guestAdapter

        /* Event - ScrollListener */
        recycler_event.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

                when (newState) {

                    RecyclerView.SCROLL_STATE_IDLE -> {

                        updateGuests()
                    }

                    else -> {
                        recycler_guest.visibility = View.INVISIBLE
                        recycler_guest_progress.visibility = View.VISIBLE
                    }
                }
            }
        })
    }

    fun updateEvents() {
        firebaseHelper.pullEvent(onComplete = { events ->

            eventAdapter.setData(events)

            if(events.size == 0){
                mb_request.visibility = View.INVISIBLE
            } else {
                mb_request.visibility = View.VISIBLE
            }

            recycler_event.visibility = View.VISIBLE
            recycler_event_progress.visibility = View.INVISIBLE

            updateGuests()
        })
    }

    fun updateGuests() {
        if (recycler_event.currentItem != -1)
            firebaseHelper.pullRequestsForEvent(eventAdapter.events[recycler_event.currentItem].oid, onComplete = {
                guestAdapter.guests.clear()
                guestAdapter.guests.addAll(it)
                guestAdapter.notifyDataSetChanged()

                recycler_guest.visibility = View.VISIBLE
                recycler_guest_progress.visibility = View.INVISIBLE
            })
    }
}