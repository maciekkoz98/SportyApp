package com.example.sportyapp.ui.home

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.location.Location
import android.os.Bundle
import android.text.method.KeyListener
import android.text.method.MovementMethod
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.example.sportyapp.R
import com.example.sportyapp.REQUEST_LOCATION
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.maps.android.clustering.ClusterManager

class HomeFragment : Fragment(), OnMapReadyCallback {

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var localFAB: FloatingActionButton
    private lateinit var clusterManager: ClusterManager<MapMarker>
    private lateinit var editTextKeyListener: KeyListener
    private lateinit var editTextMovementMethod: MovementMethod

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
            ViewModelProviders.of(this).get(HomeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
//        val textView: TextView = root.findViewById(R.id.text_home)
//        homeViewModel.text.observe(this, Observer {
//            textView.text = it
//        })
        mapFragment.getMapAsync(this)
        localFAB = root.findViewById(R.id.localFAB)
        localFAB.setOnClickListener {
            localize()
        }
        val addFAB: FloatingActionButton = root.findViewById(R.id.addFAB)
        addFAB.setOnClickListener { view ->
            Snackbar.make(view, "Replace with ADD action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        val editText: TextView = root.findViewById(R.id.search_edit_text)
        editTextKeyListener = editText.keyListener
        editText.keyListener = null
        editTextMovementMethod = editText.movementMethod
        editText.movementMethod = null

        val bottomSheet: View = root.findViewById(R.id.bottom_sheet_search)
        val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        val searchBottomSheetLayout: LinearLayout = root.findViewById(R.id.linear_layout_search)
        searchBottomSheetLayout.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
        val bottomSheetCallback = object : BottomSheetBehavior.BottomSheetCallback() {

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    editText.movementMethod = editTextMovementMethod
                    editText.keyListener = editTextKeyListener
                    editText.requestFocus()
                    val imm: InputMethodManager =
                        activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
                } else {
                    editText.movementMethod = null
                    editText.keyListener = null
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                // Do something for slide offset
            }
        }
        bottomSheetBehavior.addBottomSheetCallback(bottomSheetCallback)
        bottomSheet.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                val imm =
                    activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(editText.windowToken, 0)
            }
            true

        }
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity!!)

        return root
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        //TODO set markers in the fields locations!
        val warsaw = LatLng(52.2297, 21.0122)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(warsaw, 13F))
        clusterManager = ClusterManager<MapMarker>(activity, mMap)
        mMap.setOnCameraIdleListener(clusterManager)
        mMap.setOnMarkerClickListener(clusterManager)
        addMarkers_TEMP()
    }

    fun addMarkers_TEMP() {
        var latitude = 52.2297
        var longitude = 21.0122
        for (i in 0 until 250) {
            val offset = 0.0005f
            latitude += offset
            longitude += offset
            val marker = MapMarker(latitude, longitude, i.toString(), "Jestem $i")
            clusterManager.addItem(marker)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_LOCATION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    localize()
                } else {
                    val noWriteMes = getString(R.string.no_local_permission)
                    Toast.makeText(
                        activity,
                        noWriteMes,
                        Toast.LENGTH_LONG
                    ).show()
                }
                return
            }
            else -> {

            }
        }
    }

    private fun localize() {
        if (ContextCompat.checkSelfPermission(
                context!!,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_LOCATION
            )
        } else {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    val img: Drawable?
                    if (location == null) {
                        img = ResourcesCompat.getDrawable(
                            context!!.resources,
                            R.drawable.ic_location_searching_white_24dp,
                            null
                        )
                    } else {
                        img = ResourcesCompat.getDrawable(
                            context!!.resources,
                            R.drawable.ic_my_location_white_24dp,
                            null
                        )

                        val longitude = location.longitude
                        val latitude = location.latitude
                        val latLng = LatLng(latitude, longitude)
                        val cameraPosition =
                            CameraPosition.builder()
                                .target(latLng)
                                .zoom(mMap.cameraPosition.zoom)
                                .build()
                        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
                    }
                    localFAB.setImageDrawable(img)
                }
            mMap.isMyLocationEnabled = true
            mMap.uiSettings.isMyLocationButtonEnabled = false
        }
    }
}