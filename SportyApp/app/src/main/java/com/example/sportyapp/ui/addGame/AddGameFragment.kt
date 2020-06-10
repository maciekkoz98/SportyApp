package com.example.sportyapp.ui.addGame

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.icu.text.SimpleDateFormat
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.example.sportyapp.R
import com.example.sportyapp.data.field.Field
import com.example.sportyapp.ui.home.HomeViewModel
import com.example.sportyapp.utils.AuthenticationInterceptor
import com.example.sportyapp.utils.SportPrefs
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList

class AddGameFragment : Fragment() {

    private lateinit var gameName: EditText
    private lateinit var maxPlayers: EditText
    private lateinit var gameDate: TextView
    private lateinit var gameStart: TextView
    private lateinit var gameDuration: TextView
    private lateinit var fieldAddress: AutoCompleteTextView
    private lateinit var isGamePublic: CheckBox
    private lateinit var disciplineSpinner: Spinner
    private lateinit var calendar: GregorianCalendar
    private lateinit var fieldsList: HashMap<Long, Field>
    private var fieldID: Long = 0L

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_add_game, container, false)
        val homeViewModel =
            ViewModelProviders.of(this).get(HomeViewModel::class.java)
        homeViewModel.fields.observe(this, androidx.lifecycle.Observer {
            fieldsList = it
            setFieldAddress()
            setAddressValues()
        })
        return root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fieldID = arguments!!.getLong("fieldID")
        calendar = GregorianCalendar()

        gameName = view.findViewById(R.id.editText_game_name)
        fieldAddress = view.findViewById(R.id.set_field_address)
        maxPlayers = view.findViewById(R.id.max_players)
        gameDate = view.findViewById(R.id.editText_date_of_game)
        gameStart = view.findViewById(R.id.add_start)
        gameDuration = view.findViewById(R.id.add_duration)
        isGamePublic = view.findViewById(R.id.checkBox_is_public)
        disciplineSpinner = view.findViewById(R.id.set_discipline)

        //Wybór daty
        gameDate.setOnClickListener {
            val c = Calendar.getInstance()
            val y = c.get(Calendar.YEAR)
            val m = c.get(Calendar.MONTH)
            val d = c.get(Calendar.DAY_OF_MONTH)

            val dpd = DatePickerDialog(
                this.activity!!,
                android.R.style.Theme_Material_Light_Dialog,
                DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                    Log.d("time", gameDuration.text.toString())
                    if(gameDuration.text.toString() == "" || gameDuration.text.toString() == "00:00") {
                        calendar = GregorianCalendar(year, monthOfYear, dayOfMonth)
                    } else {
                        calendar.set(year, monthOfYear, dayOfMonth)
                    }
                    gameDate.text =
                        dayOfMonth.toString() + "." + (monthOfYear + 1).toString() + "." + year.toString()

                }, y, m, d)
            dpd.show()
        }
        //wybór godzin
        gameStart.setOnClickListener {
            val cal = Calendar.getInstance()
            val timeSetListener = TimePickerDialog.OnTimeSetListener { _, hour, minute ->
                cal.set(Calendar.HOUR_OF_DAY, hour)
                cal.set(Calendar.MINUTE, minute)
                calendar.set(Calendar.HOUR_OF_DAY, hour)
                calendar.set(Calendar.MINUTE, minute)
                gameStart.text = SimpleDateFormat("HH:mm").format(cal.time)
            }
            TimePickerDialog(
                this.activity!!,
                timeSetListener,
                cal.get(Calendar.HOUR_OF_DAY),
                cal.get(Calendar.MINUTE),
                true
            ).show()
        }
        gameDuration.setOnClickListener {
            val cal = Calendar.getInstance()
            val timeSetListener = TimePickerDialog.OnTimeSetListener { _, hour, minute ->
                cal.set(Calendar.HOUR_OF_DAY, hour)
                cal.set(Calendar.MINUTE, minute)
                gameDuration.text = SimpleDateFormat("HH:mm").format(cal.time)
            }
            TimePickerDialog(
                this.activity!!,
                timeSetListener,
                cal.get(Calendar.HOUR_OF_DAY),
                cal.get(Calendar.MINUTE),
                true
            ).show()
        }

        fieldAddress.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                var checked = false

                for ((id, value) in fieldsList) {
                    if (value.address == fieldAddress.text.toString()) {
                        setSpinnerValues(fieldsList[id]!!)
                        checked = true
                    }
                }

                if(!checked) {
                    disciplineSpinner.adapter = null
                }
            }
        })

        //obsługa guzika
        val addGameBtn = view.findViewById<Button>(R.id.addGameButton)
        addGameBtn.setOnClickListener(addGameListener)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private val addGameListener = View.OnClickListener {
        if(validate()) {
            addGame()
            activity!!.onBackPressed()
        }
    }

    //walidacja pól: name, date, duration - można pokusić się o więcej regexów.
    @RequiresApi(Build.VERSION_CODES.O)
    private fun validate(): Boolean{
        var check = true
        val namePattern = Regex("[a-zA-Z0-9 ]+")
        if (gameName.length() == 0) {
            gameName.error = getString(R.string.name_required_error)
            check = false
        } else if (gameName.length() > 30) {
            gameName.error = getString(R.string.name_max_length_error)
            check = false
        } else if (!namePattern.matches(gameName.text.toString())) {
            gameName.error = getString(R.string.name_content_error)
            check = false
        }

        if (disciplineSpinner.selectedItem.toString().isEmpty()) {
            fieldAddress.error = getString(R.string.field_not_found)
            check = false
        }

        var guard = false
        for ((_, value) in fieldsList) {
            if (value.address == fieldAddress.text.toString()) {
                guard = true
            }
        }
        if (!guard) {
            fieldAddress.error = getString(R.string.field_not_found)
            check = false
        }

        if (maxPlayers.length() == 0) {
            maxPlayers.error = R.string.max_players_empty_error.toString()
            check = false
        } else if (maxPlayers.text.toString().toInt() < 2) {
            maxPlayers.error = R.string.max_players_error.toString()
            check = false
        }
        if (gameDate.length() == 0) {
            gameDate.error = getString(R.string.date_format_error)
            check = false
        }
        if (getDuration() <= 0) {
            gameDuration.error = getString(R.string.duration_error)
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
        var fieldID = 0L
        for ((_, value) in fieldsList) {
            if (value.address == fieldAddress.text.toString()) {
                fieldID = value.id
            }
        }
        payload.put("facility", fieldID)
        val disciplineName = disciplineSpinner.selectedItem.toString()
        payload.put(
            "sport",
            SportPrefs.getSportByName(disciplineName, Locale.getDefault().language)!!.id
        )
        payload.put("owner", 1.toLong())
        payload.put("maxPlayers", maxPlayers.text.toString().toInt())
        payload.put("isPublic", isGamePublic.isChecked)
        //payload.put("players", arrayOf(0, 1))
        //payload.put("id", 1)
        val body = RequestBody.create(
            MediaType.parse("application/json; charset=utf-8"),
            payload.toString()
        )
        val request = Request.Builder().method("POST", body).url(url).build()

        httpClient.newCall(request).enqueue(object : Callback {
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
    private fun getDuration(): Long {
        val dateFormatter = SimpleDateFormat("HH:mm")
        val end = dateFormatter.parse(gameDuration.text.toString())
        val start = dateFormatter.parse(gameStart.text.toString())

        return end.time - start.time
    }

    private fun setFieldAddress() {
        if (fieldID != 0L) {
            fieldAddress.setText(fieldsList[fieldID]!!.address, TextView.BufferType.EDITABLE)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setAddressValues() {
        val addresses = ArrayList<String>()

        fieldsList.forEach { (_, field) ->
            addresses.add(field.address)
        }

        fieldAddress.threshold = 0
        fieldAddress.setAdapter(ArrayAdapter(requireContext(), android.R.layout.select_dialog_item, addresses.toList()))

        fieldAddress.setOnTouchListener { _, _ ->
            fieldAddress.showDropDown()
            false
        }
        fieldAddress.onItemClickListener =
            AdapterView.OnItemClickListener { _, p1, _, _ ->
                val inputManager : InputMethodManager = activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputManager.hideSoftInputFromWindow(p1!!.applicationWindowToken, 0)
            }

    }

    private fun setSpinnerValues(field: Field) {
        val disciplines = ArrayList<String>()

        field.disciplines.forEach { id ->
            disciplines.add(getSportText(SportPrefs.getSportFromMemory(id.toLong()).nameEN))
        }

        val spinnerAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            disciplines.toList()
        )
        disciplineSpinner.adapter = spinnerAdapter
    }

    private fun getSportText(sportName: String): String {
        return when (sportName) {
            "Basketball" -> disciplineSpinner.context.resources.getString(R.string.basketball)
            "Football" -> disciplineSpinner.context.resources.getString(R.string.football)
            "Volleyball" -> disciplineSpinner.context.resources.getString(R.string.volleyball)
            "Handball" -> disciplineSpinner.context.getString(R.string.handball)
            "Badminton" -> disciplineSpinner.context.resources.getString(R.string.badminton)
            "Tennis" -> disciplineSpinner.context.resources.getString(R.string.tennis)
            "Table tennis" -> disciplineSpinner.context.resources.getString(R.string.table_tennis)
            else -> "Unknown"
        }
    }
}