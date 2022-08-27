package com.nikanorov.newsjetminiflux.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nikanorov.newsjetminiflux.data.settings.UserPreferencesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


data class SettingsUiState(
    val minifluxApiUrl: String = "",
    val minifluxApiToken: String = "",
    val loading: Boolean = false,
)

class SettingsViewModel constructor(
    private val preferencesRepository: UserPreferencesRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(SettingsUiState(loading = true))
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    private val userPreferencesFlow = preferencesRepository.userPreferencesFlow

    init {
        _uiState.update { it.copy(loading = true) }

        viewModelScope.launch {
            userPreferencesFlow.collect { prefs ->
                _uiState.update {
                    it.copy(
                        loading = false,
                        minifluxApiUrl = prefs.minifluxApiUrl,
                        minifluxApiToken = prefs.minifluxApiToken
                    )
                }

            }
        }
    }


    fun updateURL(value: String) {
        _uiState.update { it.copy(minifluxApiUrl = value) }
    }

    fun updateToken(value: String) {
        _uiState.update { it.copy(minifluxApiToken = value) }
    }


    fun saveData() {
        viewModelScope.launch {
            preferencesRepository.updateMinifluxApiToken(_uiState.value.minifluxApiToken.trim())
        }
        viewModelScope.launch {
            preferencesRepository.updateMinifluxApiUrl(_uiState.value.minifluxApiUrl.trim())
        }
    }

}