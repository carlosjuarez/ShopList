package com.juvcarl.shoplist.database

import android.util.Log
import androidx.room.Room
import androidx.room.migration.AutoMigrationSpec
import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class ShopListDatabaseMigrationTest {
    private val TEST_DB = "migration-test"

    @get:Rule
    val helper: MigrationTestHelper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        ShopListDatabase::class.java, listOf(),
        FrameworkSQLiteOpenHelperFactory()
    )

    @Test
    @Throws(IOException::class)
    fun testAllMigrations(){
        helper.createDatabase(TEST_DB,1).apply {
            close()
        }
        Room.databaseBuilder(
            InstrumentationRegistry.getInstrumentation().targetContext,
            ShopListDatabase::class.java,
            TEST_DB
        ).addMigrations(MIGRATION_3_4).build().apply {
            openHelper.writableDatabase.close()
        }
    }

    @Test
    @Throws(IOException::class)
    fun migrate1To2(){
        var db = helper.createDatabase(TEST_DB,1).apply {
            execSQL(
                "INSERT INTO items (id, name, date, buyAgain, type) values (1,'test',12312,1,'testtype')"
            )
            close()
        }

        db = helper.runMigrationsAndValidate(TEST_DB,2,true)

        val cursor = db.query("SELECT * FROM items")

        cursor.use {
            assertEquals(it.columnCount,7)
            assertEquals(it.count, 1)
            it.moveToFirst()
            assertEquals("test",it.getString(1))
        }

        db.close()

    }

    @Test
    @Throws(IOException::class)
    fun migrate2To3(){
        var db = helper.createDatabase(TEST_DB,2).apply {
            execSQL(
                "INSERT INTO items (id, name, date, buyAgain, type, buyQty, buyStatus) values (1,'test',12312,1,'testtype',15,'BOUGHT')"
            )
            close()
        }

        db = helper.runMigrationsAndValidate(TEST_DB,3,true)

        val cursor = db.query("SELECT * FROM items")

        cursor.use {
            assertEquals(it.columnCount,7)
            assertEquals(it.count, 1)
            it.moveToFirst()
            assertEquals(15.0,it.getDouble(it.getColumnIndexOrThrow("buyQty")),0.1)
        }

        db.close()

    }

    @Test
    @Throws(IOException::class)
    fun migrate3To4(){
        var db = helper.createDatabase(TEST_DB,3).apply {
            execSQL(
                "INSERT INTO items (id, name, date, buyAgain, type, buyQty, buyStatus) values (1,'test',12312,1,'testtype',15,'BOUGHT')"
            )
            close()
        }

        db = helper.runMigrationsAndValidate(TEST_DB,4,true, MIGRATION_3_4)

        val cursor = db.query("SELECT * FROM items")

        cursor.use {
            assertEquals(it.columnCount,8)
            assertEquals(it.count, 1)
            it.moveToFirst()
            println(it.getString(it.getColumnIndexOrThrow("identifier")))
            assert(!it.getString(it.getColumnIndexOrThrow("identifier")).isNullOrEmpty())
            cursor.close()
        }

        db.close()

    }

}