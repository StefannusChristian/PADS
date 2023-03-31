package com.example.simplequizapp

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.example.simplequizapp.databinding.FragmentGuessGameBinding
import kotlin.random.Random

class GuessGameFragment : Fragment() {
    private lateinit var binding: FragmentGuessGameBinding
    private lateinit var viewModel: SharedViewModel
    private var randomWordList = listOf("PYTHON","TENSORFLOW","PANDAS","NUMPY","MATPLOTLIB","RUBY","DATAMINING","ANGULAR","DJANGO","RESTAPI")

    var correctGuess = 0
    var lives = 5
    private val goToResult = GuessGameFragmentDirections.actionGuessGameFragmentToGuessResultFragment()
    private val wrongLetterList = mutableListOf<Char>()
    private val correctLetterList = mutableListOf<Char>()
    @SuppressLint("SetTextI18n")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGuessGameBinding.inflate(inflater, container, false)
        val view = binding.root
        // Make a random Index variable that contains a random
        val n = randomWordList.size
        val randomIndex = Random.nextInt(0,n)

        // Choose a secretWord from the secretWordList by indexing the secretWordList with a randomIndex
        val randomWordtoGuess = randomWordList[randomIndex]

        viewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]

        binding.hangman.text = "_" + " _".repeat(randomWordtoGuess.length - 1)
        val guessLetters = binding.userGuess
        guessLetters.text = " ".repeat(randomWordtoGuess.length * 2 - 1)

        var correctGuess = savedInstanceState?.getInt("curr_corr_guess") ?: 0
        var lives = savedInstanceState?.getInt("curr_lives") ?: 5

        binding.guessButton.setOnClickListener {
            val randomWordArr = randomWordtoGuess.toCharArray()
            val answerViewCharArr = guessLetters.text.toString().toCharArray()
            val userGuess = binding.editTextLetterInput.text.toString().uppercase()
//            Guess Button is click when user hasn't input anything
            if (userGuess.isEmpty()) {
                Toast.makeText(context, "You Have Not Input A Letter!", Toast.LENGTH_SHORT).show()
            }
//            Guess the wrong letter more than once
            else if (wrongLetterList.contains(userGuess.first())) {
                Toast.makeText(context, "You Have Already Guess That Wrong Letter!", Toast.LENGTH_SHORT).show()
            }
            else if(correctLetterList.contains(userGuess.first())) {
                Toast.makeText(context, "You Have Already Guess This Letter!", Toast.LENGTH_SHORT).show()
            }
//            Correct Guess
            else if (randomWordtoGuess.contains(userGuess)) {
                Toast.makeText(context, "Correct Letter!", Toast.LENGTH_SHORT).show()
                for (i in randomWordtoGuess.indices) {
                    if (randomWordArr[i] == userGuess.first()) {
                        answerViewCharArr[i * 2] = userGuess.first()
                        correctLetterList.add(userGuess.first())
                        correctGuess++
                    }
                }
                guessLetters.text = String(answerViewCharArr)
//                Done Guessing is checked when the correctGuess variable is the same as the word length
                if (correctGuess == randomWordtoGuess.length) {
//                    Navigate to Guess Game Result Fragment With Win Message
                    viewModel.isWinGuessGame = true
                    val navController = view.findNavController()
                    navController.navigate(goToResult)
                }
            } else {
                Toast.makeText(context, "Oops, Wrong Guess!", Toast.LENGTH_SHORT).show()
                val guessChar = userGuess.first()
                if (!wrongLetterList.contains(guessChar)) {
                    binding.wrongLetters.text = binding.wrongLetters.text.toString() + guessChar + " "
                    wrongLetterList.add(guessChar)
                    lives--
                    binding.remainingGuesses.text = lives.toString()
                }
                if (lives == 0) {
                    val navController = view.findNavController()
                    viewModel.isWinGuessGame = false
                    // Navigate to Guess Game Result Fragment with Fail Message
                    navController.navigate(goToResult)
                }
            }
                binding.editTextLetterInput.text.clear()
        }
            return view
    }
}