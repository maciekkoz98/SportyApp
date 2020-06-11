package com.example.sportyapp.ui.personInfo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import com.example.sportyapp.R

class PersonInfoFragment : Fragment() {

    private lateinit var personInfoViewModel: PersonInfoViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                view!!.findNavController().navigate(R.id.action_nav_person_to_nav_home)
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        personInfoViewModel =
            ViewModelProviders.of(this).get(PersonInfoViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_person_info, container, false)
        /*val textView: TextView = root.findViewById(R.id.text_tools)
        personInfoViewModel.text.observe(this, Observer {
            textView.text = it
        })*/

        (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.user_app_bar_title)

        return root
    }
}