package tech.davidburns.activitytracker

import android.content.Context
import tech.davidburns.activitytracker.interfaces.Database

object User {
    private val listeners: MutableList<ActivityListener> = mutableListOf()
    var name: String = "UNNAMED"
    lateinit var currentActivity: Activity
    lateinit var applicationContext: Context

    private lateinit var database: Database

    private val activitiesBack: MutableList<Activity> = mutableListOf()

    /**
     * Locally caches activities
     */
    val activities: MutableList<Activity>
        /**
         * @return cached activities
         */
        get() = activitiesBack

    fun setDatabase(database: Database) {
        this.database = database
    }

    /**
     * Create activity with given name and add to the database.
     * @param name of the [Activity] to create
     */
    fun addActivity(name: String, saveToDatabase: Boolean = true) =
        addActivity(Activity(name, activities.size), saveToDatabase) //Increases size of database by one, so does not need to be size - 1

    /**
     * Create activity with given name and add to the database.
     * @param activity to add
     */
    fun addActivity(activity: Activity, saveToDatabase: Boolean = true) {
        activitiesBack.add(activity)
        listeners.forEach { it.itemAdded(activities.size - 1) }
        if (saveToDatabase) database.addActivity(activity)
    }

    fun updateOrder(index: Int, order: Int) {
        database.orderUpdated(index, order)
    }

//    /**
//     * Swaps two activities by index
//     * @param from index of activity to swap
//     * @param to index to swap
//     */
//    fun moveActivity(from: Int, to: Int) {
//        val fromActivity = activitiesBack[from]
//        activitiesBack.removeAt(from)
//        if (from < to) {
//            activitiesBack.add(to, fromActivity)
//        } else { // Account for items shifting
//            activitiesBack.add(to - 1, fromActivity)
//        }
//        database.orderUpdated()
//    }

    /**
     * Add given session to the database attached to the given activity.
     * @param session to add to database
     * @param activityName to attach the session to
     */
    fun addSession(session: Session, activityName: String) =
        database.addSession(session, activityName)

    /**
     * Retrieve all [Session] belonging to a specific activity.
     * @return list of [Session] belonging to the activity given
     */
    fun getSessionsFromActivity(activityName: String): MutableList<Session> =
        database.getSessionsFromActivity(activityName)

    /**
     * Retrieve all [Session] belonging to a specific activity.
     * @return list of [Session] belonging to the activity given
     */
    fun getSessionsFromCurrentActivity(): MutableList<Session> =
        database.getSessionsFromActivity(currentActivity.name)

    /**
     * Add a listener who will be notified whenever activities list is changed
     * @param listener to be notified
     */
    fun addActivityListener(listener: ActivityListener) {
        listeners.add(listener)
    }

    /**
     * Remove a listener
     * @param listener to be removed
     */
    fun removeActivityListener(listener: ActivityListener) {
        listeners.remove(listener)
    }
}

interface ActivityListener {
    fun itemChanged(index: Int)
    fun itemRemoved(index: Int)
    fun itemAdded(index: Int)
    fun itemRangeAdded(start: Int, itemCount: Int)
}