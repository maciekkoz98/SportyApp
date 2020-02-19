package com.example.sportyapp.ui.myGames

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sportyapp.R
import com.example.sportyapp.ui.myGames.utils.MyGamesListAdapter

class MyGamesListFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val rootView = inflater.inflate(R.layout.fragment_my_games_list, container, false)

        val lastGamesRecyclerView = rootView.findViewById(R.id.my_games_list_recycler_view) as RecyclerView

        lastGamesRecyclerView.layoutManager = LinearLayoutManager(activity)

        lastGamesRecyclerView.adapter = MyGamesListAdapter()

        return rootView
    }
}