package com.vschouppe.artapp.supabase.database

import android.util.Log
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.vschouppe.artapp.R
import com.vschouppe.artapp.supabase
import io.github.jan.supabase.exceptions.NotFoundRestException
import io.github.jan.supabase.exceptions.RestException
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.launch

@Composable
fun dbButton(){
    val coroutineScope = rememberCoroutineScope()
    Button(
        onClick = {
                Log.d("DB_WRITE","pre writing to DB ")
                coroutineScope.launch {
                    try {
                        supabase.from("login").insert(mapOf("content" to "LOGIN"))
                        Log.d("DB_WRITE","Written to DB ")
                    }catch(e: RestException){
                        Log.e("DB_WRITE","something went wrong + ${e}")
                    }catch(e: NotFoundRestException){
                        Log.e("DB_WRITE","something went wrong + ${e}")                    }
                }
        }
    ){
        Text("Insert row in DB")
    }
}