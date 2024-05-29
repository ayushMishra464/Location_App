package com.example.location_app

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.Manifest
import android.annotation.SuppressLint
import android.location.Address
import android.location.Geocoder
import android.os.Looper
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import java.util.Locale


class CheckPermission (val context : Context) {

    // Create a FusedLocationProviderClient object
    private val _fusedLocationClient : FusedLocationProviderClient=
        LocationServices.getFusedLocationProviderClient(context)

 @SuppressLint("Missingpermission")
    // Define a LocationCallback to handle location updates
    fun requestLocationUpdate(viewModel: LocationViewModel){
        val locationCallback = object : LocationCallback(){
            override fun onLocationResult(location: LocationResult) {
                super.onLocationResult(location)
                location.lastLocation?.let {
                    val Location = LocationData(Longitude = it.longitude, Latitude = it.latitude)
                    viewModel.UpdateLocation(Location)
                }
            }
        }

        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,1000
        ).build()

        _fusedLocationClient.requestLocationUpdates(locationRequest,locationCallback, Looper.getMainLooper())
    }




    fun LocationPermission(context: Context) : Boolean {
       return ContextCompat.checkSelfPermission(context,Manifest.permission.ACCESS_COARSE_LOCATION)==
               PackageManager.PERMISSION_GRANTED
}

    fun reverseGeocoderLocation (location : LocationData ) : String {
        val geocoder = Geocoder(context, Locale.getDefault())
        val coordinate = LatLng(location.Longitude,location.Latitude)
        val addresses : MutableList<Address>? =
            geocoder.getFromLocation(coordinate.latitude, coordinate.longitude , 1)

        return if (addresses?.isNotEmpty()== true){
            addresses[0].getAddressLine(0)
        }
        else{
            "Address not found !"
        }
    }


}