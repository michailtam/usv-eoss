import com.example.usveoss.MainActivity
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds

class CameraAndViewport(private val zoomLevel: Float) {
    private val mLlatLon by lazy { MainActivity() }

    // Tilt the camera to achieve a 3D effect for better viewing the region
    val santoriniThira : CameraPosition = CameraPosition.Builder()
        .target(mLlatLon.getLatLon())
        .zoom(zoomLevel)
        .bearing(25f)
        .tilt(45f)
        .build()

    // Set the boundary to Santorini
    val santoriniBounds = LatLngBounds(
        LatLng(36.327351, 25.347488), // South-West
        LatLng(36.483749, 25.484117)  // North-East
    )
}