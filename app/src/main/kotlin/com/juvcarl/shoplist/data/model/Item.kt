package com.juvcarl.shoplist.data.model

import com.juvcarl.shoplist.database.model.ItemEntity
import kotlinx.datetime.Instant

data class Item(val id: Long = 0L,
                val name: String,
                val date: Instant,
                val buyAgain: Boolean,
                val type: String? = null,
                val buyQty: Double? = null,
                val buyStatus: String? = if(buyAgain) BUYSTATUS.BUY.name else BUYSTATUS.BOUGHT.name)

enum class BUYSTATUS{
    BUY,
    BOUGHT,
    WAIT_TO_BUY
}

fun Item.asEntity(): ItemEntity{
    return ItemEntity(
        id = this.id,
        name = this.name,
        date = this.date,
        buyAgain = this.buyAgain,
        type = this.type,
        buyQty = this.buyQty,
        buyStatus = this.buyStatus)
}