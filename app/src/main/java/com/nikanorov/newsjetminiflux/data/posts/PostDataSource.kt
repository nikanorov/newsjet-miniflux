package com.nikanorov.newsjetminiflux.data.posts

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.nikanorov.newsjetminiflux.model.Post

class PostDataSource(private val postsRepository: PostsRepository, private val feedId: Long) :
    PagingSource<Int, Post>() {
    private val TAG = "miniflux-PostDataSource"

    override fun getRefreshKey(state: PagingState<Int, Post>): Int? {
        return state.anchorPosition
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Post> {
        return try {

            val pageNumber = params.key ?: 0
            val prevKey = if (pageNumber > 0) pageNumber - 1 else null
            val response = postsRepository.getPosts(feedId, params.loadSize, pageNumber * params.loadSize)

            // initial load size = 3 * CONTACT_HISTORY_PAGE_SIZE
            // ensure we're not requesting duplicating items, at the 2nd request
            val nextKey = if (response.isNotEmpty()) pageNumber + (params.loadSize / NUMBER_OF_POST_PER_REQUEST)
            else null

            LoadResult.Page(
                data = response,
                prevKey = prevKey,
                nextKey = nextKey
            )
        } catch (e: Exception) {
            Log.d(TAG, "Paging data source exception: ${e.message}")
            LoadResult.Error(e)
        }
    }

    companion object {
        const val NUMBER_OF_POST_PER_REQUEST = 50
    }
}