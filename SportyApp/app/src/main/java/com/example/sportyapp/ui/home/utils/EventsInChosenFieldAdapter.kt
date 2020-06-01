package com.example.sportyapp.ui.home.utils

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.sportyapp.R

class EventsInChosenFieldAdapter : RecyclerView.Adapter<EventsInChosenFieldHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventsInChosenFieldHolder {
        val eventsLayout = LayoutInflater.from(parent.context).inflate(
            R.layout.event_view,
            parent,
            false
        ) as LinearLayout
        return EventsInChosenFieldHolder(eventsLayout)
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: EventsInChosenFieldHolder, position: Int) {
        TODO("Not yet implemented")
    }

}