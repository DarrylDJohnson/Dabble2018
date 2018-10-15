package com.dabble.dabble.adapters

import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dabble.dabble.R
import com.dabble.dabble.models.Event
import com.dabble.dabblelibrary.NRVA
import kotlinx.android.synthetic.main.item_event.view.*
import kotlinx.android.synthetic.main.item_guest.view.*
import com.dabble.dabble.adapters.EventAdapter.GuestViewHolder
import com.dabble.dabble.adapters.EventAdapter.EventViewHolder
import com.dabble.dabble.utils.GlideUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import org.joda.time.DateTime

class EventAdapter(var events: ArrayList<Event>) : NRVA<EventViewHolder, GuestViewHolder, Event>(events) {

    override fun onCreateOuterViewHolder(parent: ViewGroup, viewType: Int) = EventViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_event, parent, false))

    override fun onCreateInnerViewHolder(parent: ViewGroup, viewType: Int) = GuestViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_guest, parent, false))

    override fun onBindOuterViewHolder(holder: EventViewHolder, position: Int) = holder.bind(events[position])

    override fun onBindInnerViewHolder(holder: GuestViewHolder, outerItem: Event, position: Int) = holder.bind(outerItem, position)

    override fun getInnerItemCount(outerItem: Event) = outerItem.guests.size

    class EventViewHolder(itemView: View) : OuterViewHolder(itemView) {
        override fun getInnerRecyclerView() = itemView.recycler_guest

        fun bind(event: Event) = with(itemView) {
            event_title.text = event.title
            event_date.text = DateTime.now().toString("MMMM d, YYYY")

            GlideUtil.loadImage(event.photoUrl, event_image)

            /* TODO GlideUtil.load(event.imageUrl)*/
            /* TODO DateTimeHelper(event.time)*/


            getInnerRecyclerView().layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        }
    }

    class GuestViewHolder(itemView: View) : InnerViewHolder(itemView) {
        fun bind(event: Event, guestPosition: Int) = with(itemView) {

            GlideUtil.loadImage(event.guests[guestPosition], item_guest)

            /* TODO GlideUtil.load(guest.imageUrl)*/
        }
    }
}