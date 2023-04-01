package com.example.simplequizapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.example.simplequizapp.databinding.FragmentQuizResultBinding

class QuizResultFragment : Fragment() {
    private lateinit var binding: FragmentQuizResultBinding
    private lateinit var viewModel: SharedViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentQuizResultBinding.inflate(inflater,container,false)
        val view = binding.root
        viewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]
        binding.resultLabel.text = getString(R.string.result_score,viewModel.rightAnswerCount)

        binding.tryAgainBtn.setOnClickListener{
            val navController = view.findNavController()
            val goQuiz = QuizResultFragmentDirections.actionQuizResultFragmentToQuizQuestionsFragment()
            navController.navigate(goQuiz)
            viewModel.rightAnswerCount = 0
        }

        val imageView = binding.winLoseImage

        var youWinString = "YOU LOST!"
        if (viewModel.isWinQuizGame) {
            youWinString = "YOU WIN!"
            imageView.setImageResource(R.drawable.won)
        }
        binding.resultTitleID.text = getString(R.string.result_title, youWinString)
        return view
    }

}