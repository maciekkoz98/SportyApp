package com.example.sportyapp.ui.game.utils

import android.view.LayoutInflater
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.sportyapp.R
import kotlinx.android.synthetic.main.player_list_row.view.*

class PlayersListAdapter(private val usersList: List<GameItem>) : RecyclerView.Adapter<PlayersListAdapter.PlayersListViewHolder>() {

    class PlayersListViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val gameUsername: TextView = itemView.game_username
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayersListViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.fragment_mygames_list_element, parent, false)
        return PlayersListViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: PlayersListViewHolder, position: Int) {
        val currentItem = usersList[position]
        holder.gameUsername.text = currentItem.username
    }

    override fun getItemCount() = usersList.size

}