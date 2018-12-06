package aldwin.tablante.com.testmapbox

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.location.Geocoder
import android.location.GnssStatus
import android.os.AsyncTask
import android.os.Bundle
import android.os.StrictMode
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.GeolocationPermissions
import android.widget.ArrayAdapter
import android.widget.Toast
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.mapbox.api.directions.v5.models.DirectionsResponse
import com.mapbox.api.directions.v5.models.DirectionsRoute
import com.mapbox.api.geocoding.v5.GeocodingCriteria
import com.mapbox.api.geocoding.v5.MapboxGeocoding
import com.mapbox.api.geocoding.v5.models.GeocodingResponse
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.annotations.InfoWindow
import com.mapbox.mapboxsdk.annotations.Marker
import com.mapbox.mapboxsdk.annotations.MarkerOptions
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.geometry.LatLngBounds
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import com.mapbox.services.android.navigation.ui.v5.route.NavigationMapRoute
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL
import java.util.*
import kotlin.collections.ArrayList

class FragmentMap : Fragment() {


    var v: View? = null


    private var navigationRoute: NavigationMapRoute? = null
    private var mapboxMap: MapboxMap? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        val mbox: Mapbox

        MapV.onCreate(savedInstanceState)


        val permission = ContextCompat.checkSelfPermission(context!!.applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION)

