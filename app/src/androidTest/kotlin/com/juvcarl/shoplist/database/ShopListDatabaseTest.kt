package com.juvcarl.shoplist.database

import android.content.Context
import android.util.Log
import androidx.room.*
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.juvcarl.shoplist.data.model.BUYSTATUS
import com.juvcarl.shoplist.data.model.Item
import com.juvcarl.shoplist.data.model.asEntity
import com.juvcarl.shoplist.database.dao.ItemDao
import com.juvcarl.shoplist.database.model.ItemEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import kotlin.random.Random

@RunWith(AndroidJUnit4::class)
class ShopListDatabaseTest{
    private lateinit var itemDao: ItemDao
    private lateinit var db: ShopListDatabase

    @Before
    fun createDB(){
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context,ShopListDatabase::class.java).build()
        itemDao = db.ItemsDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    private fun createListOfItems(buyAgain : Boolean? = null): List<Item>{
        val result = mutableListOf<Item>()
        for(i in 1..10){
            val item = Item(name  = "name${i}",
                date = Clock.System.now(),
                buyAgain = if(buyAgain == null) Random.nextBoolean() else buyAgain,
                type = "test")
            result.add(item)
        }
        return result
    }

    @Test
    @Throws(Exception::class)
    fun insertItem() = runTest {
        val item = Item(name = "Test", date = Clock.System.now(), buyAgain = true, type = "test")
        itemDao.insertItem(item.asEntity())
        val listItems = itemDao.getItems().first()
        val itemResult = listItems.first()
        assertEquals(item.asEntity().copy(id = 1), itemResult)
    }



    @Test
    fun searchItemByName() = runTest{
        val items = createListOfItems()
        for(i in items){
            itemDao.insertItem(i.asEntity())
        }
        val dbResult = itemDao.searchItemByName("2").first()

        assertEquals("name2",dbResult.first().name)
    }

    @Test
    fun getItemsToBuy() = runTest{
        val items = createListOfItems(true)
        for(i in items){
            itemDao.insertItem(i.asEntity())
        }
        val dbResult = itemDao.getItemsToBuy().first()

        assertEquals(10,dbResult.size)
        assertTrue(dbResult.all { it.buyAgain })
    }


    @Test
    fun getItemsOrderByName() = runTest {
        val items = listOf(
            Item(name = "zuccini", date = Clock.System.now(), buyAgain = true,type = "test"),
            Item(name = "spinach", date = Clock.System.now(), buyAgain = true,type = "test"),
            Item(name = "beets", date = Clock.System.now(), buyAgain = true,type = "test"),
        )
        for(i in items){
            itemDao.insertItem(i.asEntity())
        }
        val dbResult = itemDao.getItems().first()

        assertEquals(3,dbResult.size)
        val itemsSorted = items.sortedBy { it.name }
        itemsSorted.forEachIndexed { index, item ->
            assertEquals(item.name,dbResult.get(index).name)
        }
    }


    @Test
    fun updateItem() = runTest {
        val item = Item(name = "Test", date = Clock.System.now(), buyAgain = true, type = "test")
        itemDao.insertItem(item.asEntity())
        var listItems = itemDao.getItems().first()
        val updateItem = listItems.first().copy(name = "Zuccini", buyAgain = true)
        itemDao.updateitem(updateItem)
        listItems = itemDao.getItems().first()
        assertEquals("Zuccini", listItems.first().name)
    }

    @Test
    fun deleteItem() = runTest {
        val item = Item(name = "Test", date = Clock.System.now(), buyAgain = true, type = "test")
        itemDao.insertItem(item.asEntity())
        itemDao.deleteItem(1)
        val listItems = itemDao.getItems().first()
        assertTrue(listItems.isEmpty())
    }

    @Test
    fun resetBoughtListItems() = runTest {
        val items = listOf(
            Item(name = "zuccini", date = Clock.System.now(), buyAgain = true, type = "test", buyQty = 10.5, buyStatus = BUYSTATUS.BUY.name),
            Item(name = "spinach", date = Clock.System.now(), buyAgain = true, type = "test", buyQty = 1.2, buyStatus = BUYSTATUS.BOUGHT.name),
            Item(name = "beets", date = Clock.System.now(), buyAgain = true, type = "test", buyQty = 1.0, buyStatus = BUYSTATUS.WAIT_TO_BUY.name),
        )
        for(i in items){
            itemDao.insertItem(i.asEntity())
        }
        itemDao.resetBoughtItemsList()

        val dbResult = itemDao.getItems().first()

        assertEquals(3,dbResult.size)
        assertEquals(1, dbResult.filter { it.buyAgain == false }.size)
        assertEquals(2, dbResult.filter { it.buyAgain != false }.size)

    }

    @Test
    fun resetAllListItems() = runTest {
        val items = listOf(
            Item(name = "zuccini", date = Clock.System.now(), buyAgain = true, type = "test", buyQty = 10.5, buyStatus = BUYSTATUS.BUY.name),
            Item(name = "spinach", date = Clock.System.now(), buyAgain = true, type = "test", buyQty = 1.2, buyStatus = BUYSTATUS.BOUGHT.name),
            Item(name = "beets", date = Clock.System.now(), buyAgain = true, type = "test", buyQty = 1.0, buyStatus = BUYSTATUS.WAIT_TO_BUY.name),
        )
        for(i in items){
            itemDao.insertItem(i.asEntity())
        }
        itemDao.resetList()

        val dbResult = itemDao.getItems().first()

        assertEquals(3,dbResult.size)
        assertEquals(3, dbResult.filter { it.buyAgain == false }.size)
        assertEquals(0, dbResult.filter { it.buyAgain != false }.size)

    }

}