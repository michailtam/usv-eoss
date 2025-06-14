package com.example.usveoss.misc

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import android.graphics.Color
import com.example.usveoss.MainActivity
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

    fun getLastPoint(): LatLng? {
        return polylinePoints.lastOrNull()
    }

    fun addSegment(from: LatLng, to: LatLng) {
        map?.let {
            val segment = it.addPolyline(
                PolylineOptions()
                    .add(from)
                    .add(to)
                    .color(Color.YELLOW)
                    .width(8f)
            )
            polylines.add(segment)
            polylinePoints.add(to) // only store the latest
            drawCornerMarker(to)
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

    // Remove the last segment and marker
    fun removeLastSegment() {
        if (polylines.isNotEmpty()) {
            // Remove last polyline from the map and list
            val lastPolyline = polylines.removeAt(polylines.size - 1)
            lastPolyline.remove()

            // Remove last corner marker from map and list
            if (cornerMarkers.isNotEmpty()) {
                val lastMarker = cornerMarkers.removeAt(cornerMarkers.size - 1)
                lastMarker.remove()
            }

            // Remove the last polyline point
            if (polylinePoints.isNotEmpty()) {
                polylinePoints.removeAt(polylinePoints.size - 1)
            }
        }
    }

    // Optionally clear all drawn polylines
    fun clearAllPaths() {
        for (polyline in polylines) {
            polyline.remove()
        }
        for (marker in cornerMarkers) {
            marker.remove()
        }
        polylines.clear()
        cornerMarkers.clear()
    }
}