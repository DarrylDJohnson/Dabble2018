package com.dabble.dabble.view.event

import android.graphics.Color
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import com.dabble.dabble.R
import com.dabble.dabble.adapters.EventAdapter
import com.dabble.dabble.adapters.GuestAdapter
import com.dabble.dabble.view.NavigationActivity
import kotlinx.android.synthetic.main.activity_navigation.*
import kotlinx.android.synthetic.main.fragment_event.*
import org.joda.time.DateTime

class EventActivity : NavigationActivity(), View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    private lateinit var eventAdapter: EventAdapter
    private lateinit var guestAdapter: GuestAdapter

    val requestedEvents = ArrayList<String>()

    override var position = 0

    override var innerViewResId = R.layout.fragment_event

    override fun onCreateNavigationActivity(view: View) {

        init()

        updateEvents()
    }

    override fun onClick(v: View?) {

        when (v?.id) {

            /* search event */
            R.id.toolbar_right -> {}

            /* create event */
            R.id.event_create -> {

                addFragment(EventDialog().newInstance(null, callback = { title ->
                    firebaseHelper.pushEvent(null, title, DateTime.now().millis, ArrayList(), onComplete = { event ->
                        updateEvents()
                    })
                }))
            }
        }
    }

    fun init() {
        /* Toolbar */
        toolbar.text = "dabble"
        toolbar_right.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_search))
        toolbar_right.setOnClickListener(this)
        event_create.setOnClickListener(this)

        /* RecyclerView - Event */
        eventAdapter = EventAdapter(ArrayList(), requestedEvents, onRequest = { requestedEvent ->

            Snackbar.make(navigation_activity, "requested", Snackbar.LENGTH_LONG)
                    .setAction("UNDO", {})
                    .setActionTextColor(Color.YELLOW)
                    .addCallback(object: Snackbar.Callback(){
                        override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                            if(event != DISMISS_EVENT_ACTION){
                                firebaseHelper.pushRequestForEvent(currentUser.uid, requestedEvent)
                                requestedEvents.add(requestedEvent)
                                eventAdapter.notifyItemChanged(recycler_event.currentItem)
                                updateGuests()
                            }
                        }
                    }).show()
        })
        recycler_event.adapter = eventAdapter

        /* RecyclerView - Guests */
        recycler_guest.layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.HORIZONTAL, false)
        guestAdapter = GuestAdapter(ArrayList())
        recycler_guest.adapter = guestAdapter

        /* SwipeToRefresh - Event */
        swipe_refresh_event.setOnRefreshListener(this)

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

            firebaseHelper.pullMyRequestedEventIds {

                eventAdapter.setData(events, it)

                swipe_refresh_event.isRefreshing = false

                updateGuests()
            }
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

    override fun onRefresh() {
        updateEvents()
    }
}