package tech.davidburns.activitytracker.util

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import tech.davidburns.activitytracker.Activity
import tech.davidburns.activitytracker.Session
import tech.davidburns.activitytracker.interfaces.Database

class FirestoreDatabase: Database {
    private lateinit var db: FirebaseFirestore

    override suspend fun initializeDatabase() {
        db = Firebase.firestore
    }

    override suspend fun getActivities(): List<Activity> {
        TODO("Not yet implemented")
    }

    override suspend fun getSessionsFromActivity(activity: Activity): List<Session> {
        TODO("Not yet implemented")
    }

}