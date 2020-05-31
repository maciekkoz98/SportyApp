package com.example.sportyapp.ui.personInfo


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.example.sportyapp.R
import com.example.sportyapp.ui.personInfo.utils.LastGamesAdapter
import com.example.sportyapp.ui.personInfo.utils.OpinionsAdapter
import kotlinx.android.synthetic.main.fragment_opinions.*


class LastGamesFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val rootView = inflater.inflate(R.layout.fragment_last_games, container, false)

        val lastGamesRecyclerView = rootView.findViewById(R.id.last_games_recycler_view) as RecyclerView

        lastGamesRecyclerView.layoutManager = LinearLayoutManager(activity)

        lastGamesRecyclerView.adapter = LastGamesAdapter()

        return rootView
    }

}
