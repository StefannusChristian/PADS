package com.example.salesapp.Login

import android.os.Bundle
import android.text.InputFilter
import android.text.Spanned
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import com.example.salesapp.databinding.LoginFragmentBinding
import com.example.salesapp.R
import com.example.salesapp.SharedViewModel.SharedViewModel

class LoginFragment : Fragment() {

    private lateinit var binding: LoginFragmentBinding
    private val loginViewModel: LoginViewModel by viewModels()
    val sharedViewModel: SharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = LoginFragmentBinding.inflate(layoutInflater)
        val view = binding.root

        binding.loginButton.setOnClickListener {
            val salesUsername = binding.theUser.text.toString()
            val salesPassword = binding.thePass.text.toString()
            loginViewModel.postlogin(salesUsername, salesPassword)
        }

        binding.gotosignup.setOnClickListener {
            val navController = view.findNavController()
            navController.navigate(R.id.action_loginFragment_to_signUpFragment)
        }

        loginViewModel.verificationStatus.observe(viewLifecycleOwner, Observer { verified ->
            if (verified) {
                sharedViewModel.salesUsername = binding.theUser.text.toString()
                val navController = view.findNavController()
                navController.navigate(R.id.action_loginFragment_to_homePageFragment)
            } else {
            // Kasih Toast Salah
            }
        })

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val inputFilter = object : InputFilter {
            override fun filter(
                source: CharSequence,
                start: Int,
                end: Int,
                dest: Spanned,
                dstart: Int,
                dend: Int
            ): CharSequence {
                // Prevent new lines from being entered
                if (source.contains("\n")) {
                    return ""
                }
                return source // Accept the input as is
            }
        }

// Apply the input filter to the username and password fields, otak atik field mana yang mau difilter
        binding.theUser.filters = arrayOf(inputFilter)
        binding.thePass.filters = arrayOf(inputFilter)
    }
}