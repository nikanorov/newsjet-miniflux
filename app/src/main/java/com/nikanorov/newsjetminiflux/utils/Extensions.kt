package com.nikanorov.newsjetminiflux.utils

import android.text.format.DateUtils
import kotlinx.datetime.Instant

val String.removeTrailingSlash:String
    get(){
        if (this.endsWith("/")) {
            return this.substring(0, this.length - 1)
        }
        return this
    }

fun Instant.toRelative(): String = DateUtils.getRelativeTimeSpanString(this.toEpochMilliseconds()).toString()