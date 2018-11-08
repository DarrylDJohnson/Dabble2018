package com.dabble.dabble.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dabble.dabble.R
import com.dabble.dabble.models.Event
import com.dabble.dabble.utils.GlideUtil
import kotlinx.android.synthetic.main.item_event.view.*
import org.joda.time.DateTime

class EventAdapter(val events: ArrayList<Event>) : RecyclerView.Adapter<EventAdapter.EventViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = EventViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_event, parent, false))

    override fun getItemCount() = events.size

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) = holder.bind(events[position])

    fun setData(events: ArrayList<Event>) {
        this.events.clear()
        this.events.addAll(events)
        notifyDataSetChanged()
    }

    class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        lateinit var eventId: String

        fun bind(event: Event): Unit = with(itemView) {

            eventId = event.oid

            event_title.text = event.title
            event_date.text = DateTime.now().toString("MMMM d, YYYY")

            GlideUtil.loadImage(event.photoUrl, event_image)

            item_event.clipToOutline = true
        }
    }
}