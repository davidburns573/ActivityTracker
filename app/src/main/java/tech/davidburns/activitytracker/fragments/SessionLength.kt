package tech.davidburns.activitytracker.fragments

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.session_length.*
import tech.davidburns.activitytracker.Activity
import tech.davidburns.activitytracker.R
import java.time.Duration


class SessionLength(val activity: Activity): Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.session_length, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        add_session_button.setOnClickListener {
            if (enter_hours.text.toString() == "" && enter_minutes.text.toString() == "") {
                //error message
                resetText()
            } else if (enter_hours.text.toString() == ""){
                val minutes = (enter_minutes.text).toString().toLong()
                val duration: Duration = Duration.ofMinutes(minutes)
                activity.addSession(duration)
                successfulSessionAnimation()
            } else if (enter_minutes.text.toString() == "") {
                val hours = (enter_hours.text).toString().toLong()
                val duration: Duration = Duration.ofHours(hours)
                activity.addSession(duration)
                successfulSessionAnimation()
            } else {
                val hours = (enter_hours.text).toString().toLong()
                val minutes = (enter_minutes.text).toString().toLong()
                val duration: Duration = Duration.ofHours(hours).plus(Duration.ofMinutes(minutes))
                activity.addSession(duration)
                successfulSessionAnimation()
            }
        }
    }

    /**
     * Resets text properties of activity_detail
     */
    private fun resetText() {
        enter_hours.setText("")
        enter_minutes.setText("")
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