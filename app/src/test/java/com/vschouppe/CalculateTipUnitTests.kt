package com.vschouppe

import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class CalculateTipUnitTests {
    @Test
    fun calculateTip_isCorrect(){
        assertEquals(calculateTip(100.0,10.0,false),"$10.00")
    }
    @Test
    fun calculateTip_20PercentNoRoundup() {
        assertEquals(calculateTip(100.0,20.0,false),"$20.00")
    }
    @Test
    fun calculateTip_21Point5PercentRoundup() {
        assertEquals(calculateTip(100.0,21.5,true),"$22.00")
    }
}