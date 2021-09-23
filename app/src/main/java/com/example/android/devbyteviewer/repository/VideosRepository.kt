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

package com.example.android.devbyteviewer.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.example.android.devbyteviewer.database.VideosDatabase
import com.example.android.devbyteviewer.database.asDomainModel
import com.example.android.devbyteviewer.domain.Video
import com.example.android.devbyteviewer.network.Network
import com.example.android.devbyteviewer.network.asDatabaseModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// initiate repo for our data
// 'dependency injection: DB passed via the constructor
class VideosRepository(private val database: VideosDatabase) {

    // getting data from DB and updating data in memory (and, by extension via LiveData, the UI)
    val videos: LiveData<List<Video>> = Transformations.map(database.videoDao.getVideos()) {

        // use DB class extension functions to convert from 'DB video' to 'domain video' items
        it.asDomainModel()
    }

    // updating data in DB
    suspend fun refreshVideos() {
        withContext(Dispatchers.IO) {
            val playlist = Network.devbytes.getPlaylist().await()

            // DAO method 'insertAll' allows to be called with 'varargs' --> use 'spread operator'
            // (*SOMEVARNAME) to turn a List<> into separate 'varargs')
            database.videoDao.insertAll(*playlist.asDatabaseModel())
        }
    }

}

