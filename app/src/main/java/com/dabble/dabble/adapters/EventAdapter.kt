package com.dabble.dabble.adapters

import android.support.v4.content.ContextCompat
import android.support.v4.widget.TextViewCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dabble.dabble.R
import com.dabble.dabble.models.Event
import com.dabble.dabble.utils.GlideUtil
import kotlinx.android.synthetic.main.item_event.view.*
import org.joda.time.DateTime

class EventAdapter(val events: ArrayList<Event>, val requestedEvents: ArrayList<String>, val onRequest: (String) -> Unit) : RecyclerView.Adapter<EventAdapter.EventViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = EventViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_event, parent, false))

    override fun getItemCount() = events.size

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) = holder.bind(events[position], requestedEvents, onRequest)

    fun setData(events: ArrayList<Event>, requestedEvents: ArrayList<String>) {
        this.events.clear()
        this.events.addAll(events)
        this.requestedEvents.clear()
        this.requestedEvents.addAll(requestedEvents)
        notifyDataSetChanged()
    }

    class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {


        fun bind(event: Event, requestedEvents: ArrayList<String>, onRequest: (String) -> Unit): Unit = with(itemView) {

            val onLongClickListener = View.OnLongClickListener {
                onRequest.invoke(event.oid)

                true
            }

            GlideUtil.loadImage(event.photoUrl, event_image)

            event_title.text = event.title
            event_date.text = DateTime.now().toString("d")
            event_month.text = DateTime.now().toString("MMM")

            TextViewCompat.setAutoSizeTextTypeWithDefaults(event_date, TextViewCompat.AUTO_SIZE_TEXT_TYPE_UNIFORM)
            TextViewCompat.setAutoSizeTextTypeWithDefaults(event_month, TextViewCompat.AUTO_SIZE_TEXT_TYPE_UNIFORM)

            item_event.clipToOutline = true

            event_image.setOnLongClickListener(onLongClickListener)

            event_request.setOnLongClickListener(onLongClickListener)

            if (requestedEvents.contains(event.oid)) {
                event_request.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_check_circle_selected))
            } else {
                event_request.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_check_circle_unselected))
            }
        }
    }
}