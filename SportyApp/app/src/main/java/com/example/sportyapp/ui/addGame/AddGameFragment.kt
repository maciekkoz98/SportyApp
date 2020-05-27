package com.example.sportyapp.ui.addGame

import android.net.Credentials
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.sportyapp.R
import com.example.sportyapp.utils.AuthenticationInterceptor
import kotlinx.android.synthetic.main.fragment_add_game.*
import okhttp3.*
import org.json.JSONObject
import retrofit2.Retrofit
import java.io.IOException
import java.time.LocalDateTime
import java.util.*

class AddGameFragment : Fragment() {

    private lateinit var addGameViewModel: AddGameViewModel
    private lateinit var gameName: EditText
    private lateinit var gameDate: EditText
    private lateinit var gameDuration: EditText
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        gameName = view.findViewById(R.id.editText_game_name)
        gameDate = view.findViewById(R.id.editText_date_of_game)
        gameDuration = view.findViewById(R.id.editText_game_duration)
        isGamePublic = view.findViewById(R.id.checkBox_is_public)

        val addGameBtn = view.findViewById<Button>(R.id.addGameButton)
        addGameBtn.setOnClickListener(addGameListener)
    }

    private val addGameListener = View.OnClickListener { view ->
        addGame()
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