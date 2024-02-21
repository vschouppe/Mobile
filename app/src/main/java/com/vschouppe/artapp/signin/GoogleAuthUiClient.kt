package com.vschouppe.artapp.signin

import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.PasswordCredential
import androidx.credentials.PublicKeyCredential
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.BeginSignInRequest.GoogleIdTokenRequestOptions
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.auth.Credentials
import com.google.auth.oauth2.AccessToken
import com.google.auth.oauth2.UserCredentials
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.vschouppe.artapp.ArtApp
import com.vschouppe.artapp.R
import com.vschouppe.artapp.supabase
import io.github.jan.supabase.exceptions.NotFoundRestException
import io.github.jan.supabase.exceptions.RestException
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.Google
import io.github.jan.supabase.gotrue.providers.builtin.IDToken
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.tasks.await
import java.security.MessageDigest
import java.util.UUID


class GoogleAuthUiClient(
    private val context: Context,
    private val oneTapClient: SignInClient
) {
    private val auth = Firebase.auth
    private val googleWebID = R.string.google_web_client_id
    var googleIdTokenCredential: GoogleIdTokenCredential? = null
    private var googelSignInClient : GoogleSignInClient? =null
    private var credentialManager : CredentialManager? =null

    suspend fun getUserCredentials(token: String): Credentials {

        val a = AccessToken(token, null)
        return UserCredentials.newBuilder()
            .setClientId("74689574541-kq8209stu4goar54qaet5qu463733sur.apps.googleusercontent.com")
            .setClientSecret(googleWebID.toString())
            .setAccessToken(a)
            .build()
    }

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
            Log.d("signOut","Starting signOut duties ${oneTapClient?.toString()}")
            oneTapClient.signOut().await()
            auth.signOut()
            Log.d("signOut","Starting signOut duties ${googelSignInClient?.toString()}")
            credentialManager
            Log.d("signOut","All signOut duties finished")
        } catch(e: Exception) {
            e.printStackTrace()
            if(e is CancellationException) throw e
        }
    }

    fun getSignedInUser(): UserData?  {
        var user: UserData? = null
        if (auth.currentUser !=null){
            user = UserData(
                userId = auth.uid,
                username = auth.currentUser!!.displayName,
                profilePictureUrl = auth.currentUser!!.photoUrl?.toString()
            )
        }else if (googleIdTokenCredential != null){
//            how do get the user details
            user = UserData(
                userId = googleIdTokenCredential?.id,
                username = googleIdTokenCredential?.displayName,
                profilePictureUrl = googleIdTokenCredential?.profilePictureUri?.toString()
            )
        }
        return user
    }

    // Function to get an access token
    fun getAccessToken(): String {
        // Force refresh to get a new token
        var token = ""
        if (googleIdTokenCredential != null){
            token= googleIdTokenCredential!!.idToken
        }
        return token
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


    @RequiresApi(34)
    suspend fun GoogleSignIn(context: Context, activity: ArtApp) : SignInResult{
        Log.d("GoogleSignInButton","start")
        Log.d("googleWithCredentialManagerSignin", "inside lifecyclescope: ${context}")

        val rawNonce = UUID.randomUUID().toString()
        val bytes = rawNonce.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        val hashedNonce = digest.fold("") {str,it -> str + "%02x".format(it)}
        Log.d("GoogleSignInButton","got hashedNonce ${hashedNonce}")

        return try {

            credentialManager = CredentialManager.create(context)
            Log.d("GoogleSignInButton","credentialManager created")

            val googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(context.getString(googleWebID))
                .setNonce(hashedNonce)
                .build()
            Log.d("GoogleSignInButton", "googleIdOption created")
            val request: GetCredentialRequest = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()
            Log.d("GoogleSignInButton", "request created")
            Log.d("GoogleSignInButton", "launched")
            Log.d("googleWithCredentialManagerSignin", "inside lifecyclescope: ${context}")
            val result = credentialManager!!.getCredential(
                request = request,
                context = context
            )
            handleSignIn(result)
            var googleIdToken = googleIdTokenCredential!!.idToken
            Log.d("GoogleSignInButton", "googleIdTokenCredential created")
            Log.d("GoogleSignInButton", "googleIdTokenCredential ${googleIdTokenCredential!!.id}")
            Log.d("GoogleSignInButton", "googleIdTokenCredential ${googleIdToken}")
            Toast.makeText(context, "Successfully logged in ${googleIdTokenCredential!!.displayName}", Toast.LENGTH_LONG).show()

            supabase.auth.signInWith(IDToken){
                idToken= googleIdToken
                provider= Google
                nonce = rawNonce
            }
            try {
                supabase.from("login").insert(mapOf("content" to "LOGIN"))
                Log.d("DB_WRITE","Written login to DB ")
            }catch(e: RestException){
                Log.e("DB_WRITE","something went wrong + ${e}")
            }catch(e: NotFoundRestException){
                Log.e("DB_WRITE","something went wrong + ${e}")
            }
            var user: UserData
            user =
                UserData(
                    userId = googleIdTokenCredential!!.id,
                    username = googleIdTokenCredential!!.displayName,
                    profilePictureUrl = googleIdTokenCredential!!.profilePictureUri.toString()
                )
            Log.d("GoogleSignInButton", " user details : ${user}")
            Toast.makeText(context, "welcome :${user.username} ", Toast.LENGTH_LONG).show()
            SignInResult(
                data = user?.run {
                    UserData(
                        userId = user.userId,
                        username = user.username,
                        profilePictureUrl = user.profilePictureUrl?.toString()
                    )
                },
                errorMessage = null
            )
        } catch(e: Exception) {
            e.printStackTrace()
//            if(e is GetCredentialException || e is CancellationException) throw e
            SignInResult(
                data = null,
                errorMessage = e.message
            )
        }
    }

    fun handleSignIn(result: GetCredentialResponse) {
        val handleSignIn_TAG = "handleSignIn"
        // Handle the successfully returned credential.
        val credential = result.credential

        when (credential) {

            is PublicKeyCredential -> {
                val responseJson = credential.authenticationResponseJson
                // Share responseJson i.e. a GetCredentialResponse on your server to
                // validate and  authenticate
                Log.e(handleSignIn_TAG, "responseJson : ${responseJson}")
            }
            is PasswordCredential -> {
                val username = credential.id
                val password = credential.password
                Log.e(handleSignIn_TAG, "PasswordCredential : ${username} & ${password}")
                // Use id and password to send to your server to validate
                // and authenticate
            }
            is CustomCredential -> {
                if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    try {
                        // Use googleIdTokenCredential and extract id to validate and
                        // authenticate on your server.
                        googleIdTokenCredential = GoogleIdTokenCredential
                            .createFrom(credential.data)
                        Log.d(handleSignIn_TAG, " TYPE_GOOGLE_ID_TOKEN_CREDENTIAL credential ${credential}")
                    } catch (e: GoogleIdTokenParsingException) {
                        Log.e(handleSignIn_TAG, "Received an invalid google id token response", e)
                    }
                } else {
                    // Catch any unrecognized custom credential type here.
                    Log.e(handleSignIn_TAG, "Unexpected type of credential")
                }
            }
        }
    }

}