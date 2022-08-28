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
                val feedsReadUnread: MinifluxAPI.ReadUnread? = api.getFeedsReadUnreadCount()

                feedsFromApi?.map {
                    it.toFeed.apply {
                        unreadCount = feedsReadUnread?.unreads?.get(id) ?: 0
                    }
                }?.sortedByDescending { it.unreadCount }?.//todo: lets temporary sort by unread count, later will create sorting filter
                let {
                    feeds.addAll(it)
                }

                Result.Success(feeds)
            } catch (e: Exception) {
                e.printStackTrace()
                Result.Error(IllegalArgumentException(e.message))
            }
        }
    }
}