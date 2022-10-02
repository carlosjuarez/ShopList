package com.juvcarl.shoplist.repository

import com.juvcarl.shoplist.data.model.Item
import com.juvcarl.shoplist.database.model.ItemEntity
import kotlinx.coroutines.flow.Flow

interface ItemRepository {
    fun getItemsStream(): Flow<List<Item>>
    fun getItemsToBuyStream(): Flow<List<Item>>
    fun getExistingItems(): Flow<List<Item>>
    suspend fun deleteItem(id: Long)
    suspend fun insertItem(item: Item)
    suspend fun updateitem(item: Item)
}