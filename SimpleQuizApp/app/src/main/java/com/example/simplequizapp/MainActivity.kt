package com.example.simplequizapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import com.example.simplequizapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var rightAnswerCount:Int = 0
    private var quizCount:Int = 1
    private var rightAnswer: Boolean? = null
    private val QUIZ_COUNT:Int = 10
    private val quizData = mutableListOf(
        mutableListOf("Indonesia is a country located in Southeast Asia.", true),
        mutableListOf("The official language of Indonesia is Malay.", false),
        mutableListOf("Indonesia has the largest Muslim population in the world.", true),
        mutableListOf("The capital of Indonesia is Jakarta.", true),
        mutableListOf("The currency of Indonesia is the rupiah.", true),
        mutableListOf("Indonesia has over 17,000 islands.", true),
        mutableListOf("Bali is a province of Indonesia.", true),
        mutableListOf("The Borobudur temple is located in Bali.", false),
        mutableListOf("The highest mountain in Indonesia is Mount Bromo.", false),
        mutableListOf("Indonesia is the world's fourth most populous country.", true)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        showNextQuiz()
        quizData.shuffle()
    }

    fun showNextQuiz() {
        val quiz = quizData[0]
        binding.questionLabel.text = quiz[0].toString()
        rightAnswer = quiz[1].toString().toBoolean()
        quiz.removeAt(0)
        quiz.shuffle()
        quizData.removeAt(0)
        binding.countLabel.text = getString(R.string.count_label,quizCount)
    }

    fun checkAnswer(view: View){
        val answerBtn: Button = findViewById<Button>(view.id)
        val btnText = answerBtn.text.toString().toBoolean()

        val alertTitle:String
        if(btnText == rightAnswer){
            alertTitle = "Correct!"
            rightAnswerCount++
        }else{
            alertTitle="Wrong!"
        }
        AlertDialog.Builder(this)
            .setTitle(alertTitle)
            .setMessage("Answer: $rightAnswer")
            .setPositiveButton("OK"){
                dialogInterface,i->checkQuizCount()
            }.setCancelable(false).show()
    }

    fun checkQuizCount(){
        if (quizCount == QUIZ_COUNT){
            val intent = Intent(this@MainActivity, ResultActivity::class.java)
            intent.putExtra("RIGHT_ANSWER_COUNT",rightAnswerCount)
            startActivity(intent)
        }else{
            quizCount++
            showNextQuiz()
        }
    }


}