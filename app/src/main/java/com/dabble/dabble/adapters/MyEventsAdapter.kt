package com.dabble.dabble.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dabble.dabble.R
import com.dabble.dabble.models.Event
import com.dabble.dabble.utils.GlideUtil
import kotlinx.android.synthetic.main.item_event_small.view.*
import org.joda.time.DateTime


class MyEventsAdapter(val events: ArrayList<Event>): RecyclerView.Adapter<MyEventsAdapter.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, position: Int) = MyEventsAdapter.ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_event_small, parent, false))


    override fun getItemCount() = events.size

    override fun onBindViewHolder(holder: MyEventsAdapter.ViewHolder, position: Int) = holder.bind(events[position])
    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){

        fun bind(event: Event) = with(itemView){

            GlideUtil.loadImage(event.photoUrl, item_event_small_photo)

            item_event_small_title.text = event.title
            item_event_small_date.text = DateTime(event.date).toString("MMMM d, YYYY")
        }
    }
}