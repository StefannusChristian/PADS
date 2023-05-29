package com.example.salesapp.SignUp

import android.os.Bundle
import android.text.InputFilter
import android.text.Spanned
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import com.example.salesapp.R
import com.example.salesapp.databinding.SignUpFragmentBinding

class SignUpFragment : Fragment() {
    private lateinit var binding: SignUpFragmentBinding
    private val viewModel: SignUpViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = SignUpFragmentBinding.inflate(inflater, container, false)
        val view = binding.root

        setupViews(view)
        observeRegistrationStatus()

        return view
    }

    private fun setupViews(view: View) {
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

        // Apply the input filter to the username and password fields
        binding.username.filters = arrayOf(inputFilter)
        binding.password.filters = arrayOf(inputFilter)
        binding.name.filters = arrayOf(inputFilter)
        binding.email.filters = arrayOf(inputFilter)
    }

    private fun observeRegistrationStatus() {
        viewModel.regisStat.observe(viewLifecycleOwner, Observer { stats ->
            if (stats == "-") {
                val navController = binding.root.findNavController()
                navController.navigate(R.id.action_signUpFragment_to_loginFragment)
            } else {
                Toast.makeText(requireContext(), "Registration failed", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
