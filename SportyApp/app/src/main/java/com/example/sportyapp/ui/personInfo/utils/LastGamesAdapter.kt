package com.example.sportyapp.ui.personInfo.utils

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.sportyapp.R

class LastGamesAdapter : RecyclerView.Adapter<LastGamesViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LastGamesViewHolder {

        val layoutInflater = LayoutInflater.from(parent.context)
        val lastGamesRow = layoutInflater.inflate(R.layout.last_games_row, parent, false)
        return LastGamesViewHolder(lastGamesRow)
    }

    override fun getItemCount(): Int {
        return 2
    }

    override fun onBindViewHolder(holder: LastGamesViewHolder, position: Int) {

    }

}


class LastGamesViewHolder(view: View) : RecyclerView.ViewHolder(view)