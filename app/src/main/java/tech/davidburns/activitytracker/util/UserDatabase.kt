package tech.davidburns.activitytracker.util

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class UserSchema {
    object ActivityTable {
        const val NAME: String = "activities"

        object Cols {
            const val ACTIVITYNAME = "activityname"
        }
    }
    object SessionTable {
        const val NAME: String = "sessions"
        object Cols {
            const val NAME = "name"
            const val LENGTH = "length"
            const val DAY = "day"
            const val START = "start"
        }
    }

}

private const val VERSION: Int = 1
private const val DATABASE_NAME = "UserBase.db"

class UserBaseHelper(private val context: Context):
    SQLiteOpenHelper(context, DATABASE_NAME, null, VERSION) {

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL("create table " + UserSchema.ActivityTable.NAME
                + "(" + " _id integer primary key autoincrement, "
                + UserSchema.ActivityTable.Cols.ACTIVITYNAME + ")")
        db?.execSQL("create table " + UserSchema.SessionTable.NAME
                + "(" + " _id integer primary key autoincrement, "
                + UserSchema.SessionTable.Cols.NAME + ", "
                + UserSchema.SessionTable.Cols.LENGTH + ", "
                + UserSchema.SessionTable.Cols.DAY + ", "
                + UserSchema.SessionTable.Cols.START + ")")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        TODO("Not yet implemented")
    }


}