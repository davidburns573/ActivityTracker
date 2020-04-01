package tech.davidburns.activitytracker

import com.google.firebase.auth.FirebaseUser
import tech.davidburns.activitytracker.interfaces.Database

object User {
    var authenticate: FirebaseUser? = null
    var name: String = "UNNAMED"
    lateinit var currentActivity: Activity

    lateinit var database: Database

    fun addActivity(name: String) {
        val activity = Activity(name)
        database.addActivity(activity)
    }
}