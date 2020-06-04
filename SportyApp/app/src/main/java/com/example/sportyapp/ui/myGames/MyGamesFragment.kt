package com.example.sportyapp.ui.myGames

import android.os.Bundle
import android.util.Log
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
import com.example.sportyapp.ui.game.GameFragment
import com.example.sportyapp.ui.myGames.utils.MyGamesAdapter
import com.example.sportyapp.ui.myGames.utils.MyGamesItem
import kotlinx.android.synthetic.main.fragment_my_games.*
import com.example.sportyapp.data.game.Game


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

        recycler = view.findViewById(R.id.my_games_list_recycler_view)
        val dummyList = generateList()
        recycler.adapter = MyGamesAdapter(dummyList)
        val layoutManager = LinearLayoutManager(activity!!)
        recycler.layoutManager =layoutManager
        recycler.setHasFixedSize(true)
        recycler.addItemDecoration(DividerItemDecoration(recycler.context, layoutManager.orientation))
    }

    private fun generateList(): List<MyGamesItem>{
        val list = ArrayList<MyGamesItem>()
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
        return list
    }
}