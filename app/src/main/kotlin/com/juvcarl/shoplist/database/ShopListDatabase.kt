package com.juvcarl.shoplist.database

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.juvcarl.shoplist.database.dao.ItemDao
import com.juvcarl.shoplist.database.model.ItemEntity
import com.juvcarl.shoplist.database.util.InstantConverter

@Database(
    entities = [
        ItemEntity::class
    ],
    version = 2,
    exportSchema = true,
    autoMigrations = [
        AutoMigration (from = 1, to = 2)
    ]
)
@TypeConverters(
    InstantConverter::class
)
abstract class ShopListDatabase : RoomDatabase() {
    abstract fun ItemsDao() : ItemDao
}