package com.example.sportyapp.ui.gameHistory

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sportyapp.R
import com.example.sportyapp.ui.game.GameFragment
import com.example.sportyapp.ui.gameHistory.utils.GameHistoryAdapter
import com.example.sportyapp.ui.gameHistory.utils.GameHistoryItem
import com.example.sportyapp.ui.myGames.utils.MyGamesAdapter
import com.example.sportyapp.ui.myGames.utils.MyGamesItem
import kotlinx.android.synthetic.main.fragment_my_games.*


class GameHistoryFragment : Fragment() {

    private lateinit var historyViewModel: GameHistoryViewModel
    private lateinit var spinner: Spinner
    private lateinit var recycler: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        historyViewModel =
            ViewModelProviders.of(this).get(GameHistoryViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_last_games, container, false)
        /*val textView: TextView = root.findViewById(R.id.text_gallery)
        myGamesViewModel.text.observe(this, Observer {
            textView.text = it
        })*/

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        spinner = view.findViewById(R.id.history_spinner)
        val items = arrayOf("No filter", "Basketball", "Football", "Volleyball")
        val adapter = ArrayAdapter<String>(
            this.activity!!,
            android.R.layout.simple_spinner_dropdown_item,
            items
        )
        spinner.adapter = adapter

        recycler = view.findViewById(R.id.last_games_recycler_view)
        val dummyList = generateList()
        recycler.adapter = GameHistoryAdapter(dummyList)
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

    private fun generateList(): List<GameHistoryItem> {
        val list = ArrayList<GameHistoryItem>()
        val item1 = GameHistoryItem(
            "16",
            "listopad",
            "Pierwsza gra na liście",
            "Koszykówka",
            "16:00",
            "20/24"
        )
        val item2 =
            GameHistoryItem("21", "październik", "Lecimy tutej!", "Koszykówka", "16:00", "20/24")
        val item3 = GameHistoryItem("1", "grudzień", "Jan Paweł II", "Piłka nożna", "20:00", "4/8")
        val item4 = GameHistoryItem(
            "16",
            "listopad",
            "Pierwsza gra na liście",
            "Koszykówka",
            "16:00",
            "20/24"
        )
        val item5 =
            GameHistoryItem("21", "październik", "Lecimy tutej!", "Koszykówka", "16:00", "20/24")
        val item6 = GameHistoryItem("1", "grudzień", "Jan Paweł II", "Piłka nożna", "20:00", "4/8")
        val item7 = GameHistoryItem(
            "16",
            "listopad",
            "Pierwsza gra na liście",
            "Koszykówka",
            "16:00",
            "20/24"
        )
        val item8 =
            GameHistoryItem("21", "październik", "Lecimy tutej!", "Koszykówka", "16:00", "20/24")
        val item9 = GameHistoryItem("1", "grudzień", "Jan Paweł II", "Piłka nożna", "20:00", "4/8")
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