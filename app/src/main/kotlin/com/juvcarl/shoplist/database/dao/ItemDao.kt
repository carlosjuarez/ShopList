package com.juvcarl.shoplist.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.juvcarl.shoplist.database.model.ItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ItemDao {
    @Query(
        value = """SELECT * FROM items ORDER BY date DESC"""
    )
    fun getItems(): Flow<List<ItemEntity>>


    @Query(
        value = """SELECT * FROM items where buyAgain = 1 ORDER BY date DESC"""
    )
    fun getItemsToBuy(): Flow<List<ItemEntity>>

    @Query(
        value = """SELECT * FROM items where buyAgain = 0 ORDER BY date DESC"""
    )
    fun getExistingItems(): Flow<List<ItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: ItemEntity)

    @Update
    suspend fun updateitem(item: ItemEntity)

    @Query(
        value = """DELETE from items WHERE id = :id"""
    )
    suspend fun deleteItem(id: Long)

}