package com.example.sportyapp.ui.myGames

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sportyapp.R
import com.example.sportyapp.ui.game.GameFragment
import com.example.sportyapp.ui.myGames.utils.MyGamesListAdapter

class MyGamesListFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val rootView = inflater.inflate(R.layout.fragment_my_games_list, container, false)

        val myGamesRecyclerView =
            rootView.findViewById(R.id.my_games_list_recycler_view) as RecyclerView

        myGamesRecyclerView.layoutManager = LinearLayoutManager(activity)

        val adapter = MyGamesListAdapter()
        adapter.onGameClicked = { pos ->
            Log.d("Position", pos.toString())

            activity!!.supportFragmentManager.beginTransaction()
                .replace(R.id.my_games_layout, GameFragment()).commit()
        }

        myGamesRecyclerView.adapter = adapter

        return rootView
    }
}