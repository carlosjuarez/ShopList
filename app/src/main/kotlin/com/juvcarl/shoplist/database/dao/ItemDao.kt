package com.juvcarl.shoplist.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.juvcarl.shoplist.data.model.BUYSTATUS
import com.juvcarl.shoplist.database.model.ItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ItemDao {
    @Query(
        value = """SELECT * FROM items ORDER BY name COLLATE NOCASE ASC"""
    )
    fun getItems(): Flow<List<ItemEntity>>

    @Query(
        value = """SELECT * FROM items ORDER BY name COLLATE NOCASE ASC"""
    )
    fun listItems(): List<ItemEntity>

    @Query(
        value = """SELECT * FROM items WHERE name LIKE '%' || :searchQuery || '%'"""
    )
    fun searchItemByName(searchQuery: String): Flow<List<ItemEntity>>

    @Query(
        value = """SELECT * FROM items where buyAgain = 1 ORDER BY name COLLATE NOCASE ASC"""
    )
    fun getItemsToBuy(): Flow<List<ItemEntity>>

    @Query(
        value = """SELECT * FROM items where buyAgain = 1 ORDER BY name COLLATE NOCASE ASC"""
    )
    fun listItemsToBuy(): List<ItemEntity>

    @Query(
        value = """SELECT * FROM items where buyAgain = 0 ORDER BY name COLLATE NOCASE ASC"""
    )
    fun getExistingItems(): Flow<List<ItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: ItemEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItems(items: List<ItemEntity>): List<Long>

    @Update
    suspend fun updateitem(item: ItemEntity)

    @Query(
        value = """DELETE from items WHERE id = :id"""
    )
    suspend fun deleteItem(id: Long)

    @Query(
        value = """SELECT * FROM items WHERE id = :id"""
    )
    fun searchItemById(id: Long): Flow<ItemEntity>

    @Query(
        value = """ UPDATE items SET buyAgain = 0, buyQty = 0, buyStatus = 'BUY' WHERE buyStatus in ('BOUGHT') """
    )
    suspend fun resetBoughtItemsList()

    @Query(
        value = """ UPDATE items SET buyAgain = 0, buyQty = 0, buyStatus = 'BUY'"""
    )
    suspend fun resetList()

}