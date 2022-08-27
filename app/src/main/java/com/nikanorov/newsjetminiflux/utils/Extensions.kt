package com.nikanorov.newsjetminiflux.utils

val String.removeTrailingSlash:String
    get(){
        if (this.endsWith("/")) {
            return this.substring(0, this.length - 1)
        }
        return this
    }

