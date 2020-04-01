package tech.davidburns.activitytracker.util

import android.content.Context
import android.util.Log
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import tech.davidburns.activitytracker.Activity
import tech.davidburns.activitytracker.Session
import tech.davidburns.activitytracker.interfaces.Database
import java.time.ZoneId

const val userPath = "users"
const val activityPath = "activities"
const val sessionPath = "sessions"
const val TAG = "FIRESTORE_DATABASE"

/**
 * Interfaces with the Firestore Database.
 * Firestore database only allows reads and writes to authenticated users.
 * These authenticated users can only view files that contain their unique user id.
 */
class FirestoreDatabase(private val firebaseUser: FirebaseUser, context: Context) :
    Database(context) {
    private lateinit var db: FirebaseFirestore

    override val activities: MutableList<Activity>
        get() {
            val mutableList = mutableListOf<Activity>()
            db.collection("$userPath/${firebaseUser.uid}/$activityPath")
                .get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        try {
                            val name = document.id
                            val activity = Activity(name)
                            mutableList.add(activity)
                            Log.d(TAG, "${document.id} => ${document.data}")
                        } catch (ex: Exception) {
                            ex.printStackTrace()
                        }
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d(TAG, "Error getting documents: ", exception)
                }
            return mutableList
        }

    override fun initializeDatabase(context: Context) {
        db = Firebase.firestore
    }

    override fun setUserInfo() {
        val userHashMap = hashMapOf(
            "name" to firebaseUser.displayName
        )
        db.document("$userPath/${firebaseUser.uid}")
            .set(userHashMap)
    }

    override fun getSessionsFromActivity(activityName: String): MutableList<Session> {
        val list = mutableListOf<Session>()
        db.collection("$userPath/${firebaseUser.uid}/$activityPath/$activityName/$sessionPath")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    try {
                        val name = document.data["name"] as String
                        val length = document.data["length"] as Long
                        val day = document.data["day"] as Long
                        val start = document.data["start"] as Long?
                        list.add(Session(name, length, day, start))
                    } catch (ex: Exception) {
                        ex.printStackTrace()
                    }
                }
            }
        return list
    }

    override fun addActivity(activity: Activity) {
        db.document("$userPath/${firebaseUser.uid}/$activityPath/${activity.name}")
            .set(hashMapOf("name" to activity.name))
            .addOnSuccessListener { Log.d(TAG, "SUCCESS") }
            .addOnFailureListener { e -> Log.w(TAG, "Error writing document", e) }
    }

    override fun addSession(session: Session, activityName: String) {
        val sessionHashMap = hashMapOf(
            "name" to session.name,
            "length" to session.length.toMillis(),
            "day" to session.day.toEpochDay(),
            "start" to session.start?.atZone(ZoneId.systemDefault())?.toEpochSecond()
        )
        db.collection("$userPath/${firebaseUser.uid}/$activityPath/$activityName/$sessionPath")
            .add(sessionHashMap)
            .addOnSuccessListener { Log.d(TAG, "SUCCESS") }
            .addOnFailureListener { e -> Log.w(TAG, "Error writing document", e) }
    }
}
