package tech.davidburns.activitytracker.util

import android.util.Log
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import tech.davidburns.activitytracker.Activity
import tech.davidburns.activitytracker.Session
import tech.davidburns.activitytracker.User
import tech.davidburns.activitytracker.interfaces.Database
import java.time.Instant
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
class FirestoreDatabase(private val firebaseUser: FirebaseUser) : Database() {
    private val db: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    init {
        db.collection("$userPath/${firebaseUser.uid}/$activityPath").orderBy("order")
            .addSnapshotListener { value, e ->
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e)
                    return@addSnapshotListener
                }

                for (dc in value!!.documentChanges) {
                    when (dc.type) {
                        DocumentChange.Type.ADDED -> {
                            addInternalActivity(
                                Activity(
                                    dc.document.data["name"] as String,
                                    dc.document.data["id"] as Int
                                )
                            )
                            Log.d(TAG, "New Activity: ${dc.document.data}")
                        }
                        DocumentChange.Type.MODIFIED -> Log.d(
                            TAG,
                            "Modified Activity: ${dc.document.data}, NOT IMPLEMENTED"
                        )
                        DocumentChange.Type.REMOVED -> {
//                            deleteInternalActivity((dc.document.data["order"] as Long).toInt())
                            Log.d(TAG, "Removed Activity: ${dc.document.data}")
                        }
                    }
                }
            }
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
                        val start = document.data["start"] as Long
                        list.add(Session(name, length, start))
                    } catch (ex: Exception) {
                        ex.printStackTrace()
                    }
                }
            }
        return list
    }

    //Must be called before this activity is added to the local list because
    //the order is defined as the size of the list, and not size - 1
    override fun addActivity(activity: Activity) {
        val activityHashMap = hashMapOf(
            "name" to activity.name,
            "created" to Instant.now().epochSecond,
            "order" to User.activities.size,
            "id" to activity.id
        )
        db.document("$userPath/${firebaseUser.uid}/$activityPath/${activity.name}")
            .set(activityHashMap)
            .addOnSuccessListener { Log.d(TAG, "SUCCESS") }
            .addOnFailureListener { e -> Log.w(TAG, "Error writing document", e) }
    }

    override fun addSession(session: Session, activityName: String) {
        val sessionHashMap = hashMapOf(
            "name" to session.name,
            "length" to session.length.toMillis(),
            "start" to session.start.atZone(ZoneId.systemDefault())?.toEpochSecond()
        )
        db.collection("$userPath/${firebaseUser.uid}/$activityPath/$activityName/$sessionPath")
            .add(sessionHashMap)
            .addOnSuccessListener { Log.d(TAG, "SUCCESS") }
            .addOnFailureListener { e -> Log.w(TAG, "Error writing document", e) }
    }

    override fun orderUpdated(index: Int) {
        db.document("$userPath/${firebaseUser.uid}/$activityPath/${activities[index].name}")
            .update("order", index)
            .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully updated") }
            .addOnFailureListener { e -> Log.w(TAG, "Error updating document", e) }
    }

    override fun executeListDiff(activityListDiffMap: ListDiffMap<Activity>) {
        db.runBatch { batch ->
            for ((activity, result) in activityListDiffMap) {
                val activityDocument =
                    db.document("$userPath/${firebaseUser.uid}/$activityPath/${activity.name}")
                when (result.state) {
                    ListDiffEnum.MOVED_TO -> batch.update(
                        activityDocument,
                        mapOf("order" to result.index)
                    )
                    ListDiffEnum.DELETED_AT -> batch.delete(activityDocument)
                }
            }
        }.addOnCompleteListener {
            Log.d(TAG, "Successful Batch")
        }
    }

//    override fun setUserInfo() {
//        val userHashMap = hashMapOf(
//            "name" to firebaseUser.displayName
//        )
//        db.document("$userPath/${firebaseUser.uid}")
//            .set(userHashMap)
//    }
}