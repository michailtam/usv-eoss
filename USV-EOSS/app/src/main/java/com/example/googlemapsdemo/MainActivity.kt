package com.example.googlemapsdemo

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions


class MainActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var map:  GoogleMap
    private var mZoomLevel = 16f // 16 times zoom

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main) // Load the layout defined in activity_main.xml
        setSupportActionBar(findViewById(R.id.toolbar))

        var mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment

        mapFragment.getMapAsync(this) // initializes the map asynchronously
    }

    // Create the Options Menu
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.map_types_menu, menu)
        return true
    }

    // Handle click-list for the menu items
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.normal_map -> {
                map.mapType = GoogleMap.MAP_TYPE_NORMAL
            }
            R.id.hybrid_map -> {
                map.mapType = GoogleMap.MAP_TYPE_HYBRID
            }
            R.id.satelite_map -> {
                map.mapType = GoogleMap.MAP_TYPE_SATELLITE
            }
            R.id.terrain_map -> {
                map.mapType = GoogleMap.MAP_TYPE_TERRAIN
            }
            R.id.none_map -> {
                map.mapType = GoogleMap.MAP_TYPE_NONE
            }
        }
        return true
    }

    //  Receive the map once it's ready
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        // TODO: Find the latitude pos 50m away to the sea
        var santorini_thira = LatLng(36.41499042172217, 25.4229644592048173)
        //santorini_thira = SphericalUtil.computeOffset(santorini_thira, 0.0, 1000.0)

        map.addMarker(MarkerOptions().position(santorini_thira).title("Περιβαλλοντική Έρευνα Θήρα"))
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(santorini_thira, mZoomLevel))
        map.uiSettings.apply {
            isZoomControlsEnabled = true
            isCompassEnabled = true
        }
        //map.setPadding(0,0,800, 0)

        // Add UI-Controls

    }
}