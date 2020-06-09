package com.example.sportyapp.ui.home

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.sportyapp.data.field.Field
import com.example.sportyapp.utils.AuthenticationInterceptor
import okhttp3.*
import org.json.JSONArray
import java.io.IOException

class HomeViewModel : ViewModel() {
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
                    val sportsHall = jsonField.getString("sportsHall")!!.toBoolean()
                    val jsonDisciplines = jsonField.getJSONArray("disciplines")
                    val disciplines = ArrayList<Int>()
                    for (j in 0 until jsonDisciplines.length()) {
                        disciplines.add(jsonDisciplines.get(j) as Int)
                    }
                    val field = Field(id, latitude, longitude, address, sportsHall, disciplines)
                    gottenFields[id] = field
                }
                fields.postValue(gottenFields)
            }
        })
    }

}