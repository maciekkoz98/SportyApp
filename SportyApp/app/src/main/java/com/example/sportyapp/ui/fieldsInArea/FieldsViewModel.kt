package com.example.sportyapp.ui.fieldsInArea

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class FieldsViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is share Fragment"
    }
    val text: LiveData<String> = _text
}