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
import java.text.SimpleDateFormat
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
            val calendar = parseDate(game.date)
            val day = calendar.get(Calendar.DAY_OF_MONTH).toString()
            val month = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault())
            val hour = calendar.get(Calendar.HOUR_OF_DAY).toString() + ":" + calendar.get(Calendar.MINUTE).toString()

            val item = MyGamesItem(day, month, game.name, game.sport.nameEN, hour, "20/24")
            list.add(item)
        }


        /*
        val item1 = MyGamesItem("16", "listopad", "Pierwsza gra na liście", "Koszykówka", "16:00", "20/24")
        val item2 = MyGamesItem("21", "październik", "Lecimy tutej!", "Koszykówka", "16:00", "20/24")
        val item3 = MyGamesItem("1", "grudzień", "Jan Paweł II", "Piłka nożna", "20:00", "4/8")
        val item4 = MyGamesItem("16", "listopad", "Pierwsza gra na liście", "Koszykówka", "16:00", "20/24")
        val item5 = MyGamesItem("21", "październik", "Lecimy tutej!", "Koszykówka", "16:00", "20/24")
        val item6 = MyGamesItem("1", "grudzień", "Jan Paweł II", "Piłka nożna", "20:00", "4/8")
        val item7 = MyGamesItem("16", "listopad", "Pierwsza gra na liście", "Koszykówka", "16:00", "20/24")
        val item8 = MyGamesItem("21", "październik", "Lecimy tutej!", "Koszykówka", "16:00", "20/24")
        val item9 = MyGamesItem("1", "grudzień", "Jan Paweł II", "Piłka nożna", "20:00", "4/8")
        list.add(item1)
        list.add(item2)
        list.add(item3)
        list.add(item4)
        list.add(item5)
        list.add(item6)
        list.add(item7)
        list.add(item8)
        list.add(item9)
        */

        return list
    }
}