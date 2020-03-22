package tech.davidburns.activitytracker.util

import android.app.Activity
import android.content.Context
import tech.davidburns.activitytracker.R

object Authentication {
    fun isDatabaseEnabled(activity: Activity?) : Boolean {
        val sharedPref =
            activity?.getPreferences(Context.MODE_PRIVATE) ?: return true
        return sharedPref.getBoolean(activity.getString(R.string.deny_database_key), true)
    }

    fun denyDatabase(activity: Activity?) {
        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE) ?: return
        with (sharedPref.edit()) {
            putBoolean(activity.getString(R.string.deny_database_key), false)
            apply()
        }
    }

    fun enableDatabase(activity: Activity?) {
        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE) ?: return
        with (sharedPref.edit()) {
            putBoolean(activity.getString(R.string.deny_database_key), true)
            apply()
        }
    }
}