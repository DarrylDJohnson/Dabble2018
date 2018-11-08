package com.dabble.dabble.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dabble.dabble.R
import com.dabble.dabble.models.User
import com.dabble.dabble.utils.GlideUtil
import kotlinx.android.synthetic.main.item_guest.view.*


class GuestAdapter(val guests: ArrayList<User>) : RecyclerView.Adapter<GuestAdapter.GuestViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = GuestViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_guest, parent, false))

    override fun getItemCount() = guests.size

    override fun onBindViewHolder(holder: GuestViewHolder, position: Int) = holder.bind(guests[position])

    class GuestViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(guest: User) = with(itemView) {

            GlideUtil.loadImage(guest.photoUrl, item_guest)
        }
    }
}