package tech.davidburns.activitytracker

import android.view.View
import kotlinx.android.synthetic.main.activity.view.*
import java.time.Duration
import java.time.LocalDateTime

class Activity(var name: String) {
    var sessions: MutableList<Session> = mutableListOf()
    val statistics: Statistics = Statistics(sessions)
    var view: View? = null

    fun addSession(length: Duration) {
        sessions.add(Session(length))
    }

    fun addSession(start: LocalDateTime, end: LocalDateTime) {
        sessions.add(Session(start, end))
    }

    fun initView(view: View) {
        this.view = view
        view.setOnClickListener {

        }
        view.btnStart.setOnClickListener {

        }
    }
}