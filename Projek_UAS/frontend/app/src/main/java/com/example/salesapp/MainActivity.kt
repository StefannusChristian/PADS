package com.example.salesapp
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.salesapp.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var bottomNavView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        bottomNavView = binding.bottomNavView
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_container) as NavHostFragment
        val navController = navHostFragment.navController
        bottomNavView.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.homePageFragment) {
                bottomNavView.visibility = View.VISIBLE
            } else {
                bottomNavView.visibility = View.GONE
            }
        }

        bottomNavView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.cartFragmentItem -> {
                    navController.navigate(R.id.cartFragment)
                    true
                }
                R.id.customersFragmentItem ->{
                    navController.navigate(R.id.customerFragment)
                    true
                }
                R.id.transactionFragmentItem ->{
                    navController.navigate(R.id.transactionFragment)
                    true
                }
                R.id.inventoryFragmentItem ->{
                    navController.navigate(R.id.inventoryFragment)
                    true
                }
                else -> false
            }
        }
    }
}