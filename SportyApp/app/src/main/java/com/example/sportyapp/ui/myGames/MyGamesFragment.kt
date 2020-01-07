package com.example.sportyapp.ui.myGames

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.sportyapp.R

class MyGamesFragment : Fragment() {

    private lateinit var myGamesViewModel: MyGamesViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        myGamesViewModel =
            ViewModelProviders.of(this).get(MyGamesViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_my_games, container, false)
        val textView: TextView = root.findViewById(R.id.text_gallery)
        myGamesViewModel.text.observe(this, Observer {
            textView.text = it
        })
        return root
    }
}