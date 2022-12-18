package com.juvcarl.shoplist.database

import android.content.ContentValues
import android.util.Log
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.OnConflictStrategy
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.juvcarl.shoplist.database.dao.ItemDao
import com.juvcarl.shoplist.database.model.ItemEntity
import com.juvcarl.shoplist.database.util.InstantConverter
import com.juvcarl.shoplist.database.util.UUIDConverter
import java.util.*

@Database(
    entities = [
        ItemEntity::class
    ],
    version = 4,
    exportSchema = true,
    autoMigrations = [
        AutoMigration (from = 1, to = 2),
        AutoMigration (from = 2, to = 3),
    ]
)
@TypeConverters(
    InstantConverter::class,
    UUIDConverter::class

)
abstract class ShopListDatabase : RoomDatabase() {
    abstract fun ItemsDao() : ItemDao
}

val MIGRATION_3_4 = object : Migration(3,4){
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("""ALTER TABLE items ADD COLUMN `identifier` TEXT""")
        val cursor = database.query("""SELECT id FROM items""")
        if(cursor != null && cursor.count > 0){
            while(cursor.moveToNext()){
                val id = cursor.getLong(0)
                val uuid = UUID.randomUUID().toString()

                val toInsert = ContentValues()
                toInsert.put("identifier",uuid)

                val total = database.update("items",OnConflictStrategy.REPLACE, toInsert,"id=?", arrayOf(id))
                Log.d("sql","Total $total")
            }
        }
        cursor.close()
    }
}