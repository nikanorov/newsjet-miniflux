package com.nikanorov.newsjetminiflux.data.posts

import com.nikanorov.newsjetminiflux.data.Result
import com.nikanorov.newsjetminiflux.model.Post
import com.nikanorov.newsjetminiflux.model.PostsFeed

/**
 * Interface to the Posts data layer.
 */
interface PostsRepository {

    /**
     * Get a specific JetNews post.
     */
    suspend fun getPost(postId: Long?): Result<Post>

    /**
     * Get JetNews posts.
     */
    suspend fun getPostsFeed(feedId: Long = -1L): Result<PostsFeed>

    /**
     * Toggle a postId to be a favorite or not.
     */
    suspend fun toggleFavorite(postId: Long)

    /**
     * Toggle a postId to be a read or not.
     */
    suspend fun toggleRead(postId: Long)
    suspend fun getPosts(feedId: Long, limit: Int, offset: Int): List<Post>

}
