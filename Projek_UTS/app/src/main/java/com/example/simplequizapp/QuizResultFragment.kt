package com.example.simplequizapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
        val score = QuizResultFragmentArgs.fromBundle(requireArguments()).rightAnswerCount
        binding.resultLabel.text = getString(R.string.result_score,score)

        binding.tryAgainBtn.setOnClickListener{
            val navController = view.findNavController()
            val goQuiz = QuizResultFragmentDirections.actionQuizResultFragmentToQuizQuestionsFragment()
            navController.navigate(goQuiz)
        }
        return view
    }
}