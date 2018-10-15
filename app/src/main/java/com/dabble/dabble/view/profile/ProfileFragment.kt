package com.dabble.dabble.view.profile


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.LinearSnapHelper
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.dabble.dabble.R
import com.dabble.dabble.adapters.EventAdapter
import com.dabble.dabble.presenter.FirebaseHelper
import com.dabble.dabble.presenter.PresenterInterface
import com.dabble.dabblelibrary.Carousel.Carousel
import kotlinx.android.synthetic.main.fragment_event.*
import kotterknife.bindView
import org.joda.time.DateTime

class ProfileFragment : Fragment(), PresenterInterface {

    private val eventRecycler: RecyclerView by bindView(R.id.recycler_event)

    private lateinit var firebaseHelper: FirebaseHelper
    private lateinit var adapter: EventAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = inflater.inflate(R.layout.fragment_event, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firebaseHelper = FirebaseHelper { Toast.makeText(context, it, Toast.LENGTH_SHORT).show() }

        eventRecycler.layoutManager = Carousel(context, LinearLayoutManager.HORIZONTAL, false)

        val snaphelper = LinearSnapHelper()
        snaphelper.attachToRecyclerView(eventRecycler)

        val guestUrls = ArrayList<String>()

        guestUrls.add("https://images.pexels.com/photos/555790/pexels-photo-555790.png?auto=compress&cs=tinysrgb&h=350")
        guestUrls.add("https://images.pexels.com/photos/220453/pexels-photo-220453.jpeg?auto=compress&cs=tinysrgb&h=350")
        guestUrls.add("https://images.pexels.com/photos/428364/pexels-photo-428364.jpeg?auto=compress&cs=tinysrgb&h=350")
        guestUrls.add("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRFJjQQ6FEtCF89eeHX7NECI4j3zu1h__2jNCD7HVAp8RRf2GFY")

        firebaseHelper.pullMyEvents(onComplete = {
            adapter = EventAdapter(it)
            eventRecycler.adapter = adapter
        })

        fab_event.setOnClickListener {
            firebaseHelper.pushEvent(null, "Networking Event", DateTime.now().millis, guestUrls, onComplete = {
                adapter.events.add(0, it)
                adapter.notifyDataSetChanged()
            })
        }

        /*
        fab_event.setOnClickListener {

            fragmentManager!!.beginTransaction().add(R.id.activity_container, EventDialog().newInstance(null, callback = { title ->
                presenter.pushEvent(null, title, DateTime.now().millis, onComplete = {
                    adapter.events.add(0, it)
                    adapter.notifyDataSetChanged()
                })
            }), null).commit()
        }
        */
    }

    override fun notify(message: String, id: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
