import com.example.usveoss.MainActivity
import com.google.android.gms.maps.model.CameraPosition

class CameraAndViewport {
    private val latlon by lazy { MainActivity() }
    val santorini_thira : CameraPosition = CameraPosition.Builder()
        .target(latlon.getLatLon()).build()
}