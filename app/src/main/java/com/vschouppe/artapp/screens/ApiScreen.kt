package com.vschouppe.artapp.screens

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.vschouppe.R
import com.vschouppe.artapp.theme.MobileAppsPlaygroundTheme

@Composable
fun ApiScreen(
    artUiState: String, modifier: Modifier = Modifier
) {
    ResultScreen(artUiState, modifier)
}

/**
 * ResultScreen displaying number of photos retrieved.
 */
@Composable
fun ResultScreen(albums: String, modifier: Modifier = Modifier) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
    ) {
        Log.d("API result", "API result screen")
        Text(text = albums)
    }
}

@Preview(showBackground = true)
@Composable
fun ResultScreenPreview() {
    MobileAppsPlaygroundTheme {
        ResultScreen(stringResource(id = R.string.api_result_start))
    }
}
