package com.example.sportyapp.ui.gameHistory

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sportyapp.R
import com.example.sportyapp.data.game.Game
import com.example.sportyapp.ui.gameHistory.utils.GameHistoryAdapter


class GameHistoryFragment : Fragment() {

    private lateinit var historyViewModel: GameHistoryViewModel
    private lateinit var spinner: Spinner
    private lateinit var recycler: RecyclerView
    private lateinit var passedGames: ArrayList<Game>

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
        val items = arrayOf("No filter", "Basketball", "Football", "Volleyball")
        val adapter = ArrayAdapter(
            this.activity!!,
            android.R.layout.simple_spinner_dropdown_item,
            items
        )
        spinner.adapter = adapter
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
    }
}