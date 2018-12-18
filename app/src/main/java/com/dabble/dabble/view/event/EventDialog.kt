package com.dabble.dabble.view.event

import android.os.Bundle
import android.util.Log
import android.view.View
import com.dabble.dabble.R
import com.dabble.dabble.view.BaseDialog
import kotlinx.android.synthetic.main.dialog_create_event.*
import java.io.Serializable


class EventDialog : BaseDialog() {
    override val dialogId = R.layout.dialog_create_event

    override fun onDialogCreated(view: View, savedInstanceState: Bundle?) {

        event_create_cancel.setOnClickListener { close.invoke() }

        @Suppress("UNCHECKED_CAST")
        try {
            val title = arguments?.getString("title")
            val callback: (String) -> Unit = arguments?.getSerializable("callback") as (String) -> Unit

            if (title != null) {
                event_create_name.setText(title)
            }

            event_create_confirm.setOnClickListener({
                callback.invoke(event_create_name.text.toString())
                close.invoke()
            })

        } catch (e: Exception) {
            Log.e("EventDialog", "exception: ", e)
        }
    }

    fun newInstance(title: String?, callback: (String) -> Unit): EventDialog {

        val fragment = EventDialog()
        val bundle = Bundle()
        bundle.putString("title", title)
        bundle.putSerializable("callback", callback as Serializable)
        fragment.arguments = bundle
        return fragment
    }
}