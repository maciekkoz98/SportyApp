package com.example.sportyapp.ui.home

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.location.Location
import android.os.Bundle
import android.text.method.KeyListener
import android.text.method.MovementMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sportyapp.R
import com.example.sportyapp.REQUEST_LOCATION
import com.example.sportyapp.data.field.Field
import com.example.sportyapp.data.game.Game
import com.example.sportyapp.ui.home.utils.GamesInChosenFieldAdapter
import com.example.sportyapp.utils.AuthenticationInterceptor
import com.example.sportyapp.utils.SportPrefs
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.maps.android.clustering.Cluster
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.ClusterManager.OnClusterItemClickListener
import okhttp3.*
import org.json.JSONArray
import java.io.IOException

class HomeFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMapClickListener,
    ClusterManager.OnClusterClickListener<MapMarker>, OnClusterItemClickListener<MapMarker> {

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var mMap: GoogleMap
    private var mapReady: Boolean = false
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var localFAB: FloatingActionButton
    private lateinit var clusterManager: ClusterManager<MapMarker>
    private lateinit var bottomSheetSearch: View
    private lateinit var bottomSheetSearchBehavior: BottomSheetBehavior<View>
    private lateinit var bottomSheetField: View
    private lateinit var bottomSheetFieldBehavior: BottomSheetBehavior<View>
    private lateinit var editTextKeyListener: KeyListener
    private lateinit var editTextMovementMethod: MovementMethod
    private lateinit var fieldsList: HashMap<Long, Field>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
            ViewModelProviders.of(this).get(HomeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        homeViewModel.fields.observe(this, Observer {
            fieldsList = it
            addMarkers()
        })
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

        bottomSheetSearch = root.findViewById(R.id.bottom_sheet_search)
        bottomSheetSearchBehavior = BottomSheetBehavior.from(bottomSheetSearch)
        val searchBottomSheetLayout: LinearLayout = root.findViewById(R.id.linear_layout_search)
        searchBottomSheetLayout.setOnClickListener {
            bottomSheetSearchBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
        val bottomSheetSearchCallback = object : BottomSheetBehavior.BottomSheetCallback() {
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
            }
        }
        bottomSheetSearchBehavior.addBottomSheetCallback(bottomSheetSearchCallback)
        bottomSheetSearch.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                val imm =
                    activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(editText.windowToken, 0)
            }
            v.performClick()
            true
        }

        bottomSheetField = root.findViewById(R.id.bottom_sheet_field)
        bottomSheetFieldBehavior = BottomSheetBehavior.from(bottomSheetField)
        bottomSheetFieldBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        val bottomSheetFieldCallback = object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    bottomSheetSearchBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                    bottomSheetSearchBehavior.isHideable = false
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                val params = localFAB.layoutParams as ViewGroup.MarginLayoutParams
                val screenDensity: Float = localFAB.context.resources.displayMetrics.density
                if (slideOffset.isNaN()) {
                    val margin = 206 * screenDensity
                    params.bottomMargin = margin.toInt()
                } else {
                    val margin = (45.428 * slideOffset + 206) * screenDensity
                    params.bottomMargin = margin.toInt()
                }
                localFAB.layoutParams = params
            }
        }
        bottomSheetFieldBehavior.addBottomSheetCallback(bottomSheetFieldCallback)
        val checkCalendarButton = root.findViewById(R.id.check_cal_button) as Button
        checkCalendarButton.setOnClickListener {
            bottomSheetFieldBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity!!)

        return root
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mapReady = true
        val warsaw = LatLng(52.2297, 21.0122)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(warsaw, 13F))
        clusterManager = ClusterManager<MapMarker>(activity, mMap)
        clusterManager.setOnClusterItemClickListener(this)
        clusterManager.setOnClusterClickListener(this)
        mMap.setOnCameraIdleListener(clusterManager)
        mMap.setOnMarkerClickListener(clusterManager)
        mMap.setOnMapClickListener(this)
        addMarkers()
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

    override fun onClusterItemClick(marker: MapMarker): Boolean {
        val cameraPosition =
            CameraPosition.builder()
                .target(marker.position)
                .zoom(mMap.cameraPosition.zoom)
                .build()
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
        val field = fieldsList[marker.fieldID]
        if (field!!.sportsHall) {
            bottomSheetField.findViewById<ImageView>(R.id.field_icon)
                .setImageResource(R.drawable.ic_gym)
            bottomSheetField.findViewById<TextView>(R.id.field_title).text =
                bottomSheetField.context.resources.getString(R.string.gym)
        } else {
            bottomSheetField.findViewById<ImageView>(R.id.field_icon)
                .setImageResource(R.drawable.ic_football_field_green)
            bottomSheetField.findViewById<TextView>(R.id.field_title).text =
                bottomSheetField.context.resources.getString(R.string.field)
        }
        setSportsInField(field)
        val addressTextView = bottomSheetField.findViewById<TextView>(R.id.address_text_view)
        addressTextView.text = field.address
        val gamesList = ArrayList<Game>()
        val eventsAdapter = GamesInChosenFieldAdapter(gamesList)
        setFieldGamesRecyclerView(marker.fieldID, gamesList, eventsAdapter)
        bottomSheetField.findViewById<RecyclerView>(R.id.events_recycler).apply {
            setHasFixedSize(false)
            layoutManager = LinearLayoutManager(bottomSheetField.context)
            adapter = eventsAdapter
        }
        bottomSheetSearchBehavior.isHideable = true
        bottomSheetSearchBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        bottomSheetFieldBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        return true
    }

    override fun onClusterClick(cluster: Cluster<MapMarker>): Boolean {
        val builder = LatLngBounds.builder()
        for (item in cluster.items) {
            builder.include(item.position)
        }
        val bounds = builder.build()
        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100))
        return true
    }

    override fun onMapClick(point: LatLng?) {
        if (bottomSheetFieldBehavior.state == BottomSheetBehavior.STATE_COLLAPSED) {
            bottomSheetFieldBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }
    }

    private fun addMarkers() {
        if (mapReady && this::fieldsList.isInitialized) {
            for ((key, field) in fieldsList) {
                val mapMarker = MapMarker(field.latitude, field.longitude, field.address, key)
                clusterManager.addItem(mapMarker)
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

    private fun setSportsInField(field: Field) {
        val sb = StringBuilder()
        for (i in 0 until field.disciplines.size) {
            val sportID = field.disciplines[i]
            val sportData = SportPrefs.getSportFromMemory(sportID.toLong())
            sb.append(getSportText(sportData.nameEN))
            if (i != field.disciplines.size - 1) {
                sb.append(" \u2022 ")
            }
        }
        bottomSheetField.findViewById<TextView>(R.id.field_sports).text = sb.toString()
    }

    private fun getSportText(sportName: String): String {
        return when (sportName) {
            "Basketball" -> bottomSheetField.context.resources.getString(R.string.basketball)
            "Football" -> bottomSheetField.context.resources.getString(R.string.football)
            "Volleyball" -> bottomSheetField.context.resources.getString(R.string.volleyball)
            "Handball" -> bottomSheetField.context.getString(R.string.handball)
            "Badminton" -> bottomSheetField.context.resources.getString(R.string.badminton)
            "Tennis" -> bottomSheetField.context.resources.getString(R.string.tennis)
            "Table tennis" -> bottomSheetField.context.resources.getString(R.string.table_tennis)
            else -> "Unknown"
        }
    }

    private fun setFieldGamesRecyclerView(
        facilityID: Long,
        gamesList: ArrayList<Game>,
        adapter: GamesInChosenFieldAdapter
    ) {
        val client = OkHttpClient().newBuilder()
            .addInterceptor(AuthenticationInterceptor())
            .build()
        val request = Request.Builder()
            .url("http://10.0.2.2:8080/game/facility/$facilityID")
            .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("blad", "polaczenia z bazom")
                Log.e("blad", e.message!!)
            }

            override fun onResponse(call: Call, response: Response) {
                val jsonResponse = response.body()?.string()
                val json = JSONArray(jsonResponse!!)
                for (i in 0 until json.length()) {
                    val jsonGame = json.getJSONObject(i)
                    val id = jsonGame.getString("id").toLong()
                    val name = jsonGame.getString("name")
                    val duration = jsonGame.getString("duration").toLong()
                    val date = jsonGame.getString("date").toLong()
                    val owner = jsonGame.getString("owner").toLong()
                    val isGamePublic = jsonGame.getString("isPublic")!!.toBoolean()
                    val fieldID = jsonGame.getString("facility").toLong()
                    val sportID = jsonGame.getString("sport").toLong()
                    val jsonPlayers = jsonGame.getJSONArray("players")
                    val players = ArrayList<Int>()
                    for (j in 0 until jsonPlayers.length()) {
                        players.add(jsonPlayers.get(j) as Int)
                    }
                    val maxPlayers = jsonGame.getString("maxPlayers").toInt()
                    val sportData = SportPrefs.getSportFromMemory(sportID)
                    val game =
                        Game(
                            id,
                            name,
                            duration,
                            date,
                            owner,
                            players,
                            isGamePublic,
                            fieldID,
                            sportData,
                            maxPlayers
                        )
                    gamesList.add(game)
                }
                gamesList.sort()
                activity!!.runOnUiThread { adapter.notifyDataSetChanged() }
            }
        })
    }
}