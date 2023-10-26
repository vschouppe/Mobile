package com.vschouppe

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.assertIsSelected
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.vschouppe.ui.theme.TipTimeTheme
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import org.junit.Test
import org.junit.runner.RunWith
import java.text.NumberFormat
import org.junit.Assert.*
import org.junit.Rule
import java.lang.Math.ceil

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class TipCalculatorUITests {
    @get:Rule
    val composeTestRule = createComposeRule()
    @Test
    fun calculate_20_Point_05_percent_tip() {
        composeTestRule.setContent {
            TipTimeTheme {
                Surface (modifier = Modifier.fillMaxSize()){
                    TipTimeLayout()
                }
            }
        }
        composeTestRule.onNodeWithText("Bill amount")
            .performTextInput("10")
        composeTestRule.onNodeWithText("Tip").performTextInput("20.5")
        val switch = composeTestRule.onNodeWithTag("Switch")
        switch.assertIsDisplayed()
        switch.assertIsOff()
        val expectedTip = NumberFormat.getCurrencyInstance().format(2.05)
        composeTestRule.onNodeWithText("Tip Amount: $expectedTip").assertExists(
            "No node with this text was found."
        )
        switch.performClick()
        switch.assertIsOn()
        composeTestRule.onNodeWithText("Tip Amount: ${NumberFormat.getCurrencyInstance().format(3)}").assertExists(
            "No node with this text was found."
        )
        switch.performClick()
        switch.assertIsOff()
        composeTestRule.onNodeWithText("Tip Amount: ${NumberFormat.getCurrencyInstance().format(2.05)}").assertExists(
            "No node with this text was found."
        )
    }
}