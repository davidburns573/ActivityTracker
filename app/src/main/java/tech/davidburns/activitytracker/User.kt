package tech.davidburns.activitytracker

import com.google.firebase.auth.FirebaseUser

//class User(var name: String) {
//    constructor(firebaseUser: FirebaseUser) : this(
//        firebaseUser.displayName ?: "UNNAMED"
//    )
//
//    var activities: MutableList<Activity> = mutableListOf()
//
//    fun addActivity(name: String) {
//        activities.add(Activity(name))
//    }
//}

object User {
    var name: String = "UNNAMED"
    var firebaseUser: FirebaseUser? = null

    val activities: MutableList<Activity> = mutableListOf()

    fun addActivity(name: String) {
        activities.add(Activity(name))
    }
}