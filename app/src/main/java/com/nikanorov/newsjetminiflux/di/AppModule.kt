package com.nikanorov.newsjetminiflux.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.nikanorov.newsjetminiflux.USER_PREFERENCES_NAME
import com.nikanorov.newsjetminiflux.api.MinifluxAPI
import com.nikanorov.newsjetminiflux.data.feeds.FeedsRepository
import com.nikanorov.newsjetminiflux.data.feeds.impl.MinifluxFeedsRepository
import com.nikanorov.newsjetminiflux.data.posts.PostsRepository
import com.nikanorov.newsjetminiflux.data.posts.impl.MinifluxPostsRepository
import com.nikanorov.newsjetminiflux.data.settings.UserPreferencesRepository
import com.nikanorov.newsjetminiflux.ui.feeds.FeedsViewModel
import com.nikanorov.newsjetminiflux.ui.home.HomeViewModel
import com.nikanorov.newsjetminiflux.ui.settings.SettingsViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    factory { CoroutineScope(Dispatchers.IO + SupervisorJob()) }

    single { providePreferencesDataStore(androidContext()) }
    single { MinifluxAPI(get(), get()) }
    single<PostsRepository> { MinifluxPostsRepository(get()) }
    single<FeedsRepository> { MinifluxFeedsRepository(get()) }

    factory { UserPreferencesRepository(get()) }

    viewModel { parameters -> HomeViewModel(get(), parameters.get()) }
    viewModel { FeedsViewModel(get()) }
    viewModel { SettingsViewModel(get()) }
}


fun providePreferencesDataStore(appContext: Context): DataStore<Preferences> {
    return PreferenceDataStoreFactory.create (corruptionHandler = ReplaceFileCorruptionHandler (produceNewData = { emptyPreferences()}),
        scope = CoroutineScope(Dispatchers.IO + SupervisorJob()), produceFile = {appContext.preferencesDataStoreFile(USER_PREFERENCES_NAME)} )
}

