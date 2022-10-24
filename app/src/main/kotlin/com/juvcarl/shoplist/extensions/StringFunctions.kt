package com.juvcarl.shoplist.extensions

fun String.separateItems(): List<String>{
    return this.split(",","[","]", ignoreCase = true)
        .filter {
            it.isNotEmpty()}
        .map {
            it.filter {
                it.isLetter() || it.isWhitespace()
            }.trim()
        }.filter {
            it.length > 2
        }.distinct()
}

