package com.juvcarl.shoplist.extensions

import java.math.RoundingMode
import java.text.DecimalFormat
import kotlin.math.roundToInt

fun Double.StringWithoutZeroDecimal(): String {
    return if(this % 1 == 0.0){
        this.roundToInt().toString()
    }else{
        val df = DecimalFormat("#.##").apply {
            roundingMode = RoundingMode.HALF_DOWN
        }
        df.format(this)
    }
}
