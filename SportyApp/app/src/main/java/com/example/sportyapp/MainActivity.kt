package com.example.sportyapp

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.sportyapp.data.game.Sport
import com.example.sportyapp.utils.AuthenticationInterceptor
import com.example.sportyapp.utils.SportPrefs
import com.google.android.material.navigation.NavigationView
import okhttp3.*
import org.json.JSONArray
import java.io.IOException
import androidx.navigation.ui.AppBarConfiguration as AppBarConfiguration1

const val REQUEST_LOCATION = 2137

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        SportPrefs.context = applicationContext
        getSports()

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration1(
            setOf(
                R.id.nav_home, R.id.nav_fields, R.id.nav_my_games,
                R.id.nav_add_game, R.id.nav_history
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        val headerView = navView.getHeaderView(0)
        val header = headerView.findViewById(R.id.header_view) as LinearLayout
        header.setOnClickListener {
            navController.navigate(R.id.nav_person)
            drawerLayout.closeDrawer(GravityCompat.START)
        }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    private fun getSports() {
        val sports = HashMap<Long, Sport>()

        val client = OkHttpClient().newBuilder()
            .addInterceptor(AuthenticationInterceptor())
            .build()
        val request = Request.Builder()
            .url("http://10.0.2.2:8080/sport")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("blad", "polaczenia z bazom")
                Log.e("blad", e.message!!)
            }

            override fun onResponse(call: Call, response: Response) {
                val jsonResponse = response.body()?.string()
                val json = JSONArray(jsonResponse!!)
                //val gottenFields = HashMap<Long, Sport>()
                for (i in 0 until json.length()) {
                    val jsonField = json.getJSONObject(i)
                    val id = jsonField.getString("id").toLong()
                    val nameEn = jsonField.getString("nameEN").toString()
                    val namePl = jsonField.getString("namePL").toString()

                    val jsonSynonyms = jsonField.getJSONArray("synonyms")
                    val synonyms = ArrayList<String>()

                    for (j in 0 until jsonSynonyms.length()) {
                        synonyms.add(jsonSynonyms.get(j) as String)
                    }

                    val sport = Sport(id, nameEn, namePl, synonyms)
                    sports[id] = sport
                }

                SportPrefs.putAllSportsToMemory(sports)
            }
        })
    }
}