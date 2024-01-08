package com.nikanorov.newsjetminiflux.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nikanorov.newsjetminiflux.R
import com.nikanorov.newsjetminiflux.data.Result
import com.nikanorov.newsjetminiflux.data.posts.PostsRepository
import com.nikanorov.newsjetminiflux.model.Post
import com.nikanorov.newsjetminiflux.model.PostsFeed
import com.nikanorov.newsjetminiflux.utils.ErrorMessage
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

/**
 * UI state for the Home route.
 *
 * This is derived from [HomeViewModelState], but split into two possible subclasses to more
 * precisely represent the state available to render the UI.
 */
sealed interface HomeUiState {

    val isLoading: Boolean
    val errorMessages: List<ErrorMessage>
    val searchInput: String

    /**
     * There are no posts to render.
     *
     * This could either be because they are still loading or they failed to load, and we are
     * waiting to reload them.
     */
    data class NoPosts(
        override val isLoading: Boolean,
        override val errorMessages: List<ErrorMessage>,
        override val searchInput: String
    ) : HomeUiState

    /**
     * There are posts to render, as contained in [postsFeed].
     *
     * There is guaranteed to be a [selectedPost], which is one of the posts from [postsFeed].
     */
    data class HasPosts(
        val postsFeed: PostsFeed,
        val selectedPost: Post?,
        val isArticleOpen: Boolean,
        val favorites: Set<Long>,
        override val isLoading: Boolean,
        override val errorMessages: List<ErrorMessage>,
        override val searchInput: String
    ) : HomeUiState
}

/**
 * An internal representation of the Home route state, in a raw form
 */
private data class HomeViewModelState(
    val postsFeed: PostsFeed? = null,
    val selectedPostId: Long? = null, // TODO back selectedPostId in a SavedStateHandle
    val isArticleOpen: Boolean = false,
    val favorites: Set<Long> = emptySet(),
    val isLoading: Boolean = false,
    val errorMessages: List<ErrorMessage> = emptyList(),
    val searchInput: String = "",
    val selectedPost: Post? = null,
) {

    /**
     * Converts this [HomeViewModelState] into a more strongly typed [HomeUiState] for driving
     * the ui.
     */
    fun toUiState(): HomeUiState =
        if (postsFeed == null) {
            HomeUiState.NoPosts(
                isLoading = isLoading,
                errorMessages = errorMessages,
                searchInput = searchInput
            )
        } else {
            HomeUiState.HasPosts(
                postsFeed = postsFeed,
                // Determine the selected post. This will be the post the user last selected.
                // If there is none (or that post isn't in the current feed), default to the
                // highlighted post
                selectedPost = selectedPost,
                isArticleOpen = isArticleOpen,
                favorites = favorites,
                isLoading = isLoading,
                errorMessages = errorMessages,
                searchInput = searchInput
            )
        }
}

/**
 * ViewModel that handles the business logic of the Home screen
 */
class HomeViewModel(
    private val postsRepository: PostsRepository, private val feedId: Long = -1L
) : ViewModel() {

    private val viewModelState = MutableStateFlow(HomeViewModelState(isLoading = true, errorMessages = emptyList()))

    // UI state exposed to the UI
    val uiState = viewModelState
        .map { it.toUiState() }
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value.toUiState()
        )


    init {
        refreshPosts()
    }


    /**
     * Refresh posts and update the UI state accordingly
     */
    fun refreshPosts() {
        // Ui state is refreshing
        viewModelState.update { it.copy(isLoading = true, errorMessages = emptyList()) }

        viewModelScope.launch {
            val result = postsRepository.getPostsFeed(feedId)

            viewModelState.update {
                when (result) {
                    is Result.Success -> {
                        it.copy(postsFeed = result.data, isLoading = false, errorMessages = emptyList())
                    }

                    is Result.Error -> {
                        val errorMessages = it.errorMessages + ErrorMessage(
                            id = UUID.randomUUID().mostSignificantBits,
                            messageId = R.string.load_error
                        )
                        it.copy(errorMessages = errorMessages, isLoading = false)
                    }
                }
            }
        }
    }


    /**
     * Toggle favorite of a post
     */
    fun toggleFavourite(postId: Long) {
        viewModelScope.launch {
            postsRepository.toggleFavorite(postId)
        }
    }

    /**
     * Toggle read mark of a post
     */
    fun toggleRead(postId: Long) {
        viewModelScope.launch {
            postsRepository.toggleRead(postId)
        }
    }


    /**
     * Selects the given article to view more information about it.
     */
    fun selectArticle(postId: Long) {
        // Treat selecting a detail as simply interacting with it
        interactedWithArticleDetails(postId)
    }

    /**
     * Notify that an error was displayed on the screen
     */
    fun errorShown(errorId: Long) {
        viewModelState.update { currentUiState ->
            val errorMessages = currentUiState.errorMessages.filterNot { it.id == errorId }
            currentUiState.copy(errorMessages = errorMessages)
        }
    }

    fun showError(errorText: String) {
        viewModelState.update { currentUiState ->
            val errorMessages = currentUiState.errorMessages + ErrorMessage(
                id = UUID.randomUUID().mostSignificantBits,
                messageId = when {
                    errorText.isNotEmpty() -> R.string.error_with_details
                    else -> R.string.load_error
                },
                messageText = errorText
            )
            currentUiState.copy(errorMessages = errorMessages)
        }
    }


    /**
     * Notify that the user interacted with the feed
     */
    fun interactedWithFeed() {
        viewModelState.update {
            it.copy(isArticleOpen = false)
        }
    }

    /**
     * Notify that the user interacted with the article details
     */
    fun interactedWithArticleDetails(postId: Long) {
        viewModelScope.launch {
            val result = postsRepository.getPost(postId)

            viewModelState.update {
                it.copy(
                    selectedPostId = postId,
                    isArticleOpen = true,
                    selectedPost = when (result) {
                        is Result.Success -> result.data
                        is Result.Error -> null
                    }
                )
            }
        }
    }

    /**
     * Notify that the user updated the search query
     */
    fun onSearchInputChanged(searchInput: String) {
        viewModelState.update {
            it.copy(searchInput = searchInput)
        }
    }


}
