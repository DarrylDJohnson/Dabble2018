package com.dabble.dabble.view.event

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.dabble.dabble.R
import com.dabble.dabble.adapters.MyEventsAdapter
import com.dabble.dabble.models.Event
import com.dabble.dabble.presenter.FirebaseHelper
import kotlinx.android.synthetic.main.recycler_view.*
import java.io.Serializable


class EventFragment : Fragment() {

    lateinit var firebaseHelper: FirebaseHelper
    val events = ArrayList<Event>()
    val adapter = MyEventsAdapter(events)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.recycler_view, container, false)

        firebaseHelper = FirebaseHelper { Snackbar.make(view, it, Snackbar.LENGTH_SHORT).show() }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        @Suppress("UNCHECKED_CAST")
        try {
            val type = arguments?.getString("type")
            val callback: () -> Unit = arguments?.getSerializable("callback") as () -> Unit

            init(type!!, callback)

        } catch (e: Exception) {
            Log.e("EventFragment", e.toString())
        }
    }

    fun init(type: String, callback: () -> Unit) {

        recycler_view.layoutManager = LinearLayoutManager(context, LinearLayout.VERTICAL, false)

        recycler_view.adapter = adapter

        updateEvents(type)

        swipe_refresh.setOnRefreshListener{
            updateEvents(type)
        }
    }

    fun newInstance(type: String, callback: () -> Unit): EventFragment {

        val fragment = EventFragment()
        val bundle = Bundle()

        bundle.putString("type", type)
        bundle.putSerializable("callback", callback as Serializable)

        fragment.arguments = bundle

        return fragment
    }

    fun updateEvents(type: String){

        events.clear()

        when (type) {
            "my" -> firebaseHelper.pullMyEvents {
                events.addAll(it)
                adapter.notifyDataSetChanged()
                swipe_refresh.isRefreshing = false
            }
            "requested" -> firebaseHelper.pullMyRequestedEvents {
                events.addAll(it)
                adapter.notifyDataSetChanged()
                swipe_refresh.isRefreshing = false
            }
            "confirmed" -> firebaseHelper.pullMyConfirmedEvents {
                events.addAll(it)
                adapter.notifyDataSetChanged()
                swipe_refresh.isRefreshing = false
            }
        }
    }
}