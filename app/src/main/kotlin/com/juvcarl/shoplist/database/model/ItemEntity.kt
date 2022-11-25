package com.juvcarl.shoplist.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.juvcarl.shoplist.data.model.BUYSTATUS
import com.juvcarl.shoplist.data.model.Item
import com.juvcarl.shoplist.extensions.StringWithoutZeroDecimal
import kotlinx.datetime.Instant
import java.util.UUID

@Entity(tableName = "items")
data class ItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val name: String,
    val date: Instant,
    val buyAgain: Boolean,
    val type: String?,
    val buyQty: Double? = null,
    val buyStatus: String? = null,
    val identifier: UUID? = null,
) : java.io.Serializable

fun ItemEntity.asModel() = Item(
    id = id,
    name = name,
    date = date,
    buyAgain = buyAgain,
    type = type,
    buyQty = buyQty,
    buyStatus = buyStatus,
    identifier = identifier
)

fun ItemEntity.asShareString() : String {
    val stringBuilder = StringBuilder()

    if(this.buyQty != null && this.buyQty > 0){
        stringBuilder.append(
            "${this.buyQty.StringWithoutZeroDecimal()} "
        )
    }
    stringBuilder.append("${this.name} ")
    stringBuilder.append(
        when(this.buyStatus){
            BUYSTATUS.BOUGHT.name -> "[*]"
            BUYSTATUS.WAIT_TO_BUY.name -> "[/]"
            else -> "[ ]"
        }
    )
    return stringBuilder.toString()
}

fun ItemEntity.asExportString() : String{
    return this.S
}