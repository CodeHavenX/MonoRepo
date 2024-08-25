package com.codehavenx.alpaca.frontend.appcore.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

/**
 * The Room database for this app.
 */
@Database(
    entities = [
        // Here we list all the entities that we want to store in the database
    ],
    version = 1,
    exportSchema = true,
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    // Here we define all the DAOs that we want to use
}
