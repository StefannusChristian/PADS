package com.example.salesapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.salesapp.databinding.FragmentCustomerBinding
import com.example.salesapp.databinding.MainToolbarBinding

class CustomerFragment : Fragment() {

    private lateinit var binding: FragmentCustomerBinding
    private lateinit var customerViewModel: CustomerViewModel
    private lateinit var toolBarBinding: MainToolbarBinding
    private val customerAdapter:  CustomerAdapter by lazy { CustomerAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCustomerBinding.inflate(inflater, container, false)
        toolBarBinding = MainToolbarBinding.bind(binding.root.findViewById(R.id.mainToolbar))
        customerViewModel = ViewModelProvider(this)[CustomerViewModel::class.java]

        setupRecyclerViews()
        observeCustomerList()

        customerViewModel.fetchCustomers()

        toolBarBinding.toolbarBtn.setOnClickListener {
            findNavController().navigate(R.id.homePageFragment)
        }

        return binding.root
    }

    private fun setupRecyclerViews() {
        binding.rvCustomer.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = customerAdapter
        }
    }

    private fun observeCustomerList() {
        customerViewModel.customers.observe(viewLifecycleOwner) { customerList ->
            customerList?.let {
                customerAdapter.setCustomers(it)
            }
        }
    }

}