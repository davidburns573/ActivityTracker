package tech.davidburns.activitytracker.fragments

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.widget.TimePicker
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.session_log_time.*
import tech.davidburns.activitytracker.Activity
import tech.davidburns.activitytracker.R
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class SessionLog(val activity: Activity) : Fragment() {

    lateinit var startTime: LocalTime
    lateinit var endTime: LocalTime

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.session_log_time, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        enter_start_time.setOnClickListener {
            val timePickerDialog: TimePickerDialog = TimePickerDialog(context,
                TimePickerDialog.OnTimeSetListener { _: TimePicker, hour: Int, minute: Int ->
                    val amPm: String
                    var amPmHour = hour
                    startTime = LocalTime.of(hour, minute)
                    when {
                        hour > 12 -> {
                            amPm = "PM"
                            amPmHour -= 12
                        }
                        hour == 12 -> {
                            amPm = "PM"
                        }
                        hour == 0 -> {
                            amPm = "AM"
                            amPmHour += 12
                        }
                        else -> {
                            amPm = "AM"
                        }
                    }
                    enter_start_time.text = String.format("%02d:%02d", amPmHour, minute) + amPm;
                }, 0,0, false);

            timePickerDialog.show()
        }

        enter_end_time.setOnClickListener {
            val timePickerDialog: TimePickerDialog = TimePickerDialog(context,
                TimePickerDialog.OnTimeSetListener { _: TimePicker, hour: Int, minute: Int ->
                    val amPm: String
                    var amPmHour = hour
                    endTime = LocalTime.of(hour, minute)
                    when {
                        hour > 12 -> {
                            amPm = "PM"
                            amPmHour -= 12
                        }
                        hour == 12 -> {
                            amPm = "PM"
                        }
                        hour == 0 -> {
                            amPm = "AM"
                            amPmHour += 12
                        }
                        else -> {
                            amPm = "AM"
                        }
                    }
                    enter_end_time.text = String.format("%02d:%02d", amPmHour, minute) + amPm;
                }, 0,0, false);

            timePickerDialog.show()
        }

        add_session_button.setOnClickListener {
            if (enter_start_time.text == "Start Time" || enter_end_time.text == "End Time") {
                //cannot be empty error
            } else if (endTime.isBefore(startTime)) {
                //endTime cannot be before startTime error
            } else {
                val start: LocalDateTime = LocalDateTime.of(LocalDate.now(), startTime)
                val end: LocalDateTime = LocalDateTime.of(LocalDate.now(), endTime)
                activity.addSession(start, end)
                successfulSessionAnimation()
            }
        }
    }

    fun resetText() {
        enter_start_time.text = getString(R.string.start_time)
        enter_end_time.text = getString(R.string.end_time)
    }

    /**
     * Animation to show that a session has been successfully added
     */
    private fun successfulSessionAnimation() {
        session_layout.animate().apply {
            interpolator = LinearInterpolator()
            duration = 200
            alpha(0f)
            setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    resetText()
                    outAnimation()
                }
            })
            start()
        }
    }

    private fun outAnimation() {
        session_layout.animate().apply {
            interpolator = LinearInterpolator()
            duration = 200
            alpha(1f)
            setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    resetText()
                }
            })
            start()
        }
    }
}