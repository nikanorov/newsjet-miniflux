package com.nikanorov.newsjetminiflux.ui.feeds

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nikanorov.newsjetminiflux.data.feeds.FeedsRepository
import com.nikanorov.newsjetminiflux.model.Feed
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

data class FeedsUiState(
    val loading: Boolean = false,
)


class FeedsViewModel(feedsRepository: FeedsRepository) : ViewModel() {

    var feeds: StateFlow<List<Feed>> = feedsRepository.observeFeedsList().stateIn(
        scope = viewModelScope,
        initialValue = arrayListOf(),
        started = SharingStarted.WhileSubscribed(5000),
    )

}
