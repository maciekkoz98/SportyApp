package com.example.sportyapp.ui.myGames

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.sportyapp.data.game.Game
import com.example.sportyapp.data.game.Sport
import com.example.sportyapp.utils.AuthenticationInterceptor
import okhttp3.*
import org.json.JSONArray
import java.io.IOException

class MyGamesViewModel : ViewModel() {

    var games = MutableLiveData<HashMap<Long, Game>>()
    private var sports = HashMap<Long, Sport>()

    init {
        getSports()
        getGames()
    }


    private fun getGames() {
        val client = OkHttpClient().newBuilder()
            .addInterceptor(AuthenticationInterceptor())
            .build()
        val request = Request.Builder()
            .url("http://10.0.2.2:8080/game")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("blad", "polaczenia z bazom")
                Log.e("blad", e.message!!)
            }

            override fun onResponse(call: Call, response: Response) {
                val jsonResponse = response.body()?.string()
                val json = JSONArray(jsonResponse!!)
                val gottenFields = HashMap<Long, Game>()
                for (i in 0 until json.length()) {
                    val jsonField = json.getJSONObject(i)
                    val id = jsonField.getString("id").toLong()
                    val name = jsonField.getString("name").toString()
                    val duration = jsonField.getString("duration").toLong()
                    val date = jsonField.getString("date").toLong()
                    val owner = jsonField.getString("owner").toLong()
                    val isGamePublic = jsonField.getString("isPublic").toBoolean()
                    val fieldID = jsonField.getString("facility").toLong()
                    val sport = jsonField.getString("sport").toLong()
                    val jsonPlayers = jsonField.getJSONArray("players")
                    val players = ArrayList<Int>()

                    for (j in 0 until jsonPlayers.length()) {
                        players.add(jsonPlayers.get(j) as Int)
                    }

                    /*Log.d("id", id.toString())
                    Log.d("dur", duration.toString())
                    Log.d("owner", owner.toString())
                    Log.d("players", players.toString())
                    Log.d("publiv", isGamePublic.toString())
                    Log.d("field", fieldID.toString())
                    Log.d("sport", sport.toString())
                    Log.d("name", name)*/

                    val game = Game(id, name, duration, date, owner, players, isGamePublic, fieldID,
                        sports[sport]!!
                    )
                    gottenFields[id] = game
                }
                games.postValue(gottenFields)
            }
        })
    }

    private fun getSports() {
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
            }
        })
    }
}