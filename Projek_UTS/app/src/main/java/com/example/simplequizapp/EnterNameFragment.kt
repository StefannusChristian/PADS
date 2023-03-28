package com.example.simplequizapp

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.example.simplequizapp.databinding.FragmentEnterNameBinding

class EnterNameFragment : Fragment() {
    private lateinit var binding: FragmentEnterNameBinding
    private lateinit var viewModel: SharedViewModel
    private var currentToast: Toast? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEnterNameBinding.inflate(inflater, container, false)
        val view = binding.root

        // Set the default text for currUserNameTv
        binding.currUserName?.text = getString(R.string.current_username_none)

        binding.gameStartBtn.setOnClickListener{
            val navController = view.findNavController()
            val userName = binding.etName.text.toString()

            // Show Error message when the username is empty to prevent the user to
            // to to the next page before entering the username
            if (userName.isEmpty()) {
                if (currentToast != null) {
                    currentToast!!.cancel()
                }
                val errorMsg = requireContext().getString(R.string.emptyName)
                currentToast = Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT)
                currentToast!!.show()
                return@setOnClickListener
            }

            viewModel.userName.value = userName

            val goToQuizGame = EnterNameFragmentDirections.actionEnterNameFragmentToMainPageFragment()
            navController.navigate(goToQuizGame)
        }

        // Observe the viewModel.userName property for changes
        viewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]
        viewModel.userName.observe(viewLifecycleOwner) { name ->
            val currUserNameTv = binding.currUserName
            if (name.isNotEmpty()) {
                currUserNameTv?.text = getString(R.string.current_username, name)
            } else {
                currUserNameTv?.text = getString(R.string.current_username_none)
            }
        }

        return view
    }
}

