package com.example.sportyapp.ui.fieldsInArea

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.sportyapp.R

class FieldsFragment : Fragment() {

    private lateinit var fieldsViewModel: FieldsViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fieldsViewModel =
            ViewModelProviders.of(this).get(FieldsViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_fields, container, false)
        val textView: TextView = root.findViewById(R.id.text_share)
        fieldsViewModel.text.observe(this, Observer {
            textView.text = it
        })
        return root
    }
}