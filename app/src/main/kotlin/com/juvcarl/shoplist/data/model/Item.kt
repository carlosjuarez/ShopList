package com.juvcarl.shoplist.data.model

import com.juvcarl.shoplist.database.model.ItemEntity
import kotlinx.datetime.Instant

data class Item(val id: Long = 0L,
                val name: String,
                val date: Instant,
                val buyAgain: Boolean,
                val type: String)

fun Item.asEntity(): ItemEntity{
    return ItemEntity(
        name = this.name,
        date = this.date,
        buyAgain = this.buyAgain,
        type = this.type)
}