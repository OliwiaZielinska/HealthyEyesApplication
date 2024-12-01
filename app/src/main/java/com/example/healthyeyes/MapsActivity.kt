package com.example.healthyeyes

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import org.json.JSONException
import org.json.JSONObject

/**
 * The `MapsActivity` class allows the user to use MapGoogle inside the application to search
 * for the nearest opticians and ophthalmologists.
 */
class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
    private lateinit var map: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var lastLocation: Location
    private lateinit var returnFromMapsButton: Button
    private lateinit var navigationButton: Button
    private lateinit var specialPlacesSearchButton: Button
    private lateinit var typeSpinnerMaps: Spinner
    private lateinit var distanceSeekBar: SeekBar
    private lateinit var distanceText: TextView
    private var searchRadius = 5000
    private var destinationLatLng: LatLng? = null
    private var userID: String = ""

    /**
     * Constants and static variables that are common to all instances of the class, relating
     * to location permissions.
     */
    companion object {
        private const val LOCATION_REQUEST_CODE = 1
    }

    /**
     * Method for creating an activity to set the layout, initialise fields and buttons
     * and operate them.
     *
     * @param savedInstanceState saved application status.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        userID = intent.getStringExtra("userID").toString()
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        returnFromMapsButton = findViewById(R.id.returnFromMapsButton)
        specialPlacesSearchButton = findViewById(R.id.specialPlacesSearchButton)
        typeSpinnerMaps = findViewById(R.id.typeSpinnerMaps)
        navigationButton = findViewById(R.id.navigationButton)
        val types = arrayOf("Optyk", "Okulista")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, types)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        typeSpinnerMaps.adapter = adapter

        returnFromMapsButton.setOnClickListener {
            openMainActivity()
        }
        navigationButton.setOnClickListener {
            openGoogleMapsForNavigation()
        }

        distanceSeekBar = findViewById(R.id.distanceSeekBar)
        distanceText = findViewById(R.id.distanceText)
        val distanceValues = arrayOf(5000, 10000, 20000, 40000, 50000, 100000, 200000)
        val distanceLabels = arrayOf("5 km", "10 km", "20 km", "40 km", "50 km", "100 km", "200 km")
        distanceSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                searchRadius = distanceValues[progress]
                distanceText.text = "Odległość: ${distanceLabels[progress]}"
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        specialPlacesSearchButton.setOnClickListener {
            val selectedType = typeSpinnerMaps.selectedItem.toString()
            val keyword = when (selectedType) {
                "Optyk" -> "optician"
                "Okulista" -> "ophthalmologist"
                else -> ""
            }
            if (keyword.isNotEmpty()) {
                findNearbyPlaces(keyword)
            } else {
                Toast.makeText(this, "Wybierz typ miejsca", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Function to search for an ophthalmologist or optician based on the keyword given.
     *
     * @param keyword A keyword that is the type of place to be searched.
     */
    private fun findNearbyPlaces(keyword: String) {
        val apiKey = getApiKeyFromManifest()
        if (apiKey != null) {
            val locationString = "${lastLocation.latitude},${lastLocation.longitude}"
            val url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=$locationString&radius=$searchRadius&type=store&keyword=$keyword&key=$apiKey"

            val request = object : StringRequest(Method.GET, url, Response.Listener { response ->
                try {
                    val jsonObject = JSONObject(response)
                    val results = jsonObject.getJSONArray("results")

                    map.clear()

                    for (i in 0 until results.length()) {
                        val place = results.getJSONObject(i)
                        val latLng = place.getJSONObject("geometry").getJSONObject("location")
                        val lat = latLng.getDouble("lat")
                        val lng = latLng.getDouble("lng")
                        val placeName = place.getString("name")

                        map.addMarker(
                            MarkerOptions()
                                .position(LatLng(lat, lng))
                                .title(placeName)
                        )
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }, Response.ErrorListener { error ->
                Log.e("MapsActivity", "Błąd podczas wyszukiwania: ${error.message}")
            }) {}

            Volley.newRequestQueue(this).add(request)
        } else {
            Log.e("MapsActivity", "Brak klucza API w AndroidManifest.xml")
        }
    }

    /**
     * Function to retrieve Google Maps API key .
     *
     * @return API key as a string.
     */
    private fun getApiKeyFromManifest(): String? {
        return try {
            val applicationInfo = packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA)
            val bundle = applicationInfo.metaData
            bundle.getString("com.google.android.geo.API_KEY")
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Method for handling the map.
     *
     * @param googleMap Google map object.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map.uiSettings.isZoomControlsEnabled = true
        map.setOnMarkerClickListener(this)
        setUpMap()
    }

    /**
     * Setting up the map when it is loaded and retrieving the user's location.
     */
    private fun setUpMap() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_REQUEST_CODE)
            return
        }
        map.isMyLocationEnabled = true
        fusedLocationClient.lastLocation.addOnSuccessListener(this) { location ->
            if (location != null) {
                lastLocation = location
                val currentLatLong = LatLng(location.latitude, location.longitude)
                placeMarkerOnMap(currentLatLong)
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLong, 13f))
            }
        }
    }

    /**
     * Adds a marker to the map at the specified location.
     *
     * @param location Location where the marker will be placed.
     */
    private fun placeMarkerOnMap(location: LatLng) {
        val markerOptions = MarkerOptions().position(location).title("Twoja lokalizacja")
        map.addMarker(markerOptions)
    }

    /**
     * Method to set the navigation target on the clicked location.
     *
     * @param marker The marker that was clicked.
     * @return Returns a Boolean value indicating that the marker is no longer ‘clicked’ (false).
     */
    override fun onMarkerClick(marker: Marker): Boolean {
        destinationLatLng = marker.position
        Toast.makeText(this, "Wybrano miejsce: ${marker.title}. Kliknij NAWIGUJ, aby rozpocząć nawigację.", Toast.LENGTH_SHORT).show()
        return false
    }

    /**
     * Method to navigate the user to a destination in Google Maps.
     */
    private fun openGoogleMapsForNavigation() {
        if (destinationLatLng != null) {
            val uri = "google.navigation:q=${destinationLatLng!!.latitude},${destinationLatLng!!.longitude}&mode=d"
            val intent = Intent(Intent.ACTION_VIEW, android.net.Uri.parse(uri))
            intent.setPackage("com.google.android.apps.maps")
            if (intent.resolveActivity(packageManager) != null) {
                startActivity(intent)
            } else {
                Toast.makeText(this, "Zainstaluj aplikację Google Maps, aby korzystać z nawigacji.", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Wybierz najpierw miejsce, klikając na pinezkę.", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Function to return to the main application window.
     */
    private fun openMainActivity() {
        val intent = Intent(this, MainViewApp::class.java)
        intent.putExtra("userID", userID)
        startActivity(intent)
    }
}
