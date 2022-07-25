package edu.msudenver.colormatch

/*
 * CS3013 - Mobile App Dev. - Summer 2022
 * Instructor: Thyago Mota
 * Student(s):
 * Description: App 03 - DBHelper (model) class
 */

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.io.Serializable
import java.text.SimpleDateFormat

class DBHelper(context: Context): SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION), Serializable {

    companion object {
        const val DATABASE_NAME = "ColorMatch.db"
        const val DATABASE_VERSION = 1
        val ISO_FORMAT = SimpleDateFormat("yyyy-MM-dd")
        val USA_FORMAT = SimpleDateFormat("MM/dd/yyyy")
    }

    override fun onCreate(db: SQLiteDatabase?) {
        // create the table
        db?.execSQL("""
            CREATE TABLE scores ( 
                date     TEXT NOT NULL, 
                level    INTEGER NOT NULL,
                value    DOUBLE NOT NULL)
        """)

        // populate the table with a few items
        db?.execSQL("""
            INSERT INTO scores VALUES 
                ("2022-07-20", 0, .5)
        """)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        // drop the table
        db?.execSQL("""
            DROP TABLE IF EXISTS scores
        """)

        // then call "onCreate" again
        onCreate(db)
    }
}