package com.vschouppe.artapp

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.Scopes
import com.google.android.gms.common.api.Scope
import com.google.android.gms.location.LocationAvailability
import com.google.android.gms.location.LocationServices
import com.google.api.gax.core.FixedCredentialsProvider
import com.google.auth.Credentials
import com.google.auth.oauth2.AccessToken
import com.google.photos.library.v1.PhotosLibrarySettings
import com.squareup.okhttp.Callback
import com.squareup.okhttp.FormEncodingBuilder
import com.squareup.okhttp.OkHttpClient
import com.squareup.okhttp.Request
import com.squareup.okhttp.RequestBody
import com.squareup.okhttp.Response
import com.vschouppe.artapp.network.GoogleAccountApi
import com.vschouppe.artapp.network.GoogleAccountsService
import com.vschouppe.artapp.network.MarsApi
import com.vschouppe.artapp.network.UserCode
import com.vschouppe.artapp.profile.ProfileScreen
import com.vschouppe.artapp.signin.GoogleAuthUiClient
import com.vschouppe.artapp.signin.SignInResult
import com.vschouppe.artapp.signin.SignInScreen
import com.vschouppe.artapp.signin.SignInViewModel
import com.vschouppe.artapp.signin.UserAddress
import com.vschouppe.artapp.theme.MobileAppsPlaygroundTheme
import com.vschouppe.artapp.zoe.ArtWindow
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.postgrest.Postgrest
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject
import retrofit2.HttpException
import java.io.IOException


val supabase = createSupabaseClient(
    supabaseUrl = "https://seklykjnuhwrcsrwbbst.supabase.co",
    supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InNla2x5a2pudWh3cmNzcndiYnN0Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3MDgwNTAwMjcsImV4cCI6MjAyMzYyNjAyN30.M-yIZB72FkSkLv3onuRcWISr4l1UTbG8SOUtzOOCyLc"
) {
    install(Auth){

    }
    install(Postgrest){}
}

class ArtApp : ComponentActivity() {

    // Configure sign-in to request the user's ID, email address, and basic
    // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
    var gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestEmail()
        .requestScopes(Scope("https://www.googleapis.com/auth/photoslibrary.readonly"))
        .build()

//    private val googleSignInClient = GoogleSignIn.getClient(this, gso)

    private val googleAuthUiClient by lazy {
        GoogleAuthUiClient(
            context = applicationContext,
            oneTapClient = Identity.getSignInClient(applicationContext)
        )
    }
    private val mGoogleSignInClient: GoogleSignInClient? = null
    private val RC_GET_AUTH_CODE = 9003

    @RequiresApi(34)
    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d("onCreate", "mGoogleSignInClient start")

        val account = GoogleSignIn.getLastSignedInAccount(this)
        Log.d("onCreate", "account: ${account}")
        val serverClientId = getString(R.string.google_web_client_id)
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestScopes(Scope(Scopes.DRIVE_APPFOLDER))
            .requestServerAuthCode(serverClientId)
            .requestEmail()
            .build()
        // [END configure_signin]

        // Build GoogleAPIClient with the Google Sign-In API and the above options.
        // [END configure_signin]

        // Build GoogleAPIClient with the Google Sign-In API and the above options.
        var mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


