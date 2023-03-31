package com.example.simplequizapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.example.simplequizapp.databinding.FragmentQuizQuestionsBinding

class QuizQuestionsFragment : Fragment() {

    private lateinit var viewModel: SharedViewModel
    private lateinit var binding: FragmentQuizQuestionsBinding
    private var rightAnswerCount:Int = 0
    private var quizCount:Int = 1
    private var rightAnswer: Boolean? = null
    private val QUIZ_COUNT:Int = 10
    private val quizData = mutableListOf(
        mutableListOf("Indonesia is a country located in Southeast Asia.", true),
        mutableListOf("The official language of Indonesia is Malay.", false),
        mutableListOf("Indonesia has the largest Muslim population in the world.", true),
        mutableListOf("The capital of Indonesia is not Jakarta.", false),
        mutableListOf("The currency of Indonesia is rupiah.", true),
        mutableListOf("Indonesia has over 17,000 islands.", true),
        mutableListOf("Bali is a province of Indonesia.", true),
        mutableListOf("The Borobudur temple is located in Bali.", false),
        mutableListOf("The highest mountain in Indonesia is Mount Bromo.", false),
        mutableListOf("Indonesia is the world's sixth most populous country.", false)
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val trueButton = binding.answerBtn1
        val falseButton = binding.answerBtn2

        trueButton.setOnClickListener { view ->
            checkAnswer(view, true)
        }

        falseButton.setOnClickListener { view ->
            checkAnswer(view, false)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]
        viewModel.isWinQuizGame = false
        binding = FragmentQuizQuestionsBinding.inflate(inflater, container, false)
        val view = binding.root

        quizData.shuffle()
        showNextQuiz()

        return view
    }

    private fun showNextQuiz() {
        val quiz = quizData[0]
        binding.questionLabel.text = quiz[0].toString()
        rightAnswer = quiz[1].toString().toBoolean()
        quiz.removeAt(0)
        quiz.shuffle()
        quizData.removeAt(0)
        binding.countLabel.text = getString(R.string.count_label,quizCount)
    }

    private fun checkAnswer(view: View, userAnswer: Boolean){
        val answerBtn = view as Button
        val btnText = answerBtn.text.toString().toBoolean()
        val alertTitle:String

        if (btnText == rightAnswer) {
            alertTitle="Correct!"
            rightAnswerCount++
        } else{
            alertTitle="Wrong!"
        }
        AlertDialog.Builder(requireContext())
            .setTitle(alertTitle)
            .setMessage("Answer: $rightAnswer")
            .setPositiveButton("OK"){ dialogInterface, i -> checkQuizCount() }
            .setCancelable(false)
            .show()
    }

    private fun checkQuizCount(){
        if (quizCount == QUIZ_COUNT){
            val navController = view?.findNavController()
            if (rightAnswerCount > 7){
                viewModel.isWinQuizGame = true;
            }
            val action = QuizQuestionsFragmentDirections.actionQuizQuestionsFragmentToQuizResultFragment(rightAnswerCount)
            navController?.navigate(action)
        } else{
            quizCount++
            showNextQuiz()
        }
    }


}