package com.example.sportyapp.ui.game

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sportyapp.R
import com.example.sportyapp.data.game.Game
import com.example.sportyapp.ui.game.utils.GameItem
import com.example.sportyapp.ui.game.utils.PlayersListAdapter

class GameFragment : Fragment() {

    private lateinit var recycler: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (activity as AppCompatActivity).supportActionBar?.title =
            getString(R.string.game_app_bar_title)
        return inflater.inflate(R.layout.fragment_game, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recycler = view.findViewById(R.id.game_recycler)
        val dummyList = generateList()
        recycler.adapter = PlayersListAdapter(dummyList)
        val layoutManager = LinearLayoutManager(activity!!)
        recycler.layoutManager =layoutManager
        recycler.setHasFixedSize(true)
        recycler.addItemDecoration(DividerItemDecoration(recycler.context, layoutManager.orientation))
    }

    private fun generateList(): List<GameItem>{
        val list = ArrayList<GameItem>()
        val item1 = GameItem("Rafał")
        val item2 = GameItem("Wojtek")
        val item3 = GameItem("Maciek")
        val item4 = GameItem("Kasia")
        val item5 = GameItem("Mateusz")
        val item6 = GameItem("Jan")
        val item7 = GameItem("Paweł")
        val item8 = GameItem("2")
        val item9 = GameItem("Papież")
        val item10 = GameItem("Rafał")
        val item11 = GameItem("Rafał")
        val item12 = GameItem("Rafał")
        val item13 = GameItem("Rafał")
        val item14 = GameItem("Rafał")
        list.add(item1)
        list.add(item2)
        list.add(item3)
        list.add(item4)
        list.add(item5)
        list.add(item6)
        list.add(item7)
        list.add(item8)
        list.add(item9)
        list.add(item10)
        list.add(item11)
        list.add(item12)
        list.add(item13)
        list.add(item14)
        return list
    }
}