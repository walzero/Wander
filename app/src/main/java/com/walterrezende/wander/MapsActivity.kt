package com.walterrezende.wander

import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import java.util.*

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var map: GoogleMap
    private val TAG = MapsActivity::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.map_options, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(!::map.isInitialized)
            return super.onOptionsItemSelected(item)

        return when (item.itemId) {
            R.id.normal_map -> {
                map.mapType = GoogleMap.MAP_TYPE_NORMAL
                true
            }
            R.id.hybrid_map -> {
                map.mapType = GoogleMap.MAP_TYPE_HYBRID
                true
            }
            R.id.satellite_map -> {
                map.mapType = GoogleMap.MAP_TYPE_SATELLITE
                true
            }
            R.id.terrain_map -> {
                map.mapType = GoogleMap.MAP_TYPE_TERRAIN
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap.apply {

            val latitude = -23.54626445090568
            val longitude = -46.63790340398839

            val spLatlng = LatLng(latitude, longitude)
            val zoomLevel = 12f

            setMapStyle()
            moveCameraToPosition(spLatlng, zoomLevel)
            addCurrentPositionMarker(spLatlng)

            setMapLongClickListener()
            setMapPOIClickListener()
        }
    }

    private fun GoogleMap.moveCameraToPosition(spLatlng: LatLng, zoomLevel: Float) {
        moveCamera(CameraUpdateFactory.newLatLngZoom(spLatlng, zoomLevel))
    }

    private fun GoogleMap.addCurrentPositionMarker(spLatlng: LatLng) {
        addMarker(
            MarkerOptions()
                .position(spLatlng)
                .title(getString(R.string.current_location_pin))
                .snippet(createSnippet(spLatlng))
        )
    }

    private fun GoogleMap.setMapLongClickListener() {
        setOnMapLongClickListener { latLng ->
            addMarker(
                MarkerOptions()
                    .position(latLng)
                    .title(getString(R.string.dropped_pin))
                    .snippet(createSnippet(latLng))
            )
        }
    }

    private fun GoogleMap.setMapPOIClickListener() {
        setOnPoiClickListener { poi ->
            val poiMarker = addMarker(
                MarkerOptions()
                    .position(poi.latLng)
                    .title(poi.name)
            )

            poiMarker.showInfoWindow()
        }
    }

    private fun GoogleMap.setMapStyle() {
        try {
            val success = setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                    this@MapsActivity,
                    R.raw.map_style
                )
            )

            if(!success)
                Log.e(TAG, getString(R.string.style_parsing_failed))
        } catch (e: Resources.NotFoundException) {
            Log.e(TAG, getString(R.string.style_not_found, e))
        }
    }

    private fun createSnippet(latLng: LatLng) = String.format(
        Locale.getDefault(),
        "Lat: %1$.5f, Long: %2$.5f",
        latLng.latitude,
        latLng.longitude
    )
}