package com.nikanorov.newsjetminiflux.data.posts.impl

import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.nikanorov.newsjetminiflux.api.MinifluxAPI
import com.nikanorov.newsjetminiflux.data.Result
import com.nikanorov.newsjetminiflux.data.posts.PostDataSource
import com.nikanorov.newsjetminiflux.data.posts.PostsRepository
import com.nikanorov.newsjetminiflux.model.*
import kotlinx.coroutines.*

class MinifluxPostsRepository(private var api: MinifluxAPI) : PostsRepository {

    private val TAG = "miniflux-PostRepo"

    //todo: implement cache
    //for now lets store all posts in memory, let's introduce correct approach with cache later
    private val allPosts = HashMap<Long, Post>(mapOf())

    override suspend fun getPost(postId: Long?): Result<Post> {
        return withContext(Dispatchers.IO) {

            val post = allPosts[postId]
            if (post == null) {
                Result.Error(IllegalArgumentException("Post not found"))
            } else {
                Result.Success(post)
            }
        }
    }

    override suspend fun getPostsFeed(feedId: Long): Result<PostsFeed> {
        return withContext(Dispatchers.IO) {
            Log.d(TAG, "start getting feeds... feedId: $feedId")
            try {
                val postFeed =
                    PostsFeed(
                        pagingPosts = Pager(PagingConfig(pageSize = PostDataSource.NUMBER_OF_POST_PER_REQUEST)) {
                            PostDataSource(this@MinifluxPostsRepository, feedId)
                        }.flow
                    )
                Result.Success(postFeed)
            } catch (e: Exception) {
                Log.d(TAG, "exception: ${e.stackTrace}")
                Result.Error(e)
            }
        }
    }


    override suspend fun toggleFavorite(postId: Long) {
        //FIXME: update the above code to reflect real storage
        withContext(Dispatchers.IO) {
            api.toggleEntryBookmark(postId)
        }
    }

    override suspend fun toggleRead(postId: Long) {
        //todo: update the to reflect real cache storage
        withContext(Dispatchers.IO) {
            allPosts[postId]?.let {
                api.updateEntries(
                    listOf(postId),
                    if (it.read) MinifluxAPI.PostStatus.UNREAD.status else MinifluxAPI.PostStatus.READ.status
                )
                allPosts[postId] = it.copy(read = !it.read)
            }
        }
    }

    override suspend fun getPosts(feedId: Long, limit: Int, offset: Int): List<Post> {
        Log.d(TAG, "getPosts, feedId: $feedId")

        val feedEntries =
            if (feedId != -1L) api.getFeedEntries(feedId, limit, offset) else api.getEntries(
                limit,
                offset
            )

        val postsList = arrayListOf<Post>()

        feedEntries?.entries?.forEach {

            val author = PostAuthor(it.author, it.feed.site_url)

            val post = Post(
                it.id,
                it.title,
                "",
                it.url,
                publication = Publication(it.feed.title, ""),
                content = it.content,
                metadata = Metadata(
                    author = author,
                    date = it.published_at,
                    readTimeMinutes = it.reading_time
                ),
                starred = it.starred,
                imageId = null,
                imageThumbId = null
            )

            allPosts[it.id] = post

            postsList.add(post)
        }
        return postsList
    }
}

