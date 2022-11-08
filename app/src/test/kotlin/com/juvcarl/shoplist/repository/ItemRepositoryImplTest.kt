package com.juvcarl.shoplist.repository

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.juvcarl.shoplist.data.model.BUYSTATUS
import com.juvcarl.shoplist.database.dao.ItemDao
import com.juvcarl.shoplist.database.model.ItemEntity
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class ItemRepositoryImplTest{

    private lateinit var itemRepository: ItemRepository
    private var itemDao: ItemDao = mockk()

    @Test
    fun createShareStringTest() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)

        itemRepository = ItemRepositoryImpl(itemDao,dispatcher)

        val listItems = listOf(
            ItemEntity(0L,"test1", Clock.System.now(),true,"basic",null, BUYSTATUS.BUY.name),
            ItemEntity(0L,"test2", Clock.System.now(),true,"basic",2.6, BUYSTATUS.BOUGHT.name),
            ItemEntity(0L,"test3", Clock.System.now(),true,"basic",2.7, BUYSTATUS.WAIT_TO_BUY.name)
        )

        coEvery {
            itemDao.listItemsToBuy()
        } returns listItems

        val result = itemRepository.createShareString()

        val expected = "Shop List\ntest1 [ ]\n2.6 test2 [*]\n2.7 test3 [/]"

        assertEquals(expected,result)


    }

}