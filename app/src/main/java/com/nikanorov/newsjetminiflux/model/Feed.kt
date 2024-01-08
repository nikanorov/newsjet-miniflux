package com.nikanorov.newsjetminiflux.model

data class Feed (
    val id: Long,
    val title: String,
    val siteUrl: String,
    val feedUrl: String,
    val unreadCount: Int
)