        setContent {
            MobileAppsPlaygroundTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val activity = this

                    NavHost(
                        navController = navController,
                        startDestination = "sign_in"
                    ) {
                        composable("art_window") {
                            ArtWindow(
                                navigation = { navController.navigate("profile") }
                            )
                        }
                        composable("sign_in") {
                            val viewModel = viewModel<SignInViewModel>()
                            val state by viewModel.state.collectAsStateWithLifecycle()
                            LaunchedEffect(key1 = Unit) {
                                if (googleAuthUiClient.getSignedInUser() != null) {
                                    navController.navigate("profile")
                                    Log.d("sign_in","User ${googleAuthUiClient.getSignedInUser()} still signed in")
                                }
                                checkLocationPermissions()
                                val userAddress: UserAddress? = getUserAddress()
                                Log.d("sign_in","${userAddress}")
                                if (userAddress != null) {
                                    viewModel.updateAddress(userAddress)
                                    Log.d("sign_in","Updated address to viewModel")
                                }
                            }

                            val launcher = rememberLauncherForActivityResult(
                                contract = ActivityResultContracts.StartIntentSenderForResult(),
                                onResult = { result ->
                                    Log.d("launcher", "result code ${result.resultCode}")
                                    if (result.resultCode == RESULT_OK) {
                                        lifecycleScope.launch {
                                            val signInResult = googleAuthUiClient.signInWithIntent(
                                                intent = result.data ?: return@launch
                                            )
                                            Log.d("launcher", "signInResult ${signInResult}")
                                            viewModel.onSignInResult(signInResult)
                                            Log.d(
                                                "signInResult",
                                                " signInResult data ${signInResult.data} and" +
                                                        "${signInResult.errorMessage} or ${signInResult.toString()}"
                                            )
                                        }
                                    } else if (result.resultCode == RESULT_CANCELED) {
                                        Toast.makeText(
                                            applicationContext,
                                            "StartIntentSenderForResult result cancelled",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                }
                            )


                            val googleSigninApiLauncher = rememberLauncherForActivityResult(
                                contract = ActivityResultContracts.StartIntentSenderForResult(),
                                onResult = { result ->
                                    Log.d("launcher", "result code ${result.resultCode}")
//                                    if (result.resultCode == RESULT_OK) {
//                                        lifecycleScope.launch {
//
//                                            val signInResult = googleAuthUiClient.signInWithIntent(
//                                                intent = result.data ?: return@launch
//                                            )
//                                            Log.d("launcher", "signInResult ${signInResult}")
//                                            viewModel.onSignInResult(signInResult)
//                                            Log.d(
//                                                "signInResult",
//                                                " signInResult data ${signInResult.data} and" +
//                                                        "${signInResult.errorMessage} or ${signInResult.toString()}"
//                                            )
//                                        }
//                                    } else if (result.resultCode == RESULT_CANCELED) {
//                                        Toast.makeText(
//                                            applicationContext,
//                                            "StartIntentSenderForResult result cancelled",
//                                            Toast.LENGTH_LONG
//                                        ).show()
//                                    }
                                }
                            )

//                            LaunchedEffect(key1 = state.address) {
//                                if (state.address != null) {
//                                    viewModel.updateAddress()
//                                }
//                            }
                            LaunchedEffect(key1 = state.isSignInSuccessful) {
                                if (state.isSignInSuccessful) {
                                    Toast.makeText(
                                        applicationContext,
                                        "Sign in successful",
                                        Toast.LENGTH_LONG
                                    ).show()
                                    Log.d("googleWithCredentialManagerSignin", "pre nav to profile")
                                    navController.navigate("profile")
                                    Log.d("googleWithCredentialManagerSignin", "${viewModel.state}")
                                    viewModel.resetState()
                                }
                            }

                            // Get the Context using LocalContext
                            SignInScreen(
                                state = state,
                                googleWithCredentialManagerSignin = {
                                    val activityContext = this@ArtApp
                                    lifecycleScope.launch {
                                        Log.d("googleWithCredentialManagerSignin", "pre googleWithCredentialManagerSignin")
                                        val sir : SignInResult = googleAuthUiClient.GoogleSignIn(activityContext, activity)
                                        Log.d("googleWithCredentialManagerSignin", "sir ${sir}")
                                        viewModel.onSignInResult(sir)
                                    }
                                },
                                onSignInClick = {
                                    Log.d("SignInScreen", "launching event")
                                    lifecycleScope.launch {

                                        Log.d("SignInScreen","start UserCode")
                                        try{
                                            val service = GoogleAccountApi.retrofitService
                                            Log.d("SignInScreen","service.toString() ${service.toString()} ")

                                            val listResult = MarsApi.retrofitService.getPhotos()

                                            Log.d("SignInScreen","mars list ${listResult} ")

//                                            val userCode : UserCode? = service.getUserCode(
//                                               "74689574541-nrhrj8i24ogc9r3b6t89vjftnodrc07k.apps.googleusercontent.com",
//                                                "https://www.googleapis.com/auth/photoslibrary.readonly"
//                                            )
                                            val userCode : UserCode? = service.getUserCode(
                                               "74689574541-3sq4b47jsepnbbgqduapilsr5pff40ug.apps.googleusercontent.com",
                                                "email profile"
                                            )
                                            Log.d("SignInScreen","start userCode ${userCode} ")

                                            val accessToken: AccessToken? =
                                                service.getAccessToken(
                                                    "74689574541-kq8209stu4goar54qaet5qu463733sur.apps.googleusercontent.com",
                                                    "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",
                                                    "4/0AeaYSHCZGXfmcX4qPMwa-Bsxy3XZ3vr7j8qenoiAhnq530UXGHY3LMgJyumCIN1_o8RymQ",
                                                    GoogleAccountsService.ACCESS_GRANT_TYPE
                                                )
                                            Log.d("SignInScreen","start accessToken ${accessToken} ")

//                                            https://accounts.google.com/o/oauth2/v2/auth?
//                                        // redirect_uri=https%3A%2F%2Fdevelopers.google.com%2Foauthplayground&prompt=consent&response_type=code&client_id=407408718192.apps.googleusercontent.com&scope=https%3A%2F%2Fwww.googleapis.com%2Fauth%2Fphotoslibrary.readonly&access_type=offline

                                        }catch(e: Exception){
                                            Log.d("SignInScreen","GoogleAccountApi API error: ${e.message} ")
                                            Log.d("SignInScreen","GoogleAccountApi API error: $e ")
                                            Log.d("SignInScreen","GoogleAccountApi API error: ${e.stackTrace.toString()} ")

                                        }
//                                        Get use


//                                        Log.d("GoogleAPI","start GoogleAuthApi ")
//                                        val authToken = GoogleAccountApi.retrofitService.getAccessToken(
//                                            R.string.google_web_client_id.toString(),
//                                            R.string.google_web_client_secret.toString(),
//                                            userCode.getDeviceCode(),
//                                            GoogleAccountsService.ACCESS_GRANT_TYPE);
//                                        Log.d("GoogleAPI","GoogleAuthApi ${authToken.toString()}")

//                                        Log.d("SignInScreen", "pre googleAuthUiClient signIn")
//                                        val signInIntentSender = googleAuthUiClient.signIn()
//                                        launcher.launch(
//                                            IntentSenderRequest.Builder(
//                                                signInIntentSender ?: return@launch
//                                            ).build()
//                                        )
//
//                                        Log.d("SignInScreen", "post googleAuthUiClient signIn")
                                    }
                                },
                                locationClick = {
                                    lifecycleScope.launch {
                                        var userAddress = getUserAddress()
                                        Toast.makeText(
                                            applicationContext,
                                            "city: ${userAddress?.city}" + "\n" +
                                                    "countryName: ${userAddress?.countryName}" + "\n" +
                                                    "latitude: ${userAddress?.latitude}" + "\n" +
                                                    "longitude: ${userAddress?.longitude}",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                }
                            )
                        }
                        composable("profile") {
                            ProfileScreen(
                                userData = googleAuthUiClient.getSignedInUser(),
                                onSignOut = {
                                    lifecycleScope.launch {
                                        googleAuthUiClient.signOut()
                                        Toast.makeText(
                                            applicationContext,
                                            "Signed out",
                                            Toast.LENGTH_LONG
                                        ).show()
                                        navController.navigate("sign_in")
                                    }
                                },
                                navToZoe = {
                                    navController.navigate("art_window")
                                },
                                loadAlbums = {
//                                    load the albums
                                    lifecycleScope.launch {
                                        Log.d("GoogleAPI", "We're loading the google albums")
                                        try {
//                                            signIn()
                                            val token =  googleAuthUiClient.getAccessToken()

                                            Log.d("GoogleAPI","token ${token}")
                                            if (token!=null){
                                                val client = OkHttpClient()
                                                val requestBody: RequestBody = FormEncodingBuilder()
                                                    .add("grant_type","authorization_code")
                                                    .add("client_id", "")
                                                    .add("client_secret", "")
                                                    .add("redirect_uri", "")
                                                    .add("code", R.string.google_web_client_id.toString())
//                                                    .add("grant_type", "authorization_code")
//                                                    .add("response_type", "code")
//                                                    .add("prompt", "consent")
//                                                    .add("client_id", R.string.client_id_notsure.toString())
//                                                    .add("client_secret", R.string.google_web_client_secret.toString())
//                                                    .add("client_secret", "407408718192.apps.googleusercontent.com")
//                                                    .add("client_secret", "{clientSecret}")
//                                                    .add("redirect_uri","https://developers.google.com/oauthplayground")
//                                                    .add("scope", "https://www.googleapis.com/auth/photoslibrary.readonly")
//                                                    .add("id_token", token) // Added this extra parameter here
//                                                    .add("id_token", "1//040lafZGVRjrUCgYIARAAGAQSNwF-L9Ir3HPUkZKup4Z59KgCP3qtm55zPpolf7YiutwRPfosTW35J-8Ivg8k15Tom2J_u7NRiHo") // Added this extra parameter here
//                                                    .add("access_type", "offline") // Added this extra parameter here
                                                    .build();


                                                Log.d("GoogleAPI","requestBody ${requestBody.toString()}")
                                                val request: Request = Request.Builder()
                                                    .url("https://www.googleapis.com/oauth2/v4/token")
//                                                    .url("https://accounts.google.com/o/oauth2/v2/auth")
                                                    .post(requestBody)
                                                    .build()
                                                Log.d("GoogleAPI","request urlString ${request.urlString()}")
                                                client.newCall(request)
                                                    .enqueue(object : Callback {
                                                        override fun onFailure(
                                                            request: Request?,
                                                            e: IOException
                                                        ) {
                                                            Log.e("GoogleAPI", "failure ${e.toString()}")
                                                        }

                                                        @Throws(IOException::class)
                                                        override fun onResponse(response: Response) {
                                                            try {
                                                                Log.i("GoogleAPI", "success")
                                                                Log.i("GoogleAPI", "response ${response.body()}")
                                                                Log.i("GoogleAPI", "response ${response.body().toString()}")
                                                                Log.i("GoogleAPI", "response ${response.toString()}")
                                                                val jsonObject: JSONObject =
                                                                    JSONObject(
                                                                        response.toString()
                                                                    )
                                                                Log.i("GoogleAPI", "jsonObject : ${jsonObject}")
                                                                val message = jsonObject.toString(5)
                                                                Log.i("GoogleAPI", "message : ${message}")
                                                            } catch (e: JSONException) {
                                                                e.printStackTrace()
                                                            }
                                                        }
                                                    })

                                                val listResult = MarsApi.retrofitService.getPhotos()
                                                Log.d("GoogleAPI","MarsApi ${listResult}")
                                                Log.d("GoogleAPI","Now Lets get Google albums")
                                                Log.d("GoogleAPI","with token ${token}")
                                                var creds : Credentials = googleAuthUiClient.getUserCredentials(token)
                                                Log.d("GoogleAPI","Google authenticationType ${creds.authenticationType}")
                                                Log.d("GoogleAPI","Google requestMetadata ${creds.requestMetadata}")
                                                var settings = PhotosLibrarySettings.newBuilder()
                                                    .setCredentialsProvider(
                                                        FixedCredentialsProvider.create(creds)
                                                    )
                                                    .build();
                                                Log.d("GoogleAPI","Google settings ${settings}")
//                                                try {
//                                                    PhotosLibraryClient.initialize(settings)
//                                                        .use { photosLibraryClient ->
//                                                            Log.d("GoogleAPI","Google photosLibraryClient: ${photosLibraryClient}")
//
//                                                            var response : ListAlbumsPagedResponse = photosLibraryClient.listAlbums()
//                                                            Log.d("GoogleAPI","Google photosLibraryClient response:${response.toString()}")
//                                                            for (album in response.iterateAll()) {
//                                                                // Get some properties of an album
//                                                                val id = album.id
//                                                                val title = album.title
//                                                                val productUrl = album.productUrl
//                                                                val coverPhotoBaseUrl =
//                                                                    album.coverPhotoBaseUrl
//                                                                // The cover photo media item id field may be empty
//                                                                val coverPhotoMediaItemId =
//                                                                    album.coverPhotoMediaItemId
//                                                                val isWritable = album.isWriteable
//                                                                val mediaItemsCount =
//                                                                    album.mediaItemsCount
//                                                            }
//                                                        }
//                                                } catch (e: ApiException) {
//                                                    Log.d("GoogleAPI","Google albums ApiException ${e}")
//                                                    // Error during album creation
//                                                } catch( e: HttpException){
//                                                    Log.d("GoogleAPI","Google albums HttpException ${e}")
//                                                } catch( e: Exception){
//                                                    Log.d("GoogleAPI","Google stackTrace ${e.stackTrace}")
//                                                    Log.d("GoogleAPI","Google message ${e.message}")
//                                                    Log.d("GoogleAPI","Google e ${e.toString()}")
//                                                }
                                            }
                                        } catch (e: IOException) {
                                            Log.d("GoogleAPI", "IOException ${e}")
                                        } catch (e: HttpException) {
                                            Log.d("GoogleAPI", "HttpException ${e}")
                                        }
                                        Log.d("GoogleAPI", "Done loading the google albums")
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private suspend fun getUserAddress() :  UserAddress?{
        Log.d("getUserAddress", "locationClick event")

        var userAddress = UserAddress()
        val client = LocationServices.getFusedLocationProviderClient(applicationContext)
        if (hasLocationPermissions(this)) {
            client.lastLocation
                .addOnSuccessListener { location: Location? ->
                    Log.d("getUserAddress", "successlistener ok")
                    if (location != null) {
                        Log.d(
                            "getUserAddress",
                            "lat ${location.latitude} + long ${location.longitude}"
                        )
                        userAddress = UserAddress(
                            latitude = location.latitude,
                            longitude = location.longitude
                        )
                        var addresses = getAddress(location.latitude, location.longitude)

                        addresses?.get(0)
                            ?.let { it1 ->
                                userAddress.city = it1.locality
                                userAddress.countryName = it1.countryName
                                Log.d(
                                    "getUserAddress","userAddress1: ${userAddress}"
                                )
                            }
                        Log.d(
                            "getUserAddress","userAddress2: ${userAddress}"
                        )
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e(
                        "getUserAddress",
                        "LocationAvailability ${exception}"
                    )
                }

            Log.d(
                "getUserAddress","userAddress3: ${userAddress}"
            )
            client.locationAvailability.addOnSuccessListener { locationAv: LocationAvailability ->
                Log.d(
                    "getUserAddress",
                    "locationAv.isLocationAvailable : ${locationAv.isLocationAvailable}"
                )
            }
                .addOnFailureListener { exception ->
                    Log.e(
                        "getUserAddress",
                        "LocationAvailability ${exception}"
                    )
                }
            delay(3000)
            Log.d(
                "getUserAddress","userAddress4: ${userAddress}"
            )
            return userAddress
        }

        Log.d(
            "getUserAddress","userAddress4: ${userAddress}"
        )
        return userAddress
    }

    private fun hasLocationPermissions(artApp: ArtApp): Boolean {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
            || ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    private fun checkLocationPermissions() {
        if (hasLocationPermissions(this)) {
            Log.d("SignInScreen", "checkLocationPermissions")
            // Permission is not granted, request it
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                123
            )
            Log.d("SignInScreen", "permissions requested")
        } else {
            Log.d("SignInScreen", "permissions granted already")
        }

    }

    private fun getAddress(lat: Double, long: Double): MutableList<Address>? {
        val geocoder = Geocoder(applicationContext)
        var addresses: MutableList<Address>? = null
        try {
            Log.d(
                "getAddress",
                "lat ${lat} + long ${long}"
            )
            Log.d("getAddress", "addresses for ${geocoder.getFromLocation(lat, long, 1)}")
            addresses = geocoder.getFromLocation(lat, long, 5)

            if (addresses != null && addresses.isNotEmpty()) {
                val address: String = addresses[0].getAddressLine(0)
                // 'address' now contains the name or address based on the provided latitude and longitude
                Toast.makeText(applicationContext, "Location: $address", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(applicationContext, "Address not found", Toast.LENGTH_LONG).show()
            }
            Log.d("getAddress", "address ${addresses}")
        } catch (e: Exception) {
            Log.e(
                "getAddress",
                "LocationAvailability ${e}"
            )
        }
        return addresses
    }
}
