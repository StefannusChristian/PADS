package com.example.salesapp.SignUp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.salesapp.R
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.example.salesapp.databinding.SignUpFragmentBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer

class SignUpFragment: Fragment() {
    private lateinit var binding : SignUpFragmentBinding
    private val viewModel : SignUpViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = SignUpFragmentBinding.inflate(layoutInflater)
        val view = binding.root

        binding.gotosignin.setOnClickListener {
            val navController = view.findNavController()
            navController.navigate(R.id.action_signUpFragment_to_loginFragment)
        }

        binding.tryregis.setOnClickListener {
            val username = binding.username.text.toString()
            val password = binding.password.text.toString()
            val name = binding.name.text.toString()
            val email = binding.email.text.toString()

            viewModel.regis(username, password, name, email)
        }

        viewModel.regisStat.observe(viewLifecycleOwner, Observer { stats ->
            if (stats == "-") {
                val navController = view.findNavController()
                navController.navigate(R.id.action_signUpFragment_to_loginFragment)
            } else {
                // Kasih Toast Message
            }
        })

        return view
    }
}