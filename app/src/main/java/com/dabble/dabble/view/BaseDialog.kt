package com.dabble.dabble.view

import android.content.Context
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import com.dabble.dabble.R
import kotterknife.bindView

abstract class BaseDialog : Fragment() {

    abstract val dialogId: Int

    val close = {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.windowToken, 0)
        fragmentManager?.beginTransaction()?.remove(this)?.commit()
    }

    val scrim: ConstraintLayout by bindView(R.id.dialog_scrim)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        return inflater.inflate(R.layout.dialog_base, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        scrim.setOnClickListener { close.invoke() }

        scrim.setBackgroundColor(ContextCompat.getColor(context!!, R.color.scrim))

        val dialogView = LayoutInflater.from(context).inflate(dialogId, view.findViewById(R.id.dialog_container), true)

        onDialogCreated(dialogView, savedInstanceState)
    }

    abstract fun onDialogCreated(view: View, savedInstanceState: Bundle?)
}
