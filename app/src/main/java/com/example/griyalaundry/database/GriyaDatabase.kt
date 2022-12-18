package com.example.griyalaundry.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [(Griya::class)], version = 1)
abstract class GriyaDatabase : RoomDatabase() {

    abstract fun griyaDao(): GriyaDao

    companion object {

        private var INSTANCE: GriyaDatabase? = null

        fun getInstance(context: Context): GriyaDatabase {
            if (INSTANCE == null) {
                synchronized(this) {
                    INSTANCE = Room.databaseBuilder(
                        context,
                        GriyaDatabase::class.java, "griya.db"
                    )
                        .allowMainThreadQueries()
                        .build()
                }
            }
            return INSTANCE as GriyaDatabase
        }

        fun destroyInstance() {
            INSTANCE = null
        }

    }
}