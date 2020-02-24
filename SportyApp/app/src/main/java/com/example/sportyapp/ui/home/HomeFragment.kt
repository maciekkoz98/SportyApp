package com.example.sportyapp.ui.home

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar

class HomeFragment : Fragment(), OnMapReadyCallback {

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var localFAB: FloatingActionButton

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
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity!!)
        return root
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        //TODO set markers in the fields locations!
        val warsaw = LatLng(52.2297, 21.0122)
        mMap.addMarker(MarkerOptions().position(warsaw).title("Marker in Warsaw"))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(warsaw, 13F))
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
            mMap.uiSettings.isMyLocationButtonEnabled = false;
        }
    }
}