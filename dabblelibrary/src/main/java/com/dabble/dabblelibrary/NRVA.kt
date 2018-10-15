package com.dabble.dabblelibrary

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.dabble.dabblelibrary.NRVA.OuterViewHolder
import com.dabble.dabblelibrary.NRVA.InnerViewHolder
import java.util.*

abstract class NRVA<OVH : OuterViewHolder, IVH : InnerViewHolder, OI : Any>(private var outerItems: ArrayList<OI>) : RecyclerView.Adapter<OVH>() {

    val globalList = outerItems

    abstract fun onCreateOuterViewHolder(parent: ViewGroup, viewType: Int): OVH

    abstract fun onCreateInnerViewHolder(parent: ViewGroup, viewType: Int): IVH

    abstract fun onBindOuterViewHolder(holder: OVH, position: Int)

    abstract fun onBindInnerViewHolder(holder: IVH, outerItem: OI, position: Int)

    abstract fun getInnerItemCount(outerItem: OI): Int

    /**********************************************************************************************/
    override fun onBindViewHolder(holder: OVH, position: Int) = onBindOuterViewHolder(holder, position).also {
        holder.getInnerRecyclerView().adapter = InnerRecyclerViewAdapter(this, outerItems[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OVH = onCreateOuterViewHolder(parent, viewType)

    override fun getItemCount() = outerItems.size

    /**********************************************************************************************/
    abstract class OuterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        abstract fun getInnerRecyclerView(): RecyclerView
    }

    abstract class InnerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    /**********************************************************************************************/
    class InnerRecyclerViewAdapter<OVH : OuterViewHolder, IVH : InnerViewHolder, OI : Any>(private val NRVA: NRVA<OVH, IVH, OI>, private val outerItem: OI) : RecyclerView.Adapter<IVH>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IVH = NRVA.onCreateInnerViewHolder(parent, viewType)

        override fun onBindViewHolder(holder: IVH, position: Int) = NRVA.onBindInnerViewHolder(holder, outerItem, position)

        override fun getItemCount() = NRVA.getInnerItemCount(outerItem)
    }
}

