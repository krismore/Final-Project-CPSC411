package com.example.finalproject.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class LogViewModel : ViewModel() {

    private val _selectedMood = MutableLiveData<String?>()
    val selectedMood: LiveData<String?> get() = _selectedMood

    private val _reflectionText = MutableLiveData<String>()
    val reflectionText: LiveData<String> get() = _reflectionText

    fun setMood(mood: String) {
        _selectedMood.value = mood
    }

    fun setReflection(text: String) {
        _reflectionText.value = text
    }
}
