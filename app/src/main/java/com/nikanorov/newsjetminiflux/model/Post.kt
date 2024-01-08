package com.nikanorov.newsjetminiflux.model

import androidx.annotation.DrawableRes
import kotlinx.datetime.Instant

data class Post(
    val id: Long,
    val title: String,
    val subtitle: String? = null,
    val url: String,
    val publication: Publication? = null,
    val metadata: Metadata,
    val paragraphs: List<Paragraph> = emptyList(),
    val content: String = "",
    val starred: Boolean = false,
    val read: Boolean = false,
    val feed: Feed,
    @DrawableRes val imageId: Int?,
    @DrawableRes val imageThumbId: Int?
)

data class Metadata(
    val author: PostAuthor,
    val date: Instant,
    val readTimeMinutes: Int
)

data class PostAuthor(
    val name: String,
    val url: String? = null
)

data class Publication(
    val name: String,
    val logoUrl: String
)

data class Paragraph(
    val type: ParagraphType,
    val text: String,
    val markups: List<Markup> = emptyList()
)

data class Markup(
    val type: MarkupType,
    val start: Int,
    val end: Int,
    val href: String? = null
)

enum class MarkupType {
    Link,
    Code,
    Italic,
    Bold,
}

enum class ParagraphType {
    Title,
    Caption,
    Header,
    Subhead,
    Text,
    CodeBlock,
    Quote,
    Bullet,
}
