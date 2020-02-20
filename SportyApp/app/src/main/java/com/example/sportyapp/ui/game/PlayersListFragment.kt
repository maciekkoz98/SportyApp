package com.example.sportyapp.ui.game

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sportyapp.R
import com.example.sportyapp.ui.game.utils.PlayersListAdapter

class PlayersListFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val rootView = inflater.inflate(R.layout.fragment_players_list, container, false)

        val playersListRecyclerView = rootView.findViewById(R.id.players_list_recycler_view) as RecyclerView

        playersListRecyclerView.layoutManager = LinearLayoutManager(activity)

        playersListRecyclerView.adapter = PlayersListAdapter()

        return rootView
    }
}