package com.juvcarl.shoplist.repository

import com.juvcarl.shoplist.data.model.BUYSTATUS
import com.juvcarl.shoplist.data.model.Item
import com.juvcarl.shoplist.data.model.asEntity
import com.juvcarl.shoplist.database.dao.ItemDao
import com.juvcarl.shoplist.database.model.ItemEntity
import com.juvcarl.shoplist.database.model.asExportString
import com.juvcarl.shoplist.database.model.asModel
import com.juvcarl.shoplist.database.model.asShareString
import com.juvcarl.shoplist.di.DefaultDispatcher
import com.juvcarl.shoplist.di.IoDispatcher
import com.juvcarl.shoplist.extensions.StringWithoutZeroDecimal
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ItemRepositoryImpl @Inject constructor(
    private val itemsDao: ItemDao,
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
) : ItemRepository {
    override fun getItemsStream(): Flow<List<Item>> = itemsDao.getItems().map { it.map(ItemEntity::asModel) }

    override fun getItemsByNameStream(searchQuery: String): Flow<List<Item>> = itemsDao.searchItemByName(searchQuery).map { it.map(ItemEntity::asModel) }

    override fun getItemByIdStream(id: Long) = itemsDao.searchItemById(id).map { it.asModel() }

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

    override suspend fun finishShopping(keepItems: Boolean){
        if(keepItems){
            itemsDao.resetBoughtItemsList()
        }else{
            itemsDao.resetList()
        }
    }

    override suspend fun ImportItems(items: List<Item>): List<Long> {
        return itemsDao.insertItems(items.map { it.asEntity() })
    }

    override suspend fun createShareString(): String {
        return withContext(dispatcher){
            val items = itemsDao.listItemsToBuy()
            //TODO add categories
            "Shop List\n" + items.map { it.asShareString() }.joinToString("\n")
        }
    }

    override suspend fun createExportString(): String {
        return withContext(dispatcher){
            val items = itemsDao.listItems()
            items.map { it.asExportString() }.joinToString(",")
        }
    }

    override suspend fun clearItems() {
        return itemsDao.clearAll()
    }

}