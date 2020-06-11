package com.example.sportyapp.ui.gameHistory.utils

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.sportyapp.R
import com.example.sportyapp.data.game.Game
import kotlinx.android.synthetic.main.fragment_mygames_list_element.view.*
import java.util.*

class GameHistoryAdapter(private val myGamesList: List<Game>) :
    RecyclerView.Adapter<GameHistoryAdapter.GameHistoryViewHolder>() {

    class GameHistoryViewHolder(itemView: ConstraintLayout) : RecyclerView.ViewHolder(itemView) {
        val gameDate: TextView = itemView.list_elem_date
        val gameMonth: TextView = itemView.list_elem_month
        val gameName: TextView = itemView.list_elem_name
        val gameHour: TextView = itemView.list_elem_hour
        val gamePlayers: TextView = itemView.list_elem_players
        val gameSport: TextView = itemView.list_elem_sport
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameHistoryViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_mygames_list_element, parent, false) as ConstraintLayout

        return GameHistoryViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: GameHistoryViewHolder, position: Int) {
        val currentItem = myGamesList[position]
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = currentItem.date
        holder.gameDate.text = calendar.get(Calendar.DAY_OF_MONTH).toString()
        holder.gameMonth.text =
            calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault())
        setSportIcon(holder, currentItem.sport.nameEN)
        holder.gameName.text = currentItem.name
        holder.gameSport.text = getSportName(currentItem.sport.nameEN, holder)
        holder.gameHour.text = holder.itemView.context.resources.getString(
            R.string.event_view_start_time,
            calendar.get(Calendar.HOUR_OF_DAY).toString(),
            calendar.get(Calendar.MINUTE).toString()
        )
        holder.gamePlayers.text = holder.itemView.context.resources.getString(
            R.string.event_view_participants,
            currentItem.players.size.toString(),
            currentItem.maxPlayers.toString()
        )
    }

    override fun getItemCount() = myGamesList.size

    private fun getSportName(sportName: String, holder: GameHistoryViewHolder): String {
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

    private fun setSportIcon(holder: GameHistoryViewHolder, sportName: String) {
        when (sportName) {
            "Basketball" -> holder.itemView.list_elem_icon.setImageResource(R.drawable.ic_basketball_svg)
            "Football" -> holder.itemView.list_elem_icon.setImageResource(R.drawable.ic_football)
            "Volleyball" -> holder.itemView.list_elem_icon.setImageResource(R.drawable.ic_volleyball)
            "Handball" -> holder.itemView.list_elem_icon.setImageResource(R.drawable.ic_handball)
            "Badminton" -> holder.itemView.list_elem_icon.setImageResource(R.drawable.ic_badminton)
            "Tennis" -> holder.itemView.list_elem_icon.setImageResource(R.drawable.ic_tennis)
            "Table tennis" -> holder.itemView.list_elem_icon.setImageResource(R.drawable.ic_table_tennis)
        }
    }
}


