package com.example.sportyapp.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.sportyapp.data.field.Field
import com.example.sportyapp.data.game.Game
import com.example.sportyapp.utils.AuthenticationInterceptor
import okhttp3.*
import org.json.JSONArray
import java.io.IOException

class HomeViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is home Fragment"
    }
    val text: LiveData<String> = _text

    var fields = MutableLiveData<HashMap<Long, Field>>()

    init {
        getFieldsFromBackend()
    }

    private fun getFieldsFromBackend() {
        val client = OkHttpClient().newBuilder()
            .addInterceptor(AuthenticationInterceptor())
            .build()
        val request = Request.Builder()
            .url("http://10.0.2.2:8080/facility")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("blad", "polaczenia z bazom")
                Log.e("blad", e.message!!)
            }

            override fun onResponse(call: Call, response: Response) {
                val jsonResponse = response.body()?.string()
                val json = JSONArray(jsonResponse!!)
                val gottenFields = HashMap<Long, Field>()
                for (i in 0 until json.length()) {
                    val jsonField = json.getJSONObject(i)
                    val id = jsonField.getString("id").toLong()
                    val latitude = jsonField.getString("latitude").toDouble()
                    val longitude = jsonField.getString("longitude").toDouble()
                    val address = jsonField.getString("address")
                    val jsonDisciplines = jsonField.getJSONArray("disciplines")
                    val disciplines = ArrayList<Int>()
                    for (j in 0 until jsonDisciplines.length()) {
                        disciplines.add(jsonDisciplines.get(j) as Int)
                    }
                    val field = Field(id, latitude, longitude, address, disciplines)
                    gottenFields[id] = field
                }
                fields.postValue(gottenFields)
            }
        })
    }

    fun getGamesList(facilityID: Int): ArrayList<Game> {
        val client = OkHttpClient().newBuilder()
            .addInterceptor(AuthenticationInterceptor())
            .build()
        val request = Request.Builder()
            .url("http://10.0.2.2:8080/game/facility/$facilityID")
            .build()
        val gamesList = ArrayList<Game>()
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
                    val sport = jsonGame.getString("sport").toLong()
                    val jsonPlayers = jsonGame.getJSONArray("players")
                    val players = ArrayList<Int>()
                    for (j in 0 until jsonPlayers.length()) {
                        players.add(jsonPlayers.get(j) as Int)
                    }
                    val game =
                        Game(id, duration, date, owner, players, isGamePublic, fieldID, sport, name)
                    gamesList.add(game)
                }
            }
        })
        return gamesList
    }

}