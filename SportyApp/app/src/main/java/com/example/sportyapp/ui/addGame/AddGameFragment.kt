package com.example.sportyapp.ui.addGame

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.sportyapp.R

class AddGameFragment : Fragment() {

    private lateinit var addGameViewModel: AddGameViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        addGameViewModel =
            ViewModelProviders.of(this).get(AddGameViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_add_game, container, false)
        val textView: TextView = root.findViewById(R.id.text_send)
        addGameViewModel.text.observe(this, Observer {
            textView.text = it
        })
        return root
    }
}