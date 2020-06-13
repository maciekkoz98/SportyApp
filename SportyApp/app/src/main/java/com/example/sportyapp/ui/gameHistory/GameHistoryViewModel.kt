package com.example.sportyapp.ui.gameHistory

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.sportyapp.data.game.Game
import com.example.sportyapp.utils.AuthenticationInterceptor
import com.example.sportyapp.utils.SportPrefs
import okhttp3.*
import org.json.JSONArray
import java.io.IOException

class GameHistoryViewModel : ViewModel() {
    var passedGames = MutableLiveData<ArrayList<Game>>()

    init {
        getPassedGames()
    }

    private fun getPassedGames() {
        val client = OkHttpClient().newBuilder()
            .addInterceptor(AuthenticationInterceptor())
            .build()
        val request = Request.Builder()
            .url("https://10.0.2.2:8443/game/history")
            .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("blad", "polaczenia z bazom")
                Log.e("blad", e.message!!)
            }

            override fun onResponse(call: Call, response: Response) {
                val gamesList = ArrayList<Game>()
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
                passedGames.postValue(gamesList)
            }
        })
    }
}