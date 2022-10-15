package com.juvcarl.shoplist.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.juvcarl.shoplist.data.model.BUY_STATUS
import com.juvcarl.shoplist.data.model.Item
import kotlinx.datetime.Instant

@Entity(tableName = "items")
data class ItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val name: String,
    val date: Instant,
    val buyAgain: Boolean,
    val type: String,
    val buyQty: Int?,
    val buyStatus: String?
)

fun ItemEntity.asModel() = Item(
    id = id,
    name = name,
    date = date,
    buyAgain = buyAgain,
    type = type,
    buyQty = buyQty,
    buyStatus = buyStatus
)