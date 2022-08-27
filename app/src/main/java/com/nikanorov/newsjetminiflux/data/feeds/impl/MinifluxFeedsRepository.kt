package com.nikanorov.newsjetminiflux.data.feeds.impl

import com.nikanorov.newsjetminiflux.api.MinifluxAPI
import com.nikanorov.newsjetminiflux.api.toFeed
import com.nikanorov.newsjetminiflux.data.Result
import com.nikanorov.newsjetminiflux.data.feeds.FeedsRepository
import com.nikanorov.newsjetminiflux.model.Feed
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class MinifluxFeedsRepository(private var api: MinifluxAPI) : FeedsRepository {

    private val refreshIntervalMs: Long = 60000

    private val feedsFlow: Flow<List<Feed>> = flow {
        while (true) {
            when (val result = getFeeds()) {
                is Result.Success -> {
                    emit(result.data)
                }
                else -> {}
            }
            delay(refreshIntervalMs)
        }
    }

    override fun observeFeedsList(): Flow<List<Feed>> = feedsFlow

    override suspend fun getFeeds(): Result<List<Feed>> {
        return withContext(Dispatchers.IO) {
            val feeds = arrayListOf<Feed>()
            try {
                val feedsFromApi: List<MinifluxAPI.FeedApi>? = api.getFeeds()
                feedsFromApi?.map { it.toFeed }?.let {
                    feeds.addAll(it)
                }

                Result.Success(feeds)
            } catch (e: Exception) {
                Result.Error(IllegalArgumentException(e.message))
            }
        }
    }
}