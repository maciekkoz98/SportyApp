package com.example.sportyapp.ui.myGames

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sportyapp.R
import com.example.sportyapp.data.game.Game
import com.example.sportyapp.ui.myGames.utils.MyGamesAdapter
import com.example.sportyapp.ui.myGames.utils.MyGamesItem
import com.example.sportyapp.utils.SportPrefs
import java.util.*
import kotlin.collections.ArrayList


class MyGamesFragment : Fragment() {

    private lateinit var gamesList: HashMap<Long, Game>
    private lateinit var gamesViewModel: MyGamesViewModel
    private lateinit var spinner: Spinner
    private lateinit var recycler: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                view!!.findNavController().navigate(R.id.action_nav_my_games_to_nav_home)
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

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
            recycler = view.findViewById(R.id.my_games_list_recycler_view)
            var dummyList = generateList(null)
            recycler.adapter = MyGamesAdapter(dummyList)
            val layoutManager = LinearLayoutManager(activity!!)
            recycler.layoutManager =layoutManager
            recycler.setHasFixedSize(true)
            recycler.addItemDecoration(DividerItemDecoration(recycler.context, layoutManager.orientation))
            setSpinnerValues()

            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(p0: AdapterView<*>?) {
                    dummyList = generateList(null)
                    recycler.adapter = MyGamesAdapter(dummyList)
                }

                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    dummyList = generateList(spinner.selectedItem.toString())
                    recycler.adapter = MyGamesAdapter(dummyList)
                }

            }

        })
    }

    private fun parseDate(dateInMillis: Long) : Calendar {
        val calendar = Calendar.getInstance()

        calendar.timeInMillis = dateInMillis
        return calendar
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun generateList(filterSport: String?): List<MyGamesItem>{
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

        if (filterSport != null && filterSport != getString(R.string.no_filter)) {
            return list.filter { gameItem ->
                gameItem.sport == filterSport
            }
        }
        return list
    }

    private fun setSpinnerValues() {
        val sports = ArrayList<String>()

        sports.add(getString(R.string.no_filter))
        SportPrefs.getAllSportsFromMemory().forEach { (_, sport) ->
            when (Locale.getDefault().language) {
                "pl" -> {
                    sports.add(sport.namePL)
                }
                else -> {
                    sports.add(sport.nameEN)
                }
            }
        }

        val spinnerAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            sports.toList()
        )

        spinner.adapter = spinnerAdapter
    }
}