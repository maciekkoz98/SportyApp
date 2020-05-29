package com.example.sportyapp.ui.myGames

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.example.sportyapp.R


class MyGamesFragment : Fragment() {

    private lateinit var myGamesViewModel: MyGamesViewModel
    private lateinit var spinner: Spinner

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        myGamesViewModel =
            ViewModelProviders.of(this).get(MyGamesViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_my_games, container, false)
        /*val textView: TextView = root.findViewById(R.id.text_gallery)
        myGamesViewModel.text.observe(this, Observer {
            textView.text = it
        })*/
        return root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        spinner = view.findViewById(R.id.myGames_spinner)
        val items = arrayOf("Basketball", "Football", "Volleyball")
        val adapter = ArrayAdapter<String>(this.activity!!, android.R.layout.simple_spinner_item, items)
        spinner.adapter=adapter
    }
}