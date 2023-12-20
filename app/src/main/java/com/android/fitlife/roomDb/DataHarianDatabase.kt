package com.android.fitlife.roomDb

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [DataHarian::class], version = 1, exportSchema = false)
abstract class DataHarianDatabase : RoomDatabase(){
    abstract fun dataHarianDao(): DataHarianDao

    companion object {
        @Volatile
        private  var INSTANCE: DataHarianDatabase? = null
        fun getDatabase(context: Context): DataHarianDatabase? {
            if (INSTANCE == null){
                synchronized(DataHarianDatabase::class.java){
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        DataHarianDatabase::class.java, "data_harian"
                    ).build()
                }
            }
            return INSTANCE
        }
    }
}