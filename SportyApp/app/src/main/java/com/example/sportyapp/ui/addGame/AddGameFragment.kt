package com.example.sportyapp.ui.addGame

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.icu.text.SimpleDateFormat
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.example.sportyapp.R
import com.example.sportyapp.utils.AuthenticationInterceptor
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import java.util.*

class AddGameFragment : Fragment() {

    private lateinit var addGameViewModel: AddGameViewModel
    private lateinit var gameName: EditText
    private lateinit var gameDate: TextView
    private lateinit var gameStart: TextView
    private lateinit var gameDuration: TextView
    private lateinit var isGamePublic: CheckBox

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        addGameViewModel =
            ViewModelProviders.of(this).get(AddGameViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_add_game, container, false)

        /*val textView: TextView = root.findViewById(R.id.text_send)
        addGameViewModel.text.observe(this, Observer {
            textView.text = it
        })*/
        return root
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        gameName = view.findViewById(R.id.editText_game_name)
        gameDate = view.findViewById(R.id.editText_date_of_game)
        gameStart = view.findViewById(R.id.add_start)
        gameDuration = view.findViewById(R.id.add_duration)
        isGamePublic = view.findViewById(R.id.checkBox_is_public)
        //Wybór daty
        gameDate.setOnClickListener{
            val c = Calendar.getInstance()
            val y = c.get(Calendar.YEAR)
            val m = c.get(Calendar.MONTH)
            val d = c.get(Calendar.DAY_OF_MONTH)
            //Niedoskonły wybór daty - trzeba kliknąć na pole 2 razy. Pytanie brzmi czy wyrzuacmy wogóle, zostawiamy czy próba poprawy.
            val dpd = DatePickerDialog(this.activity!!, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    gameDate.setText(dayOfMonth.toString()+"."+monthOfYear.toString()+"."+year.toString())

                }, y, m, d)
            dpd.show()
        }
        //wybór godzin
        gameStart.setOnClickListener{
            val cal = Calendar.getInstance()
            val timeSetListener = TimePickerDialog.OnTimeSetListener { view, hour, minute ->
                cal.set(Calendar.HOUR_OF_DAY, hour)
                cal.set(Calendar.MINUTE, minute)
               gameStart.text = SimpleDateFormat("HH:mm").format(cal.time)
            }
            TimePickerDialog(this.activity!!, timeSetListener, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true).show()
        }
        gameDuration.setOnClickListener{
            val cal = Calendar.getInstance()
            val timeSetListener = TimePickerDialog.OnTimeSetListener { view, hour, minute ->
                cal.set(Calendar.HOUR_OF_DAY, hour)
                cal.set(Calendar.MINUTE, minute)
                gameDuration.text = SimpleDateFormat("HH:mm").format(cal.time)
            }
            TimePickerDialog(this.activity!!, timeSetListener, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true).show()
        }
        //obsługa guzika
        val addGameBtn = view.findViewById<Button>(R.id.addGameButton)
        addGameBtn.setOnClickListener(addGameListener)
    }
    private val addGameListener = View.OnClickListener { view ->
        if(validate()) {
            addGame()
            activity?.onBackPressed()
        }
    }
    //walidacja pól: name, date, duration - można pokusić się o więcej regexów.
    private fun validate(): Boolean{
        var check = true
        val namePattern = Regex("[a-zA-Z0-9]+")
        if(gameName.length()==0){
            gameName.error = "Name is required!"
            check = false
        }else if(gameName.length()>30){
            gameName.error = "Name max length is 30!"
            check = false
        }else if(!namePattern.matches(gameName.text.toString())){
            gameName.error = "Name can contain only letters and numbers!"
            check = false
        }
        if(gameDate.length()==0){
            gameDate.error = "Provide date in dd.mm.yyyy formant."
        }

        return check
    }
    private fun addGame() {
        val url = "http://10.0.2.2:8080/game"
        val httpClient = OkHttpClient().newBuilder()
            .addInterceptor(AuthenticationInterceptor())
            .build()

        val payload = JSONObject()
        payload.put("name", gameName.text.toString())
        payload.put("date", System.currentTimeMillis())
        payload.put("duration", 90.toLong())
        payload.put("facility", 1.toLong())
        //payload.put("sport", 1.toLong())
        payload.put("isPublic", "")
        payload.put("owner", "")
        payload.put("players", emptyList<Int>())

        val body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), payload.toString())
        val request = Request.Builder().method("POST", body).url(url).build()

        httpClient.newCall(request).enqueue(object: Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                Log.d("test", e.stackTrace.toString())
            }

            override fun onResponse(call: Call, response: Response) {
                Log.d("test", "fine")
                print(response.message())
            }
        })
    }
}