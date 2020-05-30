package com.example.sportyapp.ui.addGame

import android.app.DatePickerDialog
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
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class AddGameFragment : Fragment() {

    private lateinit var addGameViewModel: AddGameViewModel
    private lateinit var gameName: EditText
    private lateinit var gameDate: TextView
    private lateinit var gameDuration: EditText
    private lateinit var isGamePublic: CheckBox
    private lateinit var calendar: GregorianCalendar

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

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        gameName = view.findViewById(R.id.editText_game_name)
        gameDate = view.findViewById(R.id.editText_date_of_game)
        gameDuration = view.findViewById(R.id.editText_game_duration)
        isGamePublic = view.findViewById(R.id.checkBox_is_public)
        gameDate.setOnClickListener{
            val c = Calendar.getInstance()
            val y = c.get(Calendar.YEAR)
            val m = c.get(Calendar.MONTH)
            val d = c.get(Calendar.DAY_OF_MONTH)

            val dpd = DatePickerDialog(this.activity!!,android.R.style.Theme_Material_Light_Dialog, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                calendar = GregorianCalendar(year, monthOfYear, dayOfMonth)
                gameDate.text = "$dayOfMonth.$monthOfYear.$year"

                }, y, m, d)
            dpd.show()
        }
        val addGameBtn = view.findViewById<Button>(R.id.addGameButton)
        addGameBtn.setOnClickListener(addGameListener)
    }

    @RequiresApi(Build.VERSION_CODES.O)
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
            gameDate.error = "Provide date in dd.mm.yyyy format."
        }
        if(gameDuration.length()==0){
            gameDuration.error = "Duration is required"
            check = false
        }

        return check
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun addGame() {
        val url = "http://10.0.2.2:8080/game"
        val httpClient = OkHttpClient().newBuilder()
            .addInterceptor(AuthenticationInterceptor())
            .build()

        val payload = JSONObject()
        payload.put("name", gameName.text.toString())
        payload.put("date", calendar.timeInMillis)
        payload.put("duration", getDuration()) // na razie zastosowałam format H:mm
        payload.put("facility", 1.toLong()) // na sztywno wpisane wartości
        payload.put("sport", 1.toLong())
        payload.put("owner", 1.toLong())
        payload.put("isPublic", isGamePublic.isChecked)
        //payload.put("players", arrayOf(0, 1))
        //payload.put("id", 1)



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

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getDuration() : Long {
        val duration = gameDuration.text
        val time = LocalTime.parse(duration, DateTimeFormatter.ofPattern("H:mm"))
        return time.toNanoOfDay()
    }
}