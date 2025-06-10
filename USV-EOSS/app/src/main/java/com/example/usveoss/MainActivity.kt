package com.example.usveoss

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.usveoss.misc.TypeAndStyle
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions


class MainActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var map:  GoogleMap
    private var mZoomLevel = 16f // 16 times zoom
    private val typeAndStyle by lazy { TypeAndStyle() }

    // Lat/Lon for thira in Santorini
    private var latitude = 36.41499042172217
    private var longitude = 25.4229644592048173
    private val santorini_thira = LatLng(latitude, longitude)

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
        typeAndStyle.setMapType(item, map)
        return true
    }

    //  Receive the map once it's ready
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        // TODO: Find the latitude pos 50m away to the sea
        //santorini_thira = SphericalUtil.computeOffset(santorini_thira, 0.0, 1000.0)

        map.addMarker(MarkerOptions().position(getLatLon()).title("Περιβαλλοντική Έρευνα Θήρα"))
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(santorini_thira, mZoomLevel))
        map.uiSettings.apply {
            isZoomControlsEnabled = true
            isCompassEnabled = true
        }
        //map.setPadding(0,0,800, 0)

        // Add UI-Controls

    }

    fun getLatLon(): LatLng {
        return santorini_thira
    }
}