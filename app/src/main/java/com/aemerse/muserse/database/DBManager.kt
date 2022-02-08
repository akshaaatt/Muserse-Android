package com.aemerse.muserse.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.database.sqlite.SQLiteQueryBuilder

class DBManager(context: Context) {
    val dbName = "LikeMusic"
    val dbTable = "TableLike"
    val colID = "ID"
    val colTitle = "Title"
    val colArtist = "Artist"
    val colSongUrl = "SongUrl"
    val colCover = "Cover"
    val dbVersion = 1

    val sqlCreateTable = "CREATE TABLE IF NOT EXISTS " +
            dbTable +" (" + colID + " INTEGER PRIMARY KEY, " +
            colTitle + " TEXT, " + colArtist + " TEXT, " +
            colSongUrl + " TEXT, " + colCover + " IMAGE);"

    var sqlDB:SQLiteDatabase? = null

    init {
        val db = DatabaseHelper(context)
        sqlDB = db.writableDatabase
    }

    inner class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, dbName, null, dbVersion) {

        override fun onCreate(db: SQLiteDatabase?) {
            db!!.execSQL(sqlCreateTable)
        }

        override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
            db!!.execSQL("DROP TABLE IF EXISTS $dbTable")
        }

    }

    fun insert(values: ContentValues): Long {
        return sqlDB!!.insert(dbTable, "", values)
    }

    fun delete(selection: String, selectionArgs: Array<String>): Int {
        return sqlDB!!.delete(dbTable, selection, selectionArgs)
    }

    fun runQuery(columns: Array<String>, selection: String, selectionArgs: Array<String>, sortOrder: String): Cursor {
        val qb = SQLiteQueryBuilder()
        qb.tables = dbTable
        return qb.query(sqlDB, columns, selection, selectionArgs, null, null, sortOrder)
    }
}