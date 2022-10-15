package com.juvcarl.shoplist.data.model

import kotlinx.datetime.Instant

data class Item(val id: Long = 0L,
                val name: String,
                val date: Instant,
                val buyAgain: Boolean,
                val type: String,
                val buyQty: Int? = null,
                val buyStatus: String? = if(buyAgain) BUY_STATUS.BUY.name else BUY_STATUS.BOUGHT.name)

enum class BUY_STATUS(literal: String){
    BUY("buy"),
    BOUGHT("bought"),
    WAIT_TO_BUY("wait")
}