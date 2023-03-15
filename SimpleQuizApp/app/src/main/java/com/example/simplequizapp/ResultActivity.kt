package com.example.simplequizapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.simplequizapp.databinding.ActivityResultBinding

class ResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResultBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val score = intent.getIntExtra("RIGHT_ANSWER_COUNT",0)
        binding.resultLabel.text = getString(R.string.result_score,score)
        binding.tryAgainBtn.setOnClickListener{
            startActivity(Intent(this@ResultActivity,MainActivity::class.java))
        }


    }
}