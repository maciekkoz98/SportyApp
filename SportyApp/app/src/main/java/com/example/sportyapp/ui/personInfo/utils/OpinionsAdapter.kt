package com.example.sportyapp.ui.personInfo.utils

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.sportyapp.R

class OpinionsAdapter : RecyclerView.Adapter<OpinionsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OpinionsViewHolder {

        val layoutInflater = LayoutInflater.from(parent.context)
        val opinionRow = layoutInflater.inflate(R.layout.opinions_row, parent, false)
        return OpinionsViewHolder(opinionRow)
    }

    override fun getItemCount(): Int {
        return 2
    }

    override fun onBindViewHolder(holder: OpinionsViewHolder, position: Int) {

    }

}


class OpinionsViewHolder(view: View) : RecyclerView.ViewHolder(view)