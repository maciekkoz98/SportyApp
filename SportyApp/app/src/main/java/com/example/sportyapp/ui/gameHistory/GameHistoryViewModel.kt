package com.example.sportyapp.ui.gameHistory

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class GameHistoryViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is history screen."
    }
    val text: LiveData<String> = _text
}