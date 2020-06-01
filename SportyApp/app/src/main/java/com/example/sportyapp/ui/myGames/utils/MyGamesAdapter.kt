package com.example.sportyapp.ui.myGames.utils

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.view.menu.ActionMenuItemView
import androidx.recyclerview.widget.RecyclerView
import com.example.sportyapp.R
import kotlinx.android.synthetic.main.fragment_mygames_list_element.view.*

class MyGamesAdapter(private val myGamesList: List<MyGamesItem>) : RecyclerView.Adapter<MyGamesAdapter.MyGamesListViewHolder>() {

    var onGameClicked: ((pos: Int) -> Unit)? = null

    class MyGamesListViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val gameDate: TextView = itemView.list_elem_date
        val gameMonth: TextView = itemView.list_elem_month
        val gameName: TextView = itemView.list_elem_name
        val gameHour: TextView = itemView.list_elem_hour
        val gamePlayers: TextView = itemView.list_elem_players
        val gameSport: TextView = itemView.list_elem_sport
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyGamesListViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.fragment_mygames_list_element, parent, false)

        return MyGamesListViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyGamesListViewHolder, position: Int) {
        val currentItem = myGamesList[position]
        holder.gameDate.text = currentItem.day
        holder.gameMonth.text = currentItem.month
        holder.gameName.text = currentItem.name
        holder.gameSport.text = currentItem.sport
        holder.gameHour.text = currentItem.hour
        holder.gamePlayers.text = currentItem.people
    }

    override fun getItemCount() = myGamesList.size
}


