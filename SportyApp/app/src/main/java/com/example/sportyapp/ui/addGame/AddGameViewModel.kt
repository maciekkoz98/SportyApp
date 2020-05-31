package com.example.sportyapp.ui.addGame

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AddGameViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is send Fragment"
    }
    val text: LiveData<String> = _text
}