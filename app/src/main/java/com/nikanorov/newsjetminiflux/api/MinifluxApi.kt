package com.nikanorov.newsjetminiflux.api

import com.nikanorov.newsjetminiflux.data.settings.UserPreferencesRepository
import com.nikanorov.newsjetminiflux.model.Feed
import com.nikanorov.newsjetminiflux.utils.removeTrailingSlash
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

//Miniflux API docs: https://miniflux.app/docs/api.html

class MinifluxAPI(
    scope: CoroutineScope,
    private val preferencesRepository: UserPreferencesRepository
) {

    private lateinit var serverURL: String
    private lateinit var authToken: String

    init {
        //and subscribe for the updates now
        scope.launch {
            preferencesRepository.userPreferencesFlow.collect {
                serverURL = it.minifluxApiUrl.removeTrailingSlash
                authToken = it.minifluxApiToken
            }
        }
    }


    private val client = HttpClient(OkHttp) {

        install(ContentNegotiation) {
            json(Json {
                prettyPrint = false
                isLenient = true
                ignoreUnknownKeys = true
            })
        }
        install(HttpTimeout) {
            requestTimeoutMillis = 5000
        }
        install(UserAgent) {
            agent = "MinifluxAndroid/"
        }

        defaultRequest {
            header("X-Auth-Token", authToken)
        }

        expectSuccess = true
    }

    suspend fun getFeeds(): List<FeedApi>? {
        return client.get("$serverURL/feeds").body()
    }

    suspend fun getEntries(limit: Int? = null, offset: Int? = null): FeedEntries? {
        return client.get("$serverURL/entries") {
            parameter("status", "unread")
            parameter("direction", "desc")
            limit?.let { parameter("limit", it) }
            offset?.let { parameter("offset", it) }
        }.body()
    }

    suspend fun getFeedEntries(
        feedId: Long,
        limit: Int? = null,
        offset: Int? = null
    ): FeedEntries? {
        return client.get("$serverURL/feeds/$feedId/entries") {
            parameter("direction", "desc")
            limit?.let { parameter("limit", it) }
            offset?.let { parameter("offset", it) }
        }.body()
    }

    suspend fun toggleEntryBookmark(entryId: Long): Boolean {
        return (client.put("$serverURL/entries/$entryId/bookmark").status.value == 204)
    }

    suspend fun updateEntries(entryIds: List<Long>, status: String): Boolean {
        val response: HttpResponse = client.put("$serverURL/entries") {
            contentType(ContentType.Application.Json)
            setBody(UpdateEntries(entry_ids = entryIds, status))
        }
        return (response.status.value == 204)
    }

    enum class PostStatus(val status: String) {
        READ("read"),
        UNREAD("unread"),
    }


    @Serializable
    data class UpdateEntries(val entry_ids: List<Long>, val status: String)

    @Serializable
    data class FeedApi(
        val id: Long,
        val user_id: Int,
        val title: String,
        val site_url: String,
        val feed_url: String
    )


    @Serializable
    data class Entry(
        val id: Long, val user_id: Int, val feed_id: Int, val title: String,
        val url: String, val comments_url: String, val author: String,
        val content: String, val hash: String, val published_at: String, val created_at: String,
        val status: String, val share_code: String, val starred: Boolean, val reading_time: Int,
        val feed: FeedApi
    )


    @Serializable
    data class FeedEntries(val total: Int, val entries: List<Entry>)
}

val MinifluxAPI.FeedApi.toFeed: Feed
    get() {
        return Feed(this.id, this.title, this.site_url, this.feed_url)
    }
