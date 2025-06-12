package com.example.usveoss.misc

import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import android.graphics.Color
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions


class PathPlanner {
    private val polylinePoints = mutableListOf<LatLng>()
    private val polylines = mutableListOf<Polyline>()
    private val cornerMarkers = mutableListOf<Marker>() // For visualizing corners
    private var map: GoogleMap? = null

    fun setMap(map: GoogleMap) {
        this.map = map
    }

    // Add a point to the list
    fun addPoint(point: LatLng) {
        polylinePoints.add(point)
        drawCornerMarker(point)
    }

    // Draw polyline from collected points
    fun drawPath() {
        if (polylinePoints.size >= 2 && map != null) {
            val polyline = map!!.addPolyline(
                PolylineOptions()
                    .addAll(polylinePoints)
                    .color(Color.YELLOW)
                    .width(3f)
            )
            polylines.add(polyline)
            polylinePoints.clear()
        }
    }

    private fun drawCornerMarker(point: LatLng) {
        //TODO: Scale the markers
        val marker = this.map?.addMarker(
            MarkerOptions()
                .position(point)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                .anchor(0.5f, 0.5f)
                .title("Corner")
        )
        marker?.let { cornerMarkers.add(it) }
    }

    // Optionally clear all drawn polylines
    fun clearAllPaths() {
        for (polyline in polylines) {
            polyline.remove()
        }
        polylines.clear()
    }
}