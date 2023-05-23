package com.example.salesapp

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.salesapp.databinding.AddCustomerPopupBinding
import com.example.salesapp.databinding.FragmentCustomerBinding
import com.example.salesapp.databinding.MainToolbarBinding

class CustomerFragment : Fragment(), CustomerAdapter.OnItemClickCallback {

    private lateinit var binding: FragmentCustomerBinding
    private lateinit var customerViewModel: CustomerViewModel
    private lateinit var toolBarBinding: MainToolbarBinding
    private lateinit var addCustomerBinding: AddCustomerPopupBinding
    private val customerAdapter: CustomerAdapter by lazy { CustomerAdapter(customerViewModel) }
    private val customerTag = "CustomerFragment"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCustomerBinding.inflate(inflater, container, false)
        toolBarBinding = MainToolbarBinding.bind(binding.root.findViewById(R.id.mainToolbar))
        customerViewModel = ViewModelProvider(this)[CustomerViewModel::class.java]

        setupRecyclerViews()
        observeCustomerList()

        customerAdapter.setOnItemClickCallback(this)

        customerViewModel.fetchCustomers()

        val sortBtn = binding.customerSortbtn
        sortBtn.setOnClickListener {
            showSortDialog()
        }

        toolBarBinding.toolbarBtn.setOnClickListener {
            findNavController().navigate(R.id.homePageFragment)
        }

        val addCustomerBtn = binding.addCustomerBtn
        addCustomerBtn.setOnClickListener {
            Log.d(customerTag, "Button Di Pencet!")
            showAddDialog()
        }

        return binding.root
    }

    override fun onUnsubscribeClicked(salesUsername: String, customerName: String) {
        customerViewModel.unsubscribeCustomer(PatchCustomerRequest(salesUsername, customerName))
    }

    private fun showSortDialog() {
        val options = arrayOf("Name", "Address")

        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Sort By")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> sortByName()
                    1 -> sortByAddress()
                }
            }
            .show()
    }

    private fun showAddDialog() {
        val dialog = Dialog(requireContext())
        val addCustomerBinding = AddCustomerPopupBinding.inflate(dialog.layoutInflater)
        dialog.setContentView(addCustomerBinding.root)

        val addBtn = addCustomerBinding.addBtn
        addBtn.setOnClickListener {
            val username = addCustomerBinding.customerUsername.text.toString()
            val address = addCustomerBinding.customerAddress.text.toString()
            val imageLink = addCustomerBinding.customerImageLink.text.toString()
            val newCustomer = PostCustomerRequest(
                sales_username = customerViewModel.salesUsername,
                customer_username = username,
                customer_address = address,
                customer_img_link = imageLink
            )
            Log.d(customerTag, newCustomer.toString())
            customerViewModel.addCustomer(newCustomer)
            Log.d(customerTag, "ADD BUTTON DIPENCET!")
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun sortByName() {
        customerViewModel.sortCustomersByName()
    }

    private fun sortByAddress() {
        customerViewModel.sortCustomersByAddress()
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
