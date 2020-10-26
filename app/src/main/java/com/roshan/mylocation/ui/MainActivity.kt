package com.roshan.mylocation.ui

import android.Manifest
import android.content.Context
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.maps.CameraUpdate
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.roshan.mylocation.R
import com.roshan.mylocation.callback.DialogNegativePressedListener
import com.roshan.mylocation.handlers.PreferenceHandler
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), DialogNegativePressedListener {

    private lateinit var locationManager : LocationManager
    private lateinit var preference : PreferenceHandler
    private val requestDialogTag : String = "Request Dialog"
    private val permissionDialogTag : String = "Permission Dialog"
    private val minTime: Long = 5000
    private val minDistance: Float = 0f
    private lateinit var marker : Marker
    private lateinit var googleMapInstance : GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setLocationManager()
        setPreference()
        fragmentGoogleMap.onCreate(savedInstanceState)
        setUpAndPointLocationOnGoogleMap()
    }

    override fun onStart() {
        super.onStart()
        requestAndHandlePermission()
        fragmentGoogleMap.onStart()
        Log.d("Lifecycle", "On Start")
    }

    private fun setLocationManager() { locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager }
    private fun setPreference() { preference = PreferenceHandler(this) }
    private fun setMapViewCallback() { fragmentGoogleMap.getMapAsync(mapViewCallback) }

    private fun requestAndHandlePermission() {
        val permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        if (permission != PackageManager.PERMISSION_GRANTED) {
            if (
                    !ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    && preference.getAskedPermissionFirstTimeStatus()
            ) {
                val dialog = PermissionDialog(this)
                dialog.show(supportFragmentManager, permissionDialogTag)
            } else {
                ActivityCompat.requestPermissions(
                        this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 100
                )
                preference.permissionAskedFirstTime()
            }
        } else {
            checkForProvider()
        }
    }

    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
    ) {
        Log.d("Lifecycle", "On Request Permission Result.")
        if (grantResults.isNotEmpty() && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            finish()
        } else {
            checkForProvider()
        }
    }

    private fun checkForProvider() {
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            val alertDialog = RequestDialog(this)
            alertDialog.show(supportFragmentManager, requestDialogTag)
        } else {
            setLocationListener()
        }
    }

    private fun setLocationListener() {
        val permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        if (permission == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, minTime, minDistance
            ) { location ->
                Log.d("Lifecycle", "On Location Changed.")
                tvLatitude.text = location.latitude.toString()
                tvLongitude.text = location.longitude.toString()
                marker.position = getLatLngObject()
                val cameraPosition = CameraPosition.builder().target(getLatLngObject()).zoom(8f).build()
                googleMapInstance.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
            }
        }
    }

    private fun checkForPlayServices() : Boolean {
        val googleApi = GoogleApiAvailability.getInstance()
        val isPlayServicesAvailable = googleApi.isGooglePlayServicesAvailable(this)
        if (isPlayServicesAvailable == ConnectionResult.SUCCESS) {
            return true
        } else if (googleApi.isUserResolvableError(isPlayServicesAvailable)) {
            val dialog = googleApi.getErrorDialog(this, isPlayServicesAvailable, 100, DialogInterface.OnCancelListener {
                Toast.makeText(this, resources.getString(R.string.cancel_error_dialog), Toast.LENGTH_LONG).show()
            })
            dialog.show()
            return false
        } else {
            Toast.makeText(this, resources.getString(R.string.play_services_not_available), Toast.LENGTH_LONG).show()
            return false
        }
    }

    private fun setUpAndPointLocationOnGoogleMap() {
        if (checkForPlayServices()) {
            setMapViewCallback()
        }
    }

    private val mapViewCallback = OnMapReadyCallback { googleMap ->
        googleMap.apply {
            googleMapInstance = this
            val latLng = getLatLngObject()
            val markerTemp = MarkerOptions()
                    .position(latLng)
                    .title("Your current location.")
            marker = addMarker(markerTemp)
        }
    }

    private fun getLatLngObject() : LatLng {
        val lat = tvLatitude.text.toString().toDouble()
        val lng = tvLongitude.text.toString().toDouble()
        return LatLng(lat, lng)
    }

    override fun onNegativePressed() {
        finish()
    }

    override fun onResume() {
        super.onResume()
        fragmentGoogleMap.onResume()
    }

    override fun onStop() {
        super.onStop()
        fragmentGoogleMap.onStop()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        fragmentGoogleMap.onSaveInstanceState(outState)
    }

    override fun onPause() {
        super.onPause()
        fragmentGoogleMap.onPause()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        fragmentGoogleMap.onLowMemory()
    }

    override fun onDestroy() {
        super.onDestroy()
        fragmentGoogleMap.onDestroy()
    }

}