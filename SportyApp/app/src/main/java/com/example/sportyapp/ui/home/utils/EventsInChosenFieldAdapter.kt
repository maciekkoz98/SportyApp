package com.example.sportyapp.ui.home.utils

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.sportyapp.R
import com.example.sportyapp.data.game.Game
import kotlinx.android.synthetic.main.event_view.view.*

class EventsInChosenFieldAdapter(private val gamesList: ArrayList<Game>) :
    RecyclerView.Adapter<EventsInChosenFieldHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventsInChosenFieldHolder {
        val eventsLayout = LayoutInflater.from(parent.context).inflate(
            R.layout.event_view,
            parent,
            false
        ) as LinearLayout
        return EventsInChosenFieldHolder(eventsLayout)
    }

    override fun getItemCount() = gamesList.size

    override fun onBindViewHolder(holder: EventsInChosenFieldHolder, position: Int) {
        val game = gamesList[position]
        holder.linearLayout.event_view_event_name.text = game.name
        //TODO holder.linearLayout.event_view_sport_icon
        holder.linearLayout.event_view_sport_name.text = getSportText(holder, game.sportID)
        //TODO time
        holder.linearLayout.event_view_participants.text =
            holder.itemView.context.resources.getString(
                R.string.event_view_participants,
                game.players.size.toString(),
                "34"
            )
    }

    private fun getSportText(holder: EventsInChosenFieldHolder, sportID: Long): String {
        return when (sportID) {
            1L -> holder.itemView.context.resources.getString(R.string.basketball)
            2L -> holder.itemView.context.resources.getString(R.string.football)
            else -> "Unknown"
        }
    }

}