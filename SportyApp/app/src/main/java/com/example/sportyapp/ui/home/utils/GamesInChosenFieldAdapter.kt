package com.example.sportyapp.ui.home.utils

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.sportyapp.R
import com.example.sportyapp.data.game.Game
import kotlinx.android.synthetic.main.game_view.view.*
import java.sql.Date
import java.sql.Timestamp
import java.util.*

class GamesInChosenFieldAdapter(private val gamesList: ArrayList<Game>) :
    RecyclerView.Adapter<GamesInChosenFieldHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GamesInChosenFieldHolder {
        val eventsLayout = LayoutInflater.from(parent.context).inflate(
            R.layout.game_view,
            parent,
            false
        ) as LinearLayout
        return GamesInChosenFieldHolder(eventsLayout)
    }

    override fun getItemCount() = gamesList.size

    override fun onBindViewHolder(holder: GamesInChosenFieldHolder, position: Int) {
        val game = gamesList[position]
        holder.linearLayout.event_view_event_name.text = game.name
        setSportIcon(holder, game.sport.nameEN)
        holder.linearLayout.event_view_sport_name.text = getSportText(holder, game.sport.nameEN)
        val stamp = Timestamp(game.date)
        val date = Date(stamp.time)
        val cal = Calendar.getInstance()
        cal.time = date
        holder.linearLayout.event_view_time.text = holder.itemView.context.resources.getString(
            R.string.event_view_start_time,
            cal.get(Calendar.HOUR_OF_DAY).toString(),
            cal.get(Calendar.MINUTE).toString()
        )
        holder.linearLayout.event_view_date_month.text =
            cal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault())!!
                .subSequence(0, 3)
        holder.linearLayout.event_view_date_number.text = cal.get(Calendar.DAY_OF_MONTH).toString()
        holder.linearLayout.event_view_participants.text =
            holder.itemView.context.resources.getString(
                R.string.event_view_participants,
                game.players.size.toString(),
                game.maxPlayers.toString()
            )
    }

    private fun setSportIcon(holder: GamesInChosenFieldHolder, sportName: String) {
        when (sportName) {
            "Basketball" -> holder.linearLayout.event_view_sport_icon.setImageResource(R.drawable.ic_basketball_svg)
            "Football" -> holder.linearLayout.event_view_sport_icon.setImageResource(R.drawable.ic_football)
            "Volleyball" -> holder.linearLayout.event_view_sport_icon.setImageResource(R.drawable.ic_volleyball)
            "Handball" -> holder.linearLayout.event_view_sport_icon.setImageResource(R.drawable.ic_handball)
            "Badminton" -> holder.linearLayout.event_view_sport_icon.setImageResource(R.drawable.ic_badminton)
            "Tennis" -> holder.linearLayout.event_view_sport_icon.setImageResource(R.drawable.ic_tennis)
            "Table tennis" -> holder.linearLayout.event_view_sport_icon.setImageResource(R.drawable.ic_table_tennis)
        }
    }

    private fun getSportText(holder: GamesInChosenFieldHolder, sportName: String): String {
        return when (sportName) {
            "Basketball" -> holder.itemView.context.resources.getString(R.string.basketball)
            "Football" -> holder.itemView.context.resources.getString(R.string.football)
            "Volleyball" -> holder.itemView.context.resources.getString(R.string.volleyball)
            "Handball" -> holder.itemView.context.getString(R.string.handball)
            "Badminton" -> holder.itemView.context.resources.getString(R.string.badminton)
            "Tennis" -> holder.itemView.context.resources.getString(R.string.tennis)
            "Table tennis" -> holder.itemView.context.resources.getString(R.string.table_tennis)
            else -> "Unknown"
        }
    }

}