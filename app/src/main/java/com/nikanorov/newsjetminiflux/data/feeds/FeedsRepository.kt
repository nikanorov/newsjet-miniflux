package com.nikanorov.newsjetminiflux.data.feeds

import com.nikanorov.newsjetminiflux.data.Result
import com.nikanorov.newsjetminiflux.model.Feed
import kotlinx.coroutines.flow.Flow

interface FeedsRepository {

    suspend fun getFeeds(): Result<List<Feed>>

    fun observeFeedsList(): Flow<List<Feed>>

}