        if (permission == PackageManager.PERMISSION_GRANTED) {

            MapV.getMapAsync(OnMapReadyCallback { v ->
                MapV.setStyleUrl("mapbox://styles/mapbox/streets-v9")


                imageView.setOnClickListener {

                    getloc(v)
                }
                getloc(v)
            })
        }


    }

    override fun onDestroy() {
        super.onDestroy()
        v!!.MapV.onDestroy()
    }

    override fun onStart() {
        super.onStart()
        v!!.MapV.onStart()
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Mapbox.getInstance(context!!.applicationContext, "pk.eyJ1IjoiYWxkd2lub2szMSIsImEiOiJjam94dTkwMTEyY2xtM3BrdzU3a2NscGIzIn0.btDRypugOuNnqKn-VZ5CiQ");
        val view = inflater!!.inflate(R.layout.activity_main, container, false)
        v = view
        return view
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        v!!.MapV.onSaveInstanceState(outState)

    }

    override fun onLowMemory() {
        super.onLowMemory()
        v!!.MapV.onLowMemory()

    }

    override fun onPause() {
        super.onPause()
        v!!.MapV.onPause()

    }

    fun getloc(MV: MapboxMap) {
        var dlat = (0).toDouble()
        var dlong = (0).toDouble()
        var address = ""
        mapboxMap = MV

        var request = LocationRequest()
        request.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        var client = LocationServices.getFusedLocationProviderClient(context!!.applicationContext)
        val permission = ContextCompat.checkSelfPermission(context!!.applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION)
        if (permission == PackageManager.PERMISSION_GRANTED) {

            client.lastLocation.addOnCompleteListener { v ->
                imageView3.isEnabled = false
                imageView3.visibility = View.INVISIBLE
                MV.removeAnnotations()
                if (v.isSuccessful && v.result != null) {
                    var latn = v.result.latitude
                    var longt = v.result.longitude
                    MV.setCameraPosition(CameraPosition.Builder()
                            .target(LatLng(latn, longt))
                            .zoom(15.00)
                            .build())


                    //getloc(v)
                    button2.setOnClickListener {
                        imageView3.isEnabled = true
                        imageView3.visibility = View.VISIBLE

                        var lat = MV.projection.visibleRegion.latLngBounds.center.latitude
                        var long = MV.projection.visibleRegion.latLngBounds.center.longitude
                        dlat = lat
                        dlong = long
                        navRoute(lat, long, latn, longt, MV)


                    }

                    imageView3.setOnClickListener {
                        fragmentnav(dlat, dlong, latn, longt)

                    }


                    MV.addMarker(MarkerOptions().position(LatLng(latn, longt)))


                    MV.addOnCameraIdleListener {
                        try {
                            var lat = MV.projection.visibleRegion.latLngBounds.center.latitude
                            var long = MV.projection.visibleRegion.latLngBounds.center.longitude
                            button2.isClickable = true
                            button2.alpha = 0.8.toFloat()
                            imageView2.visibility = View.INVISIBLE
                            MV.setCameraPosition(CameraPosition.Builder()
                                    .zoom(15.00)
                                    .build())



                           var g = geoc()
                            g.execute(MapV)
                            //geoCode(lat, long)
                        } catch (e: IllegalStateException) {

                            e.printStackTrace()
                        }
                    }


                    MV.uiSettings.isLogoEnabled = false
                    MV.uiSettings.invalidate()



                    MV.addOnCameraMoveListener(MapboxMap.OnCameraMoveListener {
                        button2.alpha = 0.5.toFloat()
                        button2.isClickable = false
                        if (MV.annotations.size == 2) {
                            MV.annotations.last().remove()
                        }

                        imageView2.visibility = View.VISIBLE
                        var lat = MV.projection.visibleRegion.latLngBounds.center.latitude
                        var long = MV.projection.visibleRegion.latLngBounds.center.longitude


                        MV.addMarker(MarkerOptions().position(LatLng(lat, long)))


                    })





                } else {
                    getloc(MV)
                }
            }


        }
    }

    fun navRoute(lat: Double, long: Double, latn: Double, longn: Double, MV: MapboxMap) {
        val orig = Point.fromLngLat(longn, latn)
        val dest = Point.fromLngLat(long, lat)

        NavigationRoute.builder(context)
                .accessToken(Mapbox.getAccessToken()!!)
                .origin(orig)
                .destination(dest)
                .build()
                .getRoute(object : retrofit2.Callback<DirectionsResponse> {
                    override fun onResponse(call: Call<DirectionsResponse>, response: Response<DirectionsResponse>) {

                        if (response.body() == null) {
                            Toast.makeText(context!!.applicationContext, "No Routes Available", Toast.LENGTH_LONG).show()
                            return
                        } else if (response.body()!!.routes().isEmpty()) {
                            Toast.makeText(context!!.applicationContext, "No Routes Available", Toast.LENGTH_LONG).show()
                            return

                        }


                        var currentRoute = response.body()!!.routes().get(0)




                        if (navigationRoute != null) {
                            navigationRoute!!.removeRoute()
                        } else {

                            navigationRoute = NavigationMapRoute(null, MapV, MV)
                        }
                        navigationRoute!!.addRoute(currentRoute)
                    }

                    override fun onFailure(call: Call<DirectionsResponse>, t: Throwable) {
                        Toast.makeText(context, "Error", Toast.LENGTH_LONG).show()
                    }
                })

    }

    fun fragmentnav(lat: Double, long: Double, latn: Double, longn: Double) {

        val transaction = fragmentManager!!.beginTransaction()
        var bundle = Bundle()

        bundle.putDouble("lat", lat)
        bundle.putDouble("long", long)
        bundle.putDouble("latn", latn)
        bundle.putDouble("longn", longn)

        val fragment = FragmentNavigation()
        fragment.arguments = bundle
        transaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
        transaction.replace(R.id.frag, fragment, "tagname")
        transaction.addToBackStack("tagname")
        transaction.commit()


    }

    inner class geoc : AsyncTask<MapView, LatLng, Void>() {
        override fun doInBackground(vararg params: MapView?): Void? {
        publishProgress()
            return null
        }

        override fun onProgressUpdate(vararg values: LatLng?) {

            //  var geocoder = Geocoder(context!!.applicationContext, Locale.getDefault())
            var lat = mapboxMap!!.projection.visibleRegion.latLngBounds.center.latitude
            var long = mapboxMap!!.projection.visibleRegion.latLngBounds.center.longitude

            try {

                geoCode(lat, long)
            } catch (e: IndexOutOfBoundsException) {
                e.printStackTrace()

            }
            super.onProgressUpdate(*values)
        }

        override fun onPostExecute(result: Void?) {
            super.onPostExecute(result)
           // imageView2.visibility = View.INVISIBLE
        }
    }


    fun geoCode(lat: Double, long: Double) {
       /* val searchLocationPoint = Point.fromLngLat(long, lat)
        var x = MapboxGeocoding.builder()
                .accessToken("pk.eyJ1IjoiYWxkd2lub2szMSIsImEiOiJjam94dTkwMTEyY2xtM3BrdzU3a2NscGIzIn0.btDRypugOuNnqKn-VZ5CiQ")
                .autocomplete(true)
                .query(searchLocationPoint)
                .geocodingTypes(GeocodingCriteria.TYPE_POI)
                .mode(GeocodingCriteria.MODE_PLACES)
                .country("Philippines")
                .build()
*/




        var geoString = "https://api.mapbox.com/geocoding/v5/mapbox.places/Philippines.json?proximity=" +
               long.toString() + "," + lat.toString() + "&access_token=" +
                "pk.eyJ1IjoiYWxkd2lub2szMSIsImEiOiJjam94dTkwMTEyY2xtM3BrdzU3a2NscGIzIn0.btDRypugOuNnqKn-VZ5CiQ"

        try {
            var JO = JSONObject(StringUrl(long, lat)).getJSONArray("features")
            var count = 0
            val listItems = ArrayList<String>()


             while(count <5){
                 try {
                     try {
                         var JA = JO.getJSONObject(count).getString("place_name")
                         listItems.add(JA)
                     } catch (e: KotlinNullPointerException) {
                         e.printStackTrace()
                     }
                 }
                 catch (i:JSONException){
                     i.printStackTrace()
                 }
                 count++
             }

                  listv.adapter = ArrayAdapter<String>(activity,android.R.layout.simple_list_item_1,listItems)





        }
        catch (e:KotlinNullPointerException){
            e.printStackTrace()


        }







    }


    fun StringUrl ( long:Double,lat:Double): String {
        var url = URL("https://api.mapbox.com/geocoding/v5/mapbox.places/Cebu.json?proximity=" +
                long.toString() + "," + lat.toString() + "&access_token=" +
                "pk.eyJ1IjoiYWxkd2lub2szMSIsImEiOiJjam94dTkwMTEyY2xtM3BrdzU3a2NscGIzIn0.btDRypugOuNnqKn-VZ5CiQ")
        var httpURLConnection = url.openConnection()
        var inputStream = httpURLConnection.getInputStream()
        val bufferedReader = BufferedReader(InputStreamReader(inputStream))
        var line: String? = ""
        var data :String =""
        while (line != null) {
            line = bufferedReader.readLine()
            data += line
        }
        return  data
    }
}


