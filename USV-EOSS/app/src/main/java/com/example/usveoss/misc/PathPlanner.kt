package com.example.usveoss.misc

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Rect
import android.util.Log
import com.example.usveoss.R
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*
import com.google.maps.android.SphericalUtil

class PathPlanner(private val context: Context) {
    private val polylinePoints = mutableListOf<LatLng>()
    private val polylines = mutableListOf<Polyline>()
    private val cornerMarkers = mutableListOf<Marker>() // For visualizing corners
    private val distanceLabels = mutableListOf<Pair<LatLng, String>>() // midpoint + distance string
    private val labelOverlays = mutableListOf<GroundOverlay>() // actual overlays for distance labels
    private var usvLatLong: LatLng? = null
    private var map: GoogleMap? = null

    // Getter and Setter functions
    fun setMap(map: GoogleMap) {
        this.map = map
    }
    fun getLastPoint(): LatLng? = polylinePoints.lastOrNull()
    fun setUSVLatLong(usvpos: LatLng) { usvLatLong = usvpos }
    fun getPolylinePointSize() = polylinePoints.size

    // Add a new segment to the path
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
            if (polylinePoints.isEmpty()) {
                polylinePoints.add(from)
            }
            polylinePoints.add(to) // only store the latest
            drawCornerMarker(to)
            addDistance(from, to)
        }
    }

    // Add the distance in the middle of the line segment
    fun addDistance(from: LatLng, to: LatLng) {
        map?.let {
            val distance = SphericalUtil.computeDistanceBetween(from, to)
            val distanceText = String.format("%.1f m", distance)
            val midpoint = LatLng(
                (from.latitude + to.latitude) / 2,
                (from.longitude + to.longitude) / 2
            )

            // Store label info
            distanceLabels.add(midpoint to distanceText)

            // Draw the label and keep track of the overlay
            drawLabelOverlay(midpoint, distanceText)
        }
    }

    // Draw the overlay for the labels (necessary when want to track text)
    private fun drawLabelOverlay(position: LatLng, text: String) {
        val bitmap = createTextBitmap(text)
        val overlay = map?.addGroundOverlay(
            GroundOverlayOptions()
                .image(BitmapDescriptorFactory.fromBitmap(bitmap))
                .position(position, 100f)
                .transparency(0f)
        )
        overlay?.let { labelOverlays.add(it) }
    }

    // Create the bitmap containing the distance
    private fun createTextBitmap(text: String): Bitmap {
        val scaleFactor = 2f  // Render at 2x resolution for sharpness
        val paint = android.graphics.Paint().apply {
            textSize = 40f * scaleFactor  // Use large size for crisp output
            color = Color.BLACK
            textAlign = android.graphics.Paint.Align.LEFT
            isAntiAlias = true
            isSubpixelText = true
            isDither = true
        }

        val bounds = Rect()
        paint.getTextBounds(text, 0, text.length, bounds)
        val bmp = Bitmap.createBitmap(
            (bounds.width() + 20).toInt(),
            (bounds.height() + 20).toInt(),
            Bitmap.Config.ARGB_8888
        )

        val canvas = android.graphics.Canvas(bmp)
        canvas.scale(1f / scaleFactor, 1f / scaleFactor)  // Downscale to high DPI
        canvas.drawText(text, 10f * scaleFactor, bounds.height().toFloat() * scaleFactor, paint)
        return bmp
    }

    // Draw the corner markers
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
            marker?.showInfoWindow()
        }
    }

    // Scale the marker
    private fun getScaledMarker(context: Context, resourceId: Int, scale: Float = 0.3f): BitmapDescriptor {
        val original = BitmapFactory.decodeResource(context.resources, resourceId)
        val width = (original.width * scale).toInt()
        val height = (original.height * scale).toInt()
        val scaledBitmap = Bitmap.createScaledBitmap(original, width, height, true)
        return BitmapDescriptorFactory.fromBitmap(scaledBitmap)
    }

    // Delete only the last line segment
    fun removeLastSegment() {
        if (polylines.isNotEmpty()) {
            val lastPolyline = polylines.removeAt(polylines.size - 1)
            lastPolyline.remove()

            if (cornerMarkers.isNotEmpty()) {
                val lastMarker = cornerMarkers.removeAt(cornerMarkers.size - 1)
                lastMarker.remove()
            }

            if (polylinePoints.isNotEmpty()) {
                polylinePoints.removeAt(polylinePoints.size - 1)
            }

            if (labelOverlays.isNotEmpty()) {
                val lastOverlay = labelOverlays.removeAt(labelOverlays.size - 1)
                lastOverlay.remove()
            }

            if (distanceLabels.isNotEmpty()) {
                distanceLabels.removeAt(distanceLabels.size - 1)
            }
        }
    }

    fun getTotalDistance(): Double {
        var total = 0.0
        for (i in 1 until polylinePoints.size) {
            total += SphericalUtil.computeDistanceBetween(polylinePoints[i - 1], polylinePoints[i])
        }
        return total
    }

    // Clear the overall path
    fun clearAllPaths() {
        for (polyline in polylines) {
            polyline.remove()
        }
        for (marker in cornerMarkers) {
            marker.remove()
        }
        for (overlay in labelOverlays) {
            overlay.remove()
        }
        polylines.clear()
        polylinePoints.clear()
        cornerMarkers.clear()
        labelOverlays.clear()
        distanceLabels.clear()
    }
}
