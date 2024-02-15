package com.vschouppe.artapp.signin

import android.credentials.GetCredentialException
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.vschouppe.artapp.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import retrofit2.HttpException
import java.security.MessageDigest
import java.util.UUID

@RequiresApi(34)
@Composable
fun SignInScreen(
    state: SignInState,
    onSignInClick: () -> Unit,
    locationClick: () -> Unit
) {
    val context = LocalContext.current
    LaunchedEffect(key1 = state.signInError) {
        state.signInError?.let { error ->
            Toast.makeText(
                context,
                error,
                Toast.LENGTH_LONG
            ).show()
        }
    }
    Column (modifier = Modifier
        .fillMaxHeight()) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.4f)
        )
        {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Log.d("SignIn Box","city ${state.address?.city}")
                if (state.address?.city != null){
                    Text(
                        text = stringResource(id = R.string.signin_welcome_initial) + " ${state.address?.city}",
                        style = MaterialTheme.typography.displaySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

            }
        }
//        FIREBASE LOGIN
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.3f)
        )
        {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Button(
                    shape = MaterialTheme.shapes.extraLarge,
                    onClick = onSignInClick) {
                    Text(text = "Sign in")
                }
            }

        }
//        GOOGLE CRED LOGIN
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.3f)
        )
        {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                GoogleSignInButton(state)
            }

        }
//        LOCATION BUTTON
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.3f)
        )
        {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Button(onClick = locationClick) {
                    Text(text = "Where are you?")
                }
            }
        }
    }
}


@RequiresApi(34)
@Composable
fun GoogleSignInButton(
    state: SignInState){
    val viewModel: SignInViewModel = viewModel()
    Log.d("GoogleSignInButton","start")
    val context = LocalContext.current
    val coroutine = rememberCoroutineScope()

    val random = UUID.randomUUID().toString()
    val bytes = random.toByteArray()
    val md = MessageDigest.getInstance("SHA-256")
    val digest = md.digest(bytes)
    val hashedNonce = digest.fold("") {str,it -> str + "%02x".format(it)}
    Log.d("GoogleSignInButton","got hashedNonce ${hashedNonce}")

    var signIn: () -> Unit = {
        val credentialManager = CredentialManager.create(context)
        Log.d("GoogleSignInButton","credentialManager created")


        try {
            val googleIdOption : GetGoogleIdOption =  GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(context.getString(R.string.google_web_client_id))
                .setNonce(hashedNonce)
                .build()
            Log.d("GoogleSignInButton","googleIdOption created")
            val request : GetCredentialRequest = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()
            Log.d("GoogleSignInButton","request created")
            coroutine.launch {
                Log.d("GoogleSignInButton","launched")
                val result = credentialManager.getCredential(
                    request = request,
                    context = context
                )
                Log.d("GoogleSignInButton","result ${result}")
                val credentials = result.credential
                val googleIdTokenCredential =
                    GoogleIdTokenCredential.createFrom(credentials.data)
                Log.d("GoogleSignInButton","googleIdTokenCredential created")
                val token = googleIdTokenCredential.idToken
                Toast.makeText(context, "Successfully logged in ", Toast.LENGTH_LONG).show()
                googleIdTokenCredential.displayName
                var user : UserData
                user =
                    UserData(
                        userId = googleIdTokenCredential.id,
                        username = googleIdTokenCredential.displayName,
                        profilePictureUrl = googleIdTokenCredential.profilePictureUri.toString()
                    )
                Log.d("GoogleSignInButton"," user details : ${user}")
                delay(1000)
                Toast.makeText(context, "welcome :${user.username} ", Toast.LENGTH_LONG).show()
                delay(2000)
                state.isSignInSuccessful
                viewModel.onSignInResult(
                    SignInResult(
                    data = user?.run { user },
                    errorMessage = null
                ))
            }
        }catch (e: HttpException){
            Log.d("GoogleSignInButton",e.message())
        }catch (e: GetCredentialException){
            Log.d("GoogleSignInButton","message: " +e.message)
        }catch (e: GetCredentialCancellationException){
            Log.d("GoogleSignInButton","message: " +e.message)
        }


    }

    Button( onClick = signIn) {
        Text(text =  "Google Sign In through Cred manager")
    }
}