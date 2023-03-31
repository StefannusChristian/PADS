package com.example.simplequizapp

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.example.simplequizapp.databinding.FragmentGuessResultBinding

class GuessResultFragment : Fragment() {
    private lateinit var binding: FragmentGuessResultBinding
    private lateinit var viewModel: SharedViewModel
    val TAG = "MainActivity"
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGuessResultBinding.inflate(inflater, container, false)
        val view = binding.root
        viewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]
        val isWin = viewModel.isWinGuessGame
        Log.d(TAG,isWin.toString()+"INI IS WIN")
        if (!isWin) {
            viewModel.winGuessGameText = "failed"
        } else{
            viewModel.winGuessGameText = "successfully"
        }
        binding.winLoseGuessGame.text = getString(R.string.winOrLoseGuessGameText,viewModel.winGuessGameText)
        binding.theCorrectWord.text = getString(R.string.the_correct_word_is_s,viewModel.theGuessWord)
        binding.guessGameTryAgain.setOnClickListener{
            val navController = view.findNavController()
            navController.navigate(R.id.action_guessResultFragment_to_guessGameFragment)
        }

        val imageView = binding.winLoseImageGuess
        if (isWin) {
            imageView.setImageResource(R.drawable.won)
        }

        return view
    }

}