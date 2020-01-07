package com.example.sportyapp.ui.myGames

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MyGamesViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is gallery Fragment"
    }
    val text: LiveData<String> = _text
}