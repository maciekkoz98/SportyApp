package com.example.sportyapp.ui.personInfo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.sportyapp.R

class PersonInfoFragment : Fragment() {

    private lateinit var personInfoViewModel: PersonInfoViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        personInfoViewModel =
            ViewModelProviders.of(this).get(PersonInfoViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_person_info, container, false)
        val textView: TextView = root.findViewById(R.id.text_tools)
        personInfoViewModel.text.observe(this, Observer {
            textView.text = it
        })
        return root
    }
}