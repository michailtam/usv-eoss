package com.example.usveoss.misc

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import android.graphics.Color
import com.example.usveoss.R
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions


class PathPlanner(private val context: Context) {
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
                    .width(8f)
            )
            polylines.add(polyline)
            polylinePoints.clear()
        }
    }

    // Draw markers at line endings
    private fun drawCornerMarker(point: LatLng) {
        map?.let { mapInstance ->
            val markerIcon = getScaledMarker(context, R.drawable.blue_marker, scale = 0.003f)
            val marker = this.map?.addMarker(
                MarkerOptions()
                    .position(point)
                    .icon(markerIcon)
                    .anchor(0.5f, 1.0f)
                    .title("${point}")
            )
            marker?.let { cornerMarkers.add(it) }
        }
    }

    // Rescale (i.e. shrink) the marker
    private fun getScaledMarker(context: Context, resourceId: Int, scale: Float=0.3f): BitmapDescriptor {
        val original = BitmapFactory.decodeResource(context.resources, resourceId)
        val width = (original.width * scale).toInt()
        val height = (original.height * scale).toInt()
        val scaledBitmap = Bitmap.createScaledBitmap(original, width, height, true)
        return BitmapDescriptorFactory.fromBitmap(scaledBitmap)
    }

    // Optionally clear all drawn polylines
    fun clearAllPaths() {
        for (polyline in polylines) {
            polyline.remove()
        }
        polylines.clear()
    }
}