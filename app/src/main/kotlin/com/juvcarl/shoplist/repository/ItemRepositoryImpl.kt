package com.juvcarl.shoplist.repository

import com.juvcarl.shoplist.data.model.Item
import com.juvcarl.shoplist.data.model.asEntity
import com.juvcarl.shoplist.database.dao.ItemDao
import com.juvcarl.shoplist.database.model.ItemEntity
import com.juvcarl.shoplist.database.model.asModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ItemRepositoryImpl @Inject constructor(
    private val itemsDao: ItemDao
) : ItemRepository {
    override fun getItemsStream(): Flow<List<Item>> = itemsDao.getItems().map { it.map(ItemEntity::asModel) }

    override fun getItemsByNameStream(searchQuery: String): Flow<List<Item>> = itemsDao.searchItemByName(searchQuery).map { it.map(ItemEntity::asModel) }

    override fun getItemsToBuyStream(): Flow<List<Item>> = itemsDao.getItemsToBuy().map { it.map(ItemEntity::asModel)}

    override fun getExistingItems(): Flow<List<Item>> = itemsDao.getExistingItems().map { it.map(ItemEntity::asModel)}

    override suspend fun insertItem(item: Item) {
        itemsDao.insertItem(item.asEntity())
    }

    override suspend fun updateitem(item: Item) {
        itemsDao.updateitem(item.asEntity())
    }

    override suspend fun deleteItem(id: Long) {
        itemsDao.deleteItem(id)
    }

}