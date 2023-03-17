package com.example.simplequizapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.example.simplequizapp.databinding.FragmentEnterNameBinding

class EnterNameFragment : Fragment() {
    private lateinit var binding: FragmentEnterNameBinding
    private lateinit var viewModel: SharedViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEnterNameBinding.inflate(inflater, container, false)
        val view = binding.root
        binding.gameStartBtn.setOnClickListener{
            val navController = view.findNavController()
            val userName = binding.etName.text.toString()
            viewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]
            viewModel.userName = userName
            val goToQuizGame = EnterNameFragmentDirections.actionEnterNameFragmentToMainPageFragment(userName)
            navController.navigate(goToQuizGame)
        }


        return view
    }

}