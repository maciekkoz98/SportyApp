package com.example.sportyapp.ui.myGames

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.sportyapp.data.game.Game
import com.example.sportyapp.utils.AuthenticationInterceptor
import com.example.sportyapp.utils.SportPrefs
import okhttp3.*
import org.json.JSONArray
import java.io.IOException

class MyGamesViewModel : ViewModel() {

    var games = MutableLiveData<HashMap<Long, Game>>()

    init {
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
                    val maxPlayers = jsonField.getString("maxPlayers").toInt()
                    val jsonPlayers = jsonField.getJSONArray("players")
                    val players = ArrayList<Int>()

                    for (j in 0 until jsonPlayers.length()) {
                        players.add(jsonPlayers.get(j) as Int)
                    }

                    val sportData = SportPrefs.getSportFromMemory(sport)

                    val game = Game(id, name, duration, date, owner, players, isGamePublic, fieldID,
                        sportData, maxPlayers)

                    gottenFields[id] = game
                }
                games.postValue(gottenFields)
            }
        })
    }
}