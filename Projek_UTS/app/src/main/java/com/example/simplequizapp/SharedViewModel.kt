package com.example.simplequizapp
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel() {
    val userName = MutableLiveData<String>()
}
