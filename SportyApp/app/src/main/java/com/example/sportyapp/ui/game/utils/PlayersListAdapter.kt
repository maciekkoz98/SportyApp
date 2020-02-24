package com.example.sportyapp.ui.game.utils

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.sportyapp.R

class PlayersListAdapter : RecyclerView.Adapter<PlayersListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayersListViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val playersListRow = layoutInflater.inflate(R.layout.players_list_row, parent, false)
        return PlayersListViewHolder(playersListRow)
    }

    override fun getItemCount(): Int {
        return 10
    }

    override fun onBindViewHolder(holder: PlayersListViewHolder, position: Int) {

    }

}


class PlayersListViewHolder(view: View) : RecyclerView.ViewHolder(view)