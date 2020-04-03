package tech.davidburns.activitytracker

import android.content.Context
import com.google.firebase.auth.FirebaseUser
import tech.davidburns.activitytracker.interfaces.Database

object User {
    var authenticate: FirebaseUser? = null
    var name: String = "UNNAMED"
    lateinit var currentActivity: Activity
    lateinit var applicationContext: Context

    lateinit var database: Database
}