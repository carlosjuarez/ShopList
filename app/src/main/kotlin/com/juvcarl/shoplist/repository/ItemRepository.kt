package com.juvcarl.shoplist.repository

import com.juvcarl.shoplist.data.model.Item
import kotlinx.coroutines.flow.Flow

interface ItemRepository {
    fun getItemsStream(): Flow<List<Item>>
    fun getItemsToBuyStream(): Flow<List<Item>>
    fun getExistingItems(): Flow<List<Item>>
    suspend fun deleteItem(id: Long)
    suspend fun insertItem(item: Item)
    suspend fun updateitem(item: Item)
    fun getItemsByNameStream(searchQuery: String): Flow<List<Item>>
    fun getItemByIdStream(id: Long): Flow<Item>
    suspend fun finishShopping(keepItems: Boolean)
    suspend fun ImportItems(items: List<Item>): List<Long>
    suspend fun createShareString(): String
    suspend fun createExportString(): String
    suspend fun clearItems()
}