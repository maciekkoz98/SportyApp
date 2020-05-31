package com.example.sportyapp.ui.personInfo


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.example.sportyapp.R
import com.example.sportyapp.ui.personInfo.utils.OpinionsAdapter
import kotlinx.android.synthetic.main.fragment_opinions.*


class OpinionsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val rootView = inflater.inflate(R.layout.fragment_opinions, container, false)

        val opinionsRecyclerView = rootView.findViewById(R.id.opinions_recycler_view) as RecyclerView

        opinionsRecyclerView.layoutManager = LinearLayoutManager(activity)

        opinionsRecyclerView.adapter = OpinionsAdapter()

        return rootView
    }

}
