package tech.davidburns.activitytracker.util

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class ActivitySchema {
    object ActivityTable {
        const val NAME: String = "activities"

        object Cols {
            val ACTIVITYNAME = "activityname"
        }
    }
}

private const val VERSION: Int = 1
private const val DATABASE_NAME = "activityBase.db"

class ActivityBaseHelper(private val context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, VERSION) {

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL("create table " + ActivitySchema.ActivityTable.NAME
                + "(" + " _id integer primary key autoincrement, "
                + ActivitySchema.ActivityTable.Cols.ACTIVITYNAME + ")")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        TODO("Not yet implemented")
    }


}