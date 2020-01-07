package com.example.sportyapp.ui.personInfo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PersonInfoViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is tools Fragment"
    }
    val text: LiveData<String> = _text
}