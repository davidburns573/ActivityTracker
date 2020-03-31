package tech.davidburns.activitytracker

import com.google.firebase.auth.FirebaseUser
import tech.davidburns.activitytracker.interfaces.Database

object User {
    var authenticate: FirebaseUser? = null
    var name: String = "UNNAMED"

    var activities: MutableList<Activity> = mutableListOf()

    lateinit var database: Database

    fun addActivity(name: String) {
        val activity = Activity(name)
        activities.add(activity)
        database.addActivity(activity)
    }
}