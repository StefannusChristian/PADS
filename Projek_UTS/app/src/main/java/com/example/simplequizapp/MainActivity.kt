package com.example.simplequizapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.simplequizapp.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.navigation.fragment.findNavController

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: SharedViewModel
    private var currentToast: Toast? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        viewModel = ViewModelProvider(this)[SharedViewModel::class.java]
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_container) as NavHostFragment
        val navController = navHostFragment.navController
        val bottomNavView = findViewById<BottomNavigationView>(R.id.bottom_nav_view)
        bottomNavView.setupWithNavController(navController)
        binding.bottomNavView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.homeFragment -> {
                    // Navigate to the home destination
                    navHostFragment.findNavController().navigate(R.id.enterNameFragment)
                    true
                }
                R.id.chooseGame -> {
                    // Check if userName is empty
                    if (viewModel.userName.value?.isEmpty() == true) {
                        // Show a toast message
                        if (currentToast != null) {
                            currentToast!!.cancel()
                        }
                        val errorMsg = this.getString(R.string.emptyName)
                        currentToast = Toast.makeText(this, errorMsg, Toast.LENGTH_SHORT)
                        currentToast!!.show()
                        false // Do not navigate to the Choose Game Destination
                    } else {
                        // Navigate to the Choose Game Destination
                        navHostFragment.findNavController().navigate(R.id.mainPageFragment)
                        true
                    }
                }
                // Handle other menu items if needed
                else -> false
            }
        }

    }

}