package aldwin.tablante.com.testmapbox

import android.app.AlertDialog
import android.content.DialogInterface
import android.location.Location
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.support.design.widget.BottomSheetBehavior
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.mapbox.api.directions.v5.models.DirectionsResponse
import com.mapbox.api.directions.v5.models.DirectionsRoute
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute
import kotlinx.android.synthetic.main.navigation_res.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.view.Menu
import com.mapbox.api.directions.v5.models.BannerInstructions
import com.mapbox.mapboxsdk.maps.MapboxMapOptions
import com.mapbox.services.android.navigation.ui.v5.*
import com.mapbox.services.android.navigation.ui.v5.camera.NavigationCamera
import com.mapbox.services.android.navigation.ui.v5.listeners.*
import com.mapbox.services.android.navigation.v5.navigation.metrics.NavigationMetricListener
import com.mapbox.services.android.navigation.v5.routeprogress.RouteProgress



class FragmentNavigation : Fragment(), OnNavigationReadyCallback, NavigationListener, NavigationMetricListener,BannerInstructionsListener
{

    var v: View? = null
    private var directionsRoute: DirectionsRoute? = null

    override fun willDisplay(instructions: BannerInstructions?): BannerInstructions? {
       return instructions
    }
    override fun onArrival(routeProgress: RouteProgress?) {

        var alertDialog = AlertDialog.Builder(this@FragmentNavigation.context)
        alertDialog.setMessage("You have reached your destination")
        alertDialog.setTitle("MapBox")
        alertDialog.setIcon(R.drawable.mapbox_logo_icon)
        alertDialog.setPositiveButton("Ok", DialogInterface.OnClickListener { dialog, which ->
        })
        alertDialog.create()
        alertDialog.show()
    }

    override fun onOffRouteEvent(offRouteLocation: Location?) {
//
    }

    override fun onRouteProgressUpdate(routeProgress: RouteProgress?) {
        Toast.makeText(context, "Moved", Toast.LENGTH_LONG).show()

//
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Mapbox.getInstance(context!!, "pk.eyJ1IjoiYWxkd2lub2szMSIsImEiOiJjam94dTkwMTEyY2xtM3BrdzU3a2NscGIzIn0.btDRypugOuNnqKn-VZ5CiQ")
        var view = inflater.inflate(R.layout.navigation_res, container, false)

        v = view
        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        v!!.navView.onCreate(savedInstanceState);
        v!!.navView.initialize(this);
    }

    override fun onCancelNavigation() {
        v!!.navView.stopNavigation();
        stopNavigation();
        var alertDialog = AlertDialog.Builder(this@FragmentNavigation.context)
        alertDialog.setMessage("You have cancelled your navigation.")
        alertDialog.setTitle("Confirmation")
        alertDialog.setIcon(R.drawable.mapbox_logo_icon)
        alertDialog.setPositiveButton("Continue viewing", DialogInterface.OnClickListener { dialog, which ->
        })
        alertDialog.setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, which ->
            openFragmentMapBox()

            null
        })

        alertDialog.create()
        alertDialog.show()


    }

    override fun onNavigationFinished() {
//



    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)

        if (savedInstanceState != null) {
            v!!.navView.onRestoreInstanceState(savedInstanceState);
        }
    }

    override fun onNavigationReady(isRunning: Boolean) {

        val destination = Point.fromLngLat(arguments!!.getDouble("long"), arguments!!.getDouble("lat"))
        val origin = Point.fromLngLat(arguments!!.getDouble("longn"), arguments!!.getDouble("latn"))
        fetchRoute(origin, destination)
    }

    override fun onNavigationRunning() {

    }

    private fun fetchRoute(origin: Point, destination: Point) {

        v!!.navView.retrieveAlertView().setOnClickListener { Toast.makeText(context, "Clicked1", Toast.LENGTH_LONG).show() }


        NavigationRoute.builder(context)
                .accessToken("pk.eyJ1IjoiYWxkd2lub2szMSIsImEiOiJjam94dTkwMTEyY2xtM3BrdzU3a2NscGIzIn0.btDRypugOuNnqKn-VZ5CiQ")
                .origin(origin)
                .destination(destination)
                .build()
                .getRoute(object : Callback<DirectionsResponse> {

                    override fun onResponse(call: Call<DirectionsResponse>?, response: Response<DirectionsResponse>?) {

                        directionsRoute = response!!.body()!!.routes().get(0)
                        startNavigation()

                    }

                    override fun onFailure(call: Call<DirectionsResponse>?, t: Throwable?) {
                        Toast.makeText(context!!.applicationContext, "Error 101", Toast.LENGTH_LONG).show()
                    }
                })

    }


    override fun onDestroy() {
        super.onDestroy()
        v!!.navView.onDestroy()

    }

    override fun onPause() {
        super.onPause()
        v!!.navView.onPause()

    }

    override fun onStart() {
        super.onStart()
        v!!.navView.onStart()

    }

    override fun onStop() {
        super.onStop()
        v!!.navView.onStop()

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        v!!.navView.onSaveInstanceState(outState)

    }

    override fun onResume() {
        super.onResume()
        v!!.navView.onResume()

    }

    override fun onLowMemory() {
        super.onLowMemory()
        v!!.navView.onLowMemory()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        v!!.navView.onDestroy()

    }

    override fun onOptionsMenuClosed(menu: Menu?) {
        super.onOptionsMenuClosed(menu)
        openFragmentMapBox()

    }

    private fun startNavigation() {
        if (directionsRoute == null) {
            return
        }
        try {



            val options = NavigationViewOptions.builder()

                    .directionsRoute(directionsRoute)
                    .shouldSimulateRoute(true)
                    .navigationListener(this@FragmentNavigation)
                    .waynameChipEnabled(true)
                     .build()




            v!!.navView.startNavigation(options)
        } catch (e: IllegalArgumentException) {

            e.printStackTrace()

            Toast.makeText(context, "No Destination", Toast.LENGTH_LONG).show()
            openFragmentMapBox()
        }
    }

    private fun stopNavigation() {
        val activity = activity
        if (activity != null && activity is FragmentViewer) {
            val fragmentNavigationActivity = activity as FragmentViewer

        }
    }


    fun openFragmentMapBox() {
        val transaction = fragmentManager!!.beginTransaction()
        val fragment = FragmentMap()
        transaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
        transaction.replace(R.id.frag, fragment, "tagname")
        transaction.addToBackStack("tagname")
        transaction.commit()


    }



}