package com.vschouppe.artapp.signin

import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat.startActivityForResult
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.BeginSignInRequest.GoogleIdTokenRequestOptions
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.Scope
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.GetTokenResult
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.vschouppe.artapp.R
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.tasks.await


class GoogleAuthUiClient(
    private val context: Context,
    private val oneTapClient: SignInClient
) {
    private val auth = Firebase.auth


    suspend fun signIn(): IntentSender? {
        Log.d("signIn"," start signIn build awaiting result")
        val result = try {
            Log.d("signIn"," start signIn build awaiting result")
            oneTapClient.beginSignIn(
                buildSignInRequest()
            ).await()
        } catch(e: Exception) {
            Toast.makeText(
                context,
                "Error: ${e.printStackTrace()}" ,
                Toast.LENGTH_LONG
            ).show()
            e.printStackTrace()
            if(e is CancellationException) throw e
            null
        }

        Log.d("signIn","signIn build complete awaiting result")
        return result?.pendingIntent?.intentSender
    }



    suspend fun signInWithIntent(intent: Intent): SignInResult {
        val credential = oneTapClient.getSignInCredentialFromIntent(intent)
        val googleIdToken = credential.googleIdToken
        val googleCredentials = GoogleAuthProvider.getCredential(googleIdToken, null)
        Log.d("signInWithIntent","credential ${credential}, googleIdToken: ${googleIdToken}, googleCredentials:${googleCredentials}")
        return try {
            auth.getAccessToken(true)
            val user = auth.signInWithCredential(googleCredentials).await().user
            SignInResult(
                data = user?.run {
                    UserData(
                        userId = uid,
                        username = displayName,
                        profilePictureUrl = photoUrl?.toString()
                    )
                },
                errorMessage = null
            )
        } catch(e: Exception) {
            e.printStackTrace()
            if(e is CancellationException) throw e
            SignInResult(
                data = null,
                errorMessage = e.message
            )
        }
    }

    suspend fun signOut() {
        try {
            oneTapClient.signOut().await()
            auth.signOut()
        } catch(e: Exception) {
            e.printStackTrace()
            if(e is CancellationException) throw e
        }
    }

    fun getSignedInUser(): UserData? = auth.currentUser?.run {
        UserData(
            userId = uid,
            username = displayName,
            profilePictureUrl = photoUrl?.toString()
        )
    }

    // Function to get an access token
    fun getAccessToken(): Task<GetTokenResult> {
        // Force refresh to get a new token
        return auth.currentUser?.getIdToken(true)
            ?: Tasks.forException(Exception("User not signed in")) // Replace this with appropriate error handling
    }

    private fun buildSignInRequest(): BeginSignInRequest {
        Log.d("signIn"," start buildSignInRequest build awaiting result")
        return BeginSignInRequest.Builder()
            .setGoogleIdTokenRequestOptions(
                GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId(context.getString(R.string.firebase_web_client_id))
                    .build()
            )
            .setAutoSelectEnabled(true)
            .build()
    }

}