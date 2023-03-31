package com.example.simplequizapp
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel() {
    val userName = MutableLiveData<String>()
    var isWinQuizGame: Boolean = false
    var theGuessWord: String = ""
    var winGuessGameText: String = "successfully"
    var isWinGuessGame: Boolean = false
}
