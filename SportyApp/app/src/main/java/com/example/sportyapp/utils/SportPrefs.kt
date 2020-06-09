package com.example.sportyapp.utils

import android.content.Context
import android.content.SharedPreferences
import com.example.sportyapp.data.game.Sport
import com.google.gson.Gson


class SportPrefs {
    companion object {
        private const val PRIVATE_MODE = Context.MODE_PRIVATE
        private const val PREF_NAME = "sports"
        lateinit var context: Context


        fun putAllSportsToMemory(sports: HashMap<Long, Sport>) {
            val sportsPref: SharedPreferences = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE)

            val gson = Gson()
            val editor = sportsPref.edit()

            sports.forEach { (id, sport) ->
                val sportJson = gson.toJson(sport)
                editor.putString(id.toString(), sportJson)
                editor.apply()
            }
        }

        fun getSportFromMemory(id: Long) : Sport {
            val sportsPref: SharedPreferences = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE)
            val gson = Gson()

            val jsonSport = sportsPref.getString(id.toString(), "")
            return gson.fromJson(jsonSport, Sport::class.java)
        }

        fun getSportByName(name: String) : Sport? {
            val sportsPref: SharedPreferences = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE)

            val allSports = HashMap<Long, Sport>()
            val gson = Gson()
            val allEntries = sportsPref.all


            for (entry in allEntries.entries) {
                val sport = gson.fromJson(entry.value.toString(), Sport::class.java)

                if (sport.nameEN == name) {
                    return sport
                }
            }

            return null
        }

        fun getAllSportsFromMemory() : HashMap<Long, Sport> {
            val sportsPref: SharedPreferences = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE)

            val allSports = HashMap<Long, Sport>()
            val gson = Gson()
            val allEntries = sportsPref.all

            for (entry in allEntries.entries) {
                val id = entry.key.toLong()
                val sport = gson.fromJson(entry.value.toString(), Sport::class.java)

                allSports[id] = sport
            }

            return allSports
        }
    }
}