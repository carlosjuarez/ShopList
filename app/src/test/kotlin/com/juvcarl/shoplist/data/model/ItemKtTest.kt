package com.juvcarl.shoplist.data.model

import com.juvcarl.shoplist.database.model.ItemEntity
import kotlinx.datetime.Clock
import org.junit.Assert.*

import org.junit.Test

class ItemTest {

    @Test
    fun ItemAsEntity() {

        val instant = Clock.System.now()


        val item = Item(id = 2, name = "Test", date = instant, buyAgain = true, type = "test", buyQty = 0.0, buyStatus = BUYSTATUS.BUY.name )
        val entity = ItemEntity(id = 2, name = "Test", date = instant, buyAgain = true, type = "test", buyQty = 0.0, buyStatus = BUYSTATUS.BUY.name )

        assertEquals(entity,item.asEntity())

        val item1 = Item(name = "Test", date = instant, buyAgain = true, type = "test", buyQty = 0.0, buyStatus = BUYSTATUS.BUY.name )
        val entity1 = ItemEntity(name = "Test", date = instant, buyAgain = true, type = "test", buyQty = 0.0, buyStatus = BUYSTATUS.BUY.name )

        assertEquals(entity1,item1.asEntity())
    }
}