package com.example.usveoss

import CameraAndViewport
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.usveoss.misc.TypeAndStyle
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity(), OnMapReadyCallback, OnMarkerDragListener {
    private lateinit var map:  GoogleMap
    private var mZoomLevelMin = 13f
    private var mZoomLevelMax = 19f
    private val mTypeAndStyle by lazy { TypeAndStyle() }
    private val mCameraAndViewport by lazy { CameraAndViewport(10f) } // Start zoom level

    // Lat/Lon for thira in Santorini
    private var latitude = 36.415416
    private var longitude = 25.426502
    private val usvLatLong = LatLng(latitude, longitude)

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
        mTypeAndStyle.setMapType(item, map)
        return true
    }

    //  Receive the map once it's ready
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        // TODO: Find the latitude pos 50m away to the sea
        //santorini_thira = SphericalUtil.computeOffset(santorini_thira, 0.0, 1000.0)
        // Add the USV marker
        val usvIcon = fromPngToBitmap(R.drawable.the_otter_usv)
        map.addMarker(
            MarkerOptions()
                .position(getLatLon())
                .title("USV")
                .draggable(true)
                .icon(usvIcon)
        )
        map.moveCamera(CameraUpdateFactory.newCameraPosition(mCameraAndViewport.santoriniThira))
        map.uiSettings.apply {
            isZoomControlsEnabled = true
            isCompassEnabled = true
        }
        // Set min-max zoom
        map.setMinZoomPreference(mZoomLevelMin)
        map.setMaxZoomPreference(mZoomLevelMax)

        // Execute the content after the map was loaded
        lifecycleScope.launch {
            delay(1000L)
            map.setLatLngBoundsForCameraTarget(mCameraAndViewport.santoriniBounds)

            val newCameraZoom : CameraPosition = CameraPosition.Builder()
                .target(usvLatLong)
                .zoom(17f)
                .bearing(25f)
                .tilt(45f)
                .build()

            map.animateCamera(
                CameraUpdateFactory.newCameraPosition(newCameraZoom),
                2000,
                null
            )

            delay(3000L)
        }

        // Add Listeners for Mouse clicks
        onMapClicked()

        // Add UI-Controls
        map.setOnMarkerDragListener(this)
    }

    // Get LatLon coordinates
    fun getLatLon(): LatLng {
        return usvLatLong
    }

    private fun onMapClicked() {
        map.setOnMapClickListener {
            Toast.makeText(this, "Συντεταγμένες\n ${it.latitude}, ${it.longitude}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onMarkerDrag(p0: Marker) {
    }

    override fun onMarkerDragEnd(p0: Marker) {
        Toast.makeText(this, "Συντεταγμένες\n ${p0.position.latitude}, ${p0.position.longitude}", Toast.LENGTH_SHORT).show()
    }

    override fun onMarkerDragStart(p0: Marker) {
    }

    // Convert a vector image to a bitmap one
    private fun fromPngToBitmap(id: Int, scale: Float = 0.2f): BitmapDescriptor {
        val bitmap = BitmapFactory.decodeResource(resources, id)

        val width = (bitmap.width * scale).toInt()
        val height = (bitmap.height * scale).toInt()

        val scaled = Bitmap.createScaledBitmap(bitmap, width, height, true)
        return BitmapDescriptorFactory.fromBitmap(scaled)
    }
}