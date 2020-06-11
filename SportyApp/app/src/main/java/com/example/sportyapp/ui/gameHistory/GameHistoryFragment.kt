package com.example.sportyapp.ui.gameHistory

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sportyapp.R
import com.example.sportyapp.data.game.Game
import com.example.sportyapp.ui.gameHistory.utils.GameHistoryAdapter
import com.example.sportyapp.utils.SportPrefs
import java.util.*
import kotlin.collections.ArrayList


class GameHistoryFragment : Fragment() {

    private lateinit var historyViewModel: GameHistoryViewModel
    private lateinit var spinner: Spinner
    private lateinit var recycler: RecyclerView
    private lateinit var passedGames: ArrayList<Game>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                view!!.findNavController().navigate(R.id.action_nav_history_to_nav_home)
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        historyViewModel =
            ViewModelProviders.of(this).get(GameHistoryViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_last_games, container, false)
        passedGames = ArrayList()
        historyViewModel.passedGames.observe(this, Observer {
            passedGames = it
            recycler.adapter = GameHistoryAdapter(passedGames)
        })

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        spinner = view.findViewById(R.id.history_spinner)
        recycler = view.findViewById(R.id.last_games_recycler_view)
        recycler.adapter = GameHistoryAdapter(passedGames)
        val layoutManager = LinearLayoutManager(activity!!)
        recycler.layoutManager = layoutManager
        recycler.setHasFixedSize(true)
        recycler.addItemDecoration(
            DividerItemDecoration(
                recycler.context,
                layoutManager.orientation
            )
        )
        setSpinnerValues()
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                val filter = spinner.selectedItem.toString()

                if (filter != getString(R.string.no_filter)) {
                    val filteredGames = passedGames.filter {game ->
                        game.sport.namePL == filter || game.sport.nameEN == filter }
                    recycler.adapter = GameHistoryAdapter(filteredGames)
                } else {
                    recycler.adapter = GameHistoryAdapter(passedGames)
                }
            }

        }
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