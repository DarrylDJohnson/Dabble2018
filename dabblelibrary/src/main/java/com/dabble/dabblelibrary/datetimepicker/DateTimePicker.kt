package com.dabble.dabblelibrary.datetimepicker

import android.content.Context
import android.content.res.TypedArray
import android.support.constraint.ConstraintLayout
import android.util.AttributeSet
import android.view.View
import android.widget.DatePicker
import android.widget.TextView
import android.widget.TimePicker
import com.dabble.dabblelibrary.R
import kotterknife.bindView

class DateTimePicker : ConstraintLayout, View.OnClickListener {

    val date: TextView by bindView(R.id.picker_date)
    val year: TextView by bindView(R.id.picker_year)
    val time: TextView by bindView(R.id.picker_time)
    val timePicker: TimePicker by bindView(R.id.picker_time_picker)
    val datePicker: DatePicker by bindView(R.id.picker_date_picker)

    var a: TypedArray

    constructor(context: Context?) : super(context) {
        a = context!!.theme.obtainStyledAttributes(R.styleable.ViewPagerToolbar);
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        a = context!!.theme.obtainStyledAttributes(attrs, R.styleable.ViewPagerToolbar, 0, 0);
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        a = context!!.theme.obtainStyledAttributes(attrs, R.styleable.ViewPagerToolbar, 0, 0);
        init()
    }

    fun init() {

        View.inflate(context, R.layout.date_time_picker, this)

        date.setOnClickListener(this)
        year.setOnClickListener(this)
        time.setOnClickListener(this)


    }

    override fun onClick(v: View?) {

        when (v?.id) {

            R.id.picker_date -> {

                if (datePicker.visibility == View.GONE) {

                    if (timePicker.visibility == View.VISIBLE) {
                        timePicker.visibility = View.GONE
                    }

                    datePicker.visibility = View.VISIBLE

                } else {
                    datePicker.visibility = View.GONE
                }
            }

            R.id.picker_year -> {

                if (datePicker.visibility == View.GONE) {

                    if (timePicker.visibility == View.VISIBLE) {
                        timePicker.visibility = View.GONE
                    }

                    datePicker.visibility = View.VISIBLE

                } else {
                    datePicker.visibility = View.GONE
                }
            }

            R.id.picker_time -> {

                if (timePicker.visibility == View.GONE) {

                    if (datePicker.visibility == View.VISIBLE) {
                        datePicker.visibility = View.GONE
                    }

                    timePicker.visibility = View.VISIBLE

                } else {
                    timePicker.visibility = View.GONE
                }
            }
        }

    }
}