package aldwin.tablante.com.testmapbox

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.view.DragEvent
import android.widget.Toast
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.mapbox.android.core.location.LocationEngine
import com.mapbox.android.core.location.LocationEngineListener
import com.mapbox.android.core.location.LocationEnginePriority
import com.mapbox.android.core.location.LocationEngineProvider
import com.mapbox.mapboxsdk.Mapbox
import kotlinx.android.synthetic.main.activity_main.*
import com.mapbox.mapboxsdk.Mapbox.getApplicationContext
import com.mapbox.mapboxsdk.annotations.Marker
import com.mapbox.mapboxsdk.annotations.MarkerOptions
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.location.LocationComponent
import com.mapbox.mapboxsdk.location.modes.RenderMode
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.core.MapboxService
import com.mapbox.core.utils.MapboxUtils
import com.mapbox.mapboxsdk.location.modes.CameraMode
import com.mapbox.mapboxsdk.maps.MapboxMapOptions
import java.io.IOException
import java.util.*


class MainActivity : AppCompatActivity() {
    private var li: MutableList<Address>? = null
    private var mapboxMap: MapboxMap? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val mbox: Mapbox
        Mapbox.getInstance(this, "pk.eyJ1IjoiYWxkd2lub2szMSIsImEiOiJjam94dTkwMTEyY2xtM3BrdzU3a2NscGIzIn0.btDRypugOuNnqKn-VZ5CiQ");
        setContentView(R.layout.activity_main)
        MapV.onCreate(savedInstanceState)


        val permission = ContextCompat.checkSelfPermission(applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION)

        if (permission == PackageManager.PERMISSION_GRANTED) {

            MapV.getMapAsync(OnMapReadyCallback { v  ->

                var g = geoc()
                g.execute(MapV)
                imageView.setOnClickListener {

                    getloc(v)
                }
                getloc(v)
            })
        }


    }

    public override fun onResume() {
        super.onResume()
        MapV.onResume()
    }

    override fun onStart() {
        super.onStart()
        MapV.onStart()
    }

    override fun onStop() {
        super.onStop()
        MapV.onStop()
    }

    public override fun onPause() {
        super.onPause()
        MapV.onPause()
    }

    public override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        MapV.onSaveInstanceState(outState)
    }

    override fun onLowMemory() {
        super.onLowMemory()
        MapV.onLowMemory()
    }

    public override fun onDestroy() {
        super.onDestroy()
        MapV.onDestroy()
    }


    fun init() {
        var geo: Geocoder = Geocoder(this, Locale.getDefault())
        // li = geo.getFromLocation()

        try {

        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    fun getloc(MV: MapboxMap) {
        mapboxMap = MV
        var address = ""

        var request = LocationRequest()
        request.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        var client = LocationServices.getFusedLocationProviderClient(applicationContext)
        val permission = ContextCompat.checkSelfPermission(applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION)
        if (permission == PackageManager.PERMISSION_GRANTED) {

            client.lastLocation.addOnCompleteListener { v ->

                MV.removeAnnotations()
                var latn = v.result.latitude
                var longt = v.result.longitude
                MV.setCameraPosition(CameraPosition.Builder()
                        .target(LatLng(latn, longt))
                        .zoom(12.00)
                        .build())


                //getloc(v)
                MV.addMarker(MarkerOptions().position(LatLng(latn, longt)))






                MV.addOnCameraMoveListener(MapboxMap.OnCameraMoveListener {
                    MV.removeAnnotations()
                    var lat = MV.projection.visibleRegion.latLngBounds.center.latitude
                    var long = MV.projection.visibleRegion.latLngBounds.center.longitude

                    val latLng = LatLng(lat, long)

                    MV.addMarker(MarkerOptions().position(LatLng(lat, long)))


                })

            }


        }

    }


    inner class geoc : AsyncTask<MapView, LatLng, Void>() {
        override fun doInBackground(vararg params: MapView?): Void? {
            params[0]!!.addOnCameraDidChangeListener {
                publishProgress()


            }

            return null
        }

        override fun onProgressUpdate(vararg values: LatLng?) {

            var geocoder = Geocoder(applicationContext, Locale.getDefault())
            var lat = mapboxMap!!.projection.visibleRegion.latLngBounds.center.latitude
            var long = mapboxMap!!.projection.visibleRegion.latLngBounds.center.longitude

            try {
                var string = geocoder.getFromLocation(lat, long, 1).get(0).featureName + ", " +
                        geocoder.getFromLocation(lat, long, 1).get(0).locality

            } catch (e: IndexOutOfBoundsException) {
                e.printStackTrace()

            }
            super.onProgressUpdate(*values)
        }
    }

}
