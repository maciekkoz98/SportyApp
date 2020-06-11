package com.example.sportyapp.ui.myGames

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sportyapp.R
import com.example.sportyapp.data.game.Game
import com.example.sportyapp.ui.myGames.utils.MyGamesAdapter
import com.example.sportyapp.ui.myGames.utils.MyGamesItem
import java.util.*
import kotlin.collections.ArrayList


class MyGamesFragment : Fragment() {

    private lateinit var gamesList: HashMap<Long, Game>
    private lateinit var gamesViewModel: MyGamesViewModel
    private lateinit var spinner: Spinner
    private lateinit var recycler: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_my_games, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        gamesViewModel =
            ViewModelProviders.of(this).get(MyGamesViewModel::class.java)

        gamesViewModel.games.observe(this, Observer {
            gamesList = it

            spinner = view.findViewById(R.id.myGames_spinner)
            val items = arrayOf("No filter","Basketball", "Football", "Volleyball")
            val adapter = ArrayAdapter<String>(this.activity!!, android.R.layout.simple_spinner_dropdown_item, items)
            spinner.adapter=adapter

            recycler = view.findViewById(R.id.my_games_list_recycler_view)
            val dummyList = generateList()
            recycler.adapter = MyGamesAdapter(dummyList)
            val layoutManager = LinearLayoutManager(activity!!)
            recycler.layoutManager =layoutManager
            recycler.setHasFixedSize(true)
            recycler.addItemDecoration(DividerItemDecoration(recycler.context, layoutManager.orientation))

        })
    }

    private fun parseDate(dateInMillis: Long) : Calendar {
        val calendar = Calendar.getInstance()

        calendar.timeInMillis = dateInMillis
        return calendar
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun generateList(): List<MyGamesItem>{
        val list = ArrayList<MyGamesItem>()

        gamesList.forEach { (_, game) ->
            if (game.date > System.currentTimeMillis()) {
                val calendar = parseDate(game.date)
                val day = calendar.get(Calendar.DAY_OF_MONTH).toString()
                val month =
                    calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault())
                val hour = calendar.get(Calendar.HOUR_OF_DAY).toString() + ":" + calendar.get(
                    Calendar.MINUTE
                ).toString()
                val people = game.players.size.toString() + "/" + game.maxPlayers.toString()


                val item = MyGamesItem(day, month, game.name, game.sport.nameEN, hour, people)
                list.add(item)
            }
        }

        return list
    }
}