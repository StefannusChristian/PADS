package com.example.simplequizapp

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.example.simplequizapp.databinding.FragmentMainPageBinding

class MainPageFragment : Fragment() {
    private lateinit var binding: FragmentMainPageBinding
    private lateinit var viewModel: SharedViewModel
    val TAG = "MainActivity"
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        binding = FragmentMainPageBinding.inflate(inflater, container, false)
        val view = binding.root
        val userNameTv = binding.userNametv
        viewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]
        userNameTv.text = viewModel.userName.value
        binding.quizBtn.setOnClickListener{
            val navController = view.findNavController()
            navController.navigate(R.id.action_mainPageFragment_to_quizQuestionsFragment)
        }

        binding.goToGuessGameBtn.setOnClickListener{
            val navController = view.findNavController()
            navController.navigate(R.id.action_mainPageFragment_to_guessGameFragment)
        }

        return view
    }

}