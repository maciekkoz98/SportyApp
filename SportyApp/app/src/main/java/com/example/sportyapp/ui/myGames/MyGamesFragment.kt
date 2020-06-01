package com.example.sportyapp.ui.myGames

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.sportyapp.R
import com.example.sportyapp.data.game.Game


class MyGamesFragment : Fragment() {

    private lateinit var gamesList: HashMap<Long, Game>
    private lateinit var gamesViewModel: MyGamesViewModel
    private lateinit var spinner: Spinner

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        gamesViewModel =
            ViewModelProviders.of(this).get(MyGamesViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_my_games, container, false)
        gamesViewModel =
            ViewModelProviders.of(this).get(MyGamesViewModel::class.java)
        gamesViewModel.games.observe(this, Observer {
            gamesList = it
        })
        return root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        spinner = view.findViewById(R.id.myGames_spinner)
        val items = arrayOf("No filter","Basketball", "Football", "Volleyball")
        val adapter = ArrayAdapter<String>(this.activity!!, android.R.layout.simple_spinner_dropdown_item, items)
        spinner.adapter=adapter
    }
}