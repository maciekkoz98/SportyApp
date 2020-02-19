package com.example.sportyapp.ui.myGames.utils

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.sportyapp.R

class MyGamesListAdapter : RecyclerView.Adapter<MyGamesListViewHolder>() {

    /*private var customItemCount: Int = 2
        set(value) {
            if (value > 0) {
                field = value
            }
        }*/

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyGamesListViewHolder {

        val layoutInflater = LayoutInflater.from(parent.context)
        val myGamesListRow = layoutInflater.inflate(R.layout.last_games_row, parent, false)
        return MyGamesListViewHolder(myGamesListRow)
    }

    override fun getItemCount(): Int {
        return 3
    }

    override fun onBindViewHolder(holder: MyGamesListViewHolder, position: Int) {

    }

}


class MyGamesListViewHolder(view: View) : RecyclerView.ViewHolder(view)