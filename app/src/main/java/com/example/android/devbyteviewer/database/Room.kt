/*
 * Copyright 2018, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.example.android.devbyteviewer.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface VideoDao {

    // fetch all available videos from DB
    @Query("select * from databasevideo")
    fun getVideos(): LiveData<List<DatabaseVideo>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg videos: DatabaseVideo)

}

@Database(entities = [DatabaseVideo::class], version = 1)
abstract class VideosDatabase : RoomDatabase() {

    // DB has a reference to the DAO (abstract, as it's but an interface
    abstract val videoDao: VideoDao

}

// singleton instance of the DB
private lateinit var INSTANCE: VideosDatabase

// getter to retrieve a reference to the (singleton) DB object
fun getDatabase(context: Context): VideosDatabase {

    // ensure thread safety
    synchronized(VideosDatabase::class.java) {

        // DB already initialized?
        if (!::INSTANCE.isInitialized) {

            // nope - initialize it here (once and for all)
            INSTANCE = Room.databaseBuilder(context.applicationContext,
                VideosDatabase::class.java,
                "videos").build()
        }

    }

    // return reference to (initialized) DB object
    return INSTANCE
}