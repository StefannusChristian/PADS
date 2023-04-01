package com.example.simplequizapp
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel() {
    val userName = MutableLiveData<String>()
    var isWinQuizGame: Boolean = false
    var theGuessWord: String = ""
    var winGuessGameText: String = "successfully"
    var isWinGuessGame: Boolean = false
    var quizCount: Int = 1
    var rightAnswerCount: Int = 0
    lateinit var randomIndexes: List<Int>
    private var is_init = false

    fun setRandomIndex() {
        if (!is_init) {
            randomIndexes= (0..9).shuffled().take(10)
            is_init = true
        }
    }




}
