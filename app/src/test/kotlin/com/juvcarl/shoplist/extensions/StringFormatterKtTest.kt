package com.juvcarl.shoplist.extensions

import org.junit.Assert.*
import org.junit.Test

class StringFormatterKtTest{

    @Test
    fun TestDoubleFormatter(){
        var double : Double = 10.00
        assertEquals("10",double.StringWithoutZeroDecimal())

        double = 10.4216
        assertEquals("10.42",double.StringWithoutZeroDecimal())

        double = 8.586
        assertEquals("8.59",double.StringWithoutZeroDecimal())

        double = 10.006
        assertEquals("10.01",double.StringWithoutZeroDecimal())

        double = 1.1
        assertEquals("1.1",double.StringWithoutZeroDecimal())

    }

}