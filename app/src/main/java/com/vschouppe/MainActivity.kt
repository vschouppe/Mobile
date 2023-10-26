package com.vschouppe

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.tiptime.ui.theme.TipTimeTheme
import java.lang.Math.ceil
import java.text.NumberFormat


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            TipTimeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    TipTimeLayout()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TipTimeLayout() {
    var inputAmount  by remember {mutableStateOf("")}
    var inputRoundUp by remember {mutableStateOf(false)}
    var inputPercentage  by remember {mutableStateOf("")}
    var tipAmount = calculateTip(inputAmount.toDoubleOrNull() ?: 0.0,inputPercentage.toDoubleOrNull() ?: 0.0,inputRoundUp)

    Column(
        modifier = Modifier
            .statusBarsPadding()
            .padding(horizontal = 40.dp)
            .safeDrawingPadding()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(R.string.calculate_tip),
            modifier = Modifier
                .padding(bottom = 16.dp, top = 40.dp)
                .align(alignment = Alignment.Start)
        )
        EditNumberField(
            value = inputAmount,
            onValueChange = {
                inputAmount = it
            },
            modifier = Modifier
                .padding(bottom = 15.dp)
                .fillMaxWidth())
        Spacer(modifier = Modifier.height(25.dp))
        TipPercentage(
            value = inputPercentage,
            onValueChange = {
                inputPercentage = it
            },
            modifier = Modifier
                .padding(bottom = 15.dp)
                .fillMaxWidth())
        Spacer(modifier = Modifier.height(10.dp))
        RoundUp(
            inputRoundUp,
            {inputRoundUp = it},
            modifier = Modifier
                .fillMaxWidth()
                .size(48.dp)
                .padding(start = 20.dp, end = 10.dp))
        Spacer(modifier = Modifier.height(10.dp))
        TipAmount(modifier = Modifier,tipAmount)
    }
}

@Composable
fun RoundUp(
    roundUp: Boolean = false,
    onCheckedChange : (Boolean) -> Unit,
    modifier : Modifier){
    Row (modifier,
        verticalAlignment = Alignment.CenterVertically){
        Text(
            text = stringResource(R.string.input_label_round_up),
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Start
        )
        Switch(
            checked = roundUp,
            onCheckedChange = onCheckedChange,
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentWidth(Alignment.End)
                )
    }
}


@Preview
@Composable
fun RoundUpPreview(){
    RoundUp(false, {true},modifier = Modifier
        .fillMaxWidth()
        .size(48.dp)
        .padding(start = 20.dp, end = 10.dp))
}

@Composable
fun TipAmount(modifier : Modifier, tipAmount : String){
    Text(
        text = stringResource(R.string.tip_amount, tipAmount),
        style = MaterialTheme.typography.displaySmall
    )
}
@Preview
@Composable
fun TipAmountPreview(){
    TipAmount(modifier = Modifier, "12.15")
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditNumberField(value: String,
                    onValueChange: (String) -> Unit,
                    modifier : Modifier){
    TextField(
        leadingIcon = { Icon(painter = painterResource(id = R.drawable.baseline_money_24), contentDescription = null )},
        value = value,
        onValueChange = onValueChange,
        label = { Text(stringResource(id = R.string.input_label_bill_amount))},
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
        modifier = Modifier
    )
}
@Preview
@Composable
fun EditNumberFieldPreview(){
    var inputAmount  by remember {mutableStateOf("")}
    EditNumberField("12",
        {
        "doesn't matter here"
        },
        modifier = Modifier)
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TipPercentage(value: String,
                    onValueChange: (String) -> Unit,
                    modifier : Modifier){
    TextField(
        leadingIcon = { Icon(painter = painterResource(id = R.drawable.baseline_percent_24), contentDescription = null )},
        value = value,
        onValueChange = onValueChange,
        label = { Text(stringResource(id = R.string.intput_label_tip_percentage))},
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Done),
        modifier = Modifier
    )
}
@Preview
@Composable
fun TipPercentagePreview(){
    var inputAmount  by remember {mutableStateOf("")}
    TipPercentage("12",
        {
        "doesn't matter here"
        },
        modifier = Modifier)
}


/**
 * Calculates the tip based on the user input and format the tip amount
 * according to the local currency.
 * Example would be "$10.00".
 */
private fun calculateTip(amount: Double, tipPercent: Double = 15.0, roundUp: Boolean): String {
    val tip = if (roundUp) ceil(tipPercent / 100 * amount) else tipPercent / 100 * amount
    return NumberFormat.getCurrencyInstance().format(tip)
}

@Preview(showBackground = true)
@Composable
fun TipTimeLayoutPreview() {
    TipTimeTheme {
        TipTimeLayout()
    }
}
