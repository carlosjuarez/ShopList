package com.juvcarl.shoplist.extensions

import org.junit.Assert.*
import org.junit.Test

class StringFunctionsKtTest{

    @Test
    fun TestSeparateItemsInString(){

        val testTest = "asdf,adf adf[ ] ad a[f as12df() -ad"

        val result = testTest.separateItems()

        println(result.joinToString("|"))

        assertEquals(4,result.size)

    }
}