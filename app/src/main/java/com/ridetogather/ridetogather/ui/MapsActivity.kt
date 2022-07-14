package com.ridetogather.ridetogather.ui

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.net.PlacesClient
import com.ridetogather.ridetogather.R
import com.ridetogather.ridetogather.databinding.ActivityMapsBinding
import java.util.jar.Manifest


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var mFusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var mPlacesClient: PlacesClient
    private lateinit var predictionList: List<AutocompletePrediction>
    private lateinit var mLastKnownLocation: Location
    private lateinit var locationCallback: LocationCallback

    var mLocationPermissionGranted = false;


    object mapObject {
        const val FINE_LOCATION = android.Manifest.permission.ACCESS_FINE_LOCATION
        const val COARSE_LOCATION = android.Manifest.permission.ACCESS_COARSE_LOCATION
        const val LOCATION_PERMISSION_REQUEST_CODE=1234
        const val DEFAULT_ZOOM = 16f
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getLocatonPermission()

    }

    private fun getDeviceLocation(){
        mFusedLocationProviderClient=LocationServices.getFusedLocationProviderClient(this)

        try {
            if(mLocationPermissionGranted){
                val location = mFusedLocationProviderClient.getLastLocation()
                location.addOnCompleteListener {
                    if(location.isSuccessful){
                        val currentLocation = it.getResult()
                        val newLatLng = LatLng(currentLocation.latitude,currentLocation.longitude)
                        moveCamera(newLatLng,mapObject.DEFAULT_ZOOM)
                    }else{
                        Toast.makeText(this@MapsActivity,"Unable to find the current location",Toast.LENGTH_SHORT).show()
                    }
                }
            }

        }catch (e:SecurityException){
            Log.e("SecurityException:",e.message.toString())
        }


    }

    private fun moveCamera(latLang:LatLng,zoom:Float){
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLang,zoom))


    }
        private fun initMap(){
            binding = ActivityMapsBinding.inflate(layoutInflater)
            setContentView(binding.root)


            val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
            mapFragment.getMapAsync(this)
        }

   
    override fun onMapReady(googleMap: GoogleMap) {
        mMap= googleMap

        if(mLocationPermissionGranted){

            if (ActivityCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {

                return
            }else{
                getDeviceLocation()
                mMap.setMyLocationEnabled(true)
            }

        }

//        val latLang= LatLng(30.7333,76.7794)
//        val markerOptions=MarkerOptions().position(latLang).title("Chandigarh")
//        mMap?.addMarker(markerOptions)
//        mMap?.moveCamera(CameraUpdateFactory.newLatLng(latLang))
//        mMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(latLang,16f))
//        mMap.setMyLocationEnabled(true)
//        mMap.uiSettings.setMyLocationButtonEnabled(true)

    }

    private fun getLocatonPermission(){
       val permission : Array<String> = arrayOf(mapObject.FINE_LOCATION,
                                            mapObject.COARSE_LOCATION )

        if(ContextCompat.checkSelfPermission(this@MapsActivity,mapObject.FINE_LOCATION)==PackageManager
                .PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this@MapsActivity,mapObject.COARSE_LOCATION
            )== PackageManager.PERMISSION_GRANTED){

            mLocationPermissionGranted=true;
            initMap()

        }else{

        ActivityCompat.requestPermissions(this@MapsActivity,permission,mapObject.LOCATION_PERMISSION_REQUEST_CODE)

        }

    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {

        mLocationPermissionGranted=false

        when(requestCode){
            mapObject.LOCATION_PERMISSION_REQUEST_CODE ->{
                if(grantResults.size >0){
                    for(i in 0..grantResults.size-1){
                        if(grantResults[i] != PackageManager.PERMISSION_GRANTED){
                            mLocationPermissionGranted = false
                            return
                        }
                    }

                        mLocationPermissionGranted=true
                        initMap()
                }
            }

        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}