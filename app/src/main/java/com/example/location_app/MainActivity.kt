package com.example.location_app

import android.content.Context
import android.Manifest
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.location_app.ui.theme.Location_AppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val viewModel :LocationViewModel = viewModel()
            Location_AppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                   App(viewModel)
                }
            }
        }
    }
}

@Composable
fun App(viewModel: LocationViewModel){
    val context = LocalContext.current
    val checkPermission = CheckPermission(context)
    Location_Screen(locationPermission = checkPermission, context = context,viewModel)
}


@Composable
// this will be same in every app you will make , just the number and type of permission will change .
fun Location_Screen(locationPermission : CheckPermission,
                    context : Context,
                    viewModel: LocationViewModel){
    val location = viewModel.location.value

    val address = location?.let {
        locationPermission.reverseGeocoderLocation(location)
    }

    val requestpermissionLauncher =
        rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                // Permission is granted
            } else {
                // Permission is not granted
                val rationaleRequired = ActivityCompat.shouldShowRequestPermissionRationale(
                    context as MainActivity,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
                // if we need to provide reason to our user that why we want permission when it denies it for
                //first time
                if (rationaleRequired){
                    Toast.makeText(context,
                        "Location permission is required to work ", Toast.LENGTH_LONG).show()}
                    else{
                        // this will be executed when the user know the reason but still denied.
                        Toast.makeText(context,
                            "Turn on The Location In settings", Toast.LENGTH_LONG).show()
                    }
                }
            })

    Column(modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center) {

        if (location != null){
            Text(text = "Latitude - ${location.Latitude} , Longitude - ${location.Longitude} \n $address",
                textAlign = TextAlign.Center)
        }else {
        Text(text = "Location in not available")}

        Button(onClick = {
            if (locationPermission.LocationPermission(context)){
                //permission already granted . show the location.
                locationPermission.requestLocationUpdate(viewModel)
            }
            else{
                requestpermissionLauncher.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
            }
        }) {
            Text(text = "Get Location")
        }
    }
        }


