package com.juvcarl.shoplist.data.model

import kotlinx.datetime.Instant

data class Item(val id: Long = 0L,
                val name: String,
                val date: Instant,
                val buyAgain: Boolean,
                val type: String)