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
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.core.app.ActivityCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.Scope
import com.google.android.gms.location.LocationAvailability
import com.google.android.gms.location.LocationServices
import com.vschouppe.artapp.profile.ProfileScreen
import com.vschouppe.artapp.signin.GoogleAuthUiClient
import com.vschouppe.artapp.signin.SignInScreen
import com.vschouppe.artapp.signin.SignInViewModel
import com.vschouppe.artapp.signin.UserAddress
import com.vschouppe.artapp.theme.MobileAppsPlaygroundTheme
import com.vschouppe.artapp.zoe.ArtWindow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException


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

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d("onCreate", "mGoogleSignInClient start")

        Log.d("onCreate", "gso: ${gso}")
//        Log.d("onCreate","mGoogleSignInClient: ${googleSignInClient}")
        val account = GoogleSignIn.getLastSignedInAccount(this)
        Log.d("onCreate", "account: ${account}")

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
                            val welcomeText = R.string.signin_welcome
                            LaunchedEffect(key1 = Unit) {
                                if (googleAuthUiClient.getSignedInUser() != null) {
                                    navController.navigate("profile")
                                }
                                checkLocationPermissions()
                                var userAddress = getUserAddress()
                                if (userAddress != null) {
                                    viewModel.updateAddress(userAddress)
                                    viewModel.updateWelcomeText(welcomeText.toString() + " \n" + userAddress.city)
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
                            LaunchedEffect(key1 = state.isSignInSuccessful) {
                                if (state.isSignInSuccessful) {
                                    Toast.makeText(
                                        applicationContext,
                                        "Sign in successful",
                                        Toast.LENGTH_LONG
                                    ).show()

                                    navController.navigate("profile")
                                    viewModel.resetState()
                                }
                            }

                            // Get the Context using LocalContext
                            SignInScreen(
                                state = state,
                                onSignInClick = {
                                    Log.d("SignInScreen", "launching event")
                                    lifecycleScope.launch {
                                        Log.d("SignInScreen", "pre googleAuthUiClient signIn")
                                        val signInIntentSender = googleAuthUiClient.signIn()
                                        launcher.launch(
                                            IntentSenderRequest.Builder(
                                                signInIntentSender ?: return@launch
                                            ).build()
                                        )
                                        Log.d("SignInScreen", "post googleAuthUiClient signIn")
                                    }
                                },
                                locationClick = {
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
//                                            val token =  account?.serverAuthCode
//                                            Log.d("GoogleAPI","token ${token}")
//                                            if (token!=null){
//                                                val listResult = GoogleApi.retrofitService.getAlbums("Bearer ${token}")
//                                                Log.d("GoogleAPI","albums ${listResult}")
//                                            }
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
    private fun getUserAddress() :  UserAddress?{
        Log.d("SignInScreen", "locationClick event")

        var userAddress = UserAddress()
        val client = LocationServices.getFusedLocationProviderClient(applicationContext)
        if (hasLocationPermissions(this)) {
            client.lastLocation
                .addOnSuccessListener { location: Location? ->
                    Log.d("SignInScreenLocation", "successlistener ok")
                    if (location != null) {
                        Log.d(
                            "SignInScreenLocation",
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
                            }
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e(
                        "SignInScreenLocation",
                        "LocationAvailability ${exception}"
                    )
                }
            client.locationAvailability.addOnSuccessListener { locationAv: LocationAvailability ->
                Log.d(
                    "SignInScreen",
                    "locationAv.isLocationAvailable : ${locationAv.isLocationAvailable}"
                )
            }
                .addOnFailureListener { exception ->
                    Log.e(
                        "SignInScreen",
                        "LocationAvailability ${exception}"
                    )
                }
        }
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
                "SignInScreenLocation",
                "lat ${lat} + long ${long}"
            )
            Log.d("SignInScreenLocation", "addresses for ${geocoder.getFromLocation(lat, long, 1)}")
            addresses = geocoder.getFromLocation(lat, long, 5)

            if (addresses != null && addresses.isNotEmpty()) {
                val address: String = addresses[0].getAddressLine(0)
                // 'address' now contains the name or address based on the provided latitude and longitude
                Toast.makeText(applicationContext, "Location: $address", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(applicationContext, "Address not found", Toast.LENGTH_LONG).show()
            }
            Log.d("SignInScreenLocation", "address ${addresses}")
        } catch (e: Exception) {
            Log.e(
                "SignInScreenLocation",
                "LocationAvailability ${e}"
            )
        }
        return addresses
    }
}

//    private fun signIn() {
//        val signInIntent = googleSignInClient.signInIntent
//        startActivityForResult(signInIntent, RC_SIGN_IN)
//    }
//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//
//        Log.w("onActivityResult", "start onActivityResult")
//        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
//        if (requestCode == RC_SIGN_IN) {
//            // The Task returned from this call is always completed, no need to attach
//            // a listener.
//            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
//            handleSignInResult(task)
//        }
//    }
//
//    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
//        try {
//            val account = completedTask.getResult(ApiException::class.java)
//
//            // Signed in successfully, show authenticated UI.
//            Log.w("handleSignInResult", "signInResult: ${account}")
//        } catch (e: ApiException) {
//            // The ApiException status code indicates the detailed failure reason.
//            // Please refer to the GoogleSignInStatusCodes class reference for more information.
//            Log.w("handleSignInResult", "signInResult:failed code=" + e.statusCode)
//
//        }